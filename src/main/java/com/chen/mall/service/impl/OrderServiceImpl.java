package com.chen.mall.service.impl;

import com.chen.mall.common.Constant;
import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.chen.mall.filter.UserFilter;
import com.chen.mall.model.mapper.CartMapper;
import com.chen.mall.model.mapper.OrderItemMapper;
import com.chen.mall.model.mapper.OrderMapper;
import com.chen.mall.model.mapper.ProductMapper;
import com.chen.mall.model.pojo.Order;
import com.chen.mall.model.pojo.OrderItem;
import com.chen.mall.model.pojo.Product;
import com.chen.mall.model.requst.CreateOrderReq;
import com.chen.mall.model.vo.CartVo;
import com.chen.mall.model.vo.OrderItemVo;
import com.chen.mall.model.vo.OrderVo;
import com.chen.mall.service.CartService;
import com.chen.mall.service.OrderService;
import com.chen.mall.service.UserService;
import com.chen.mall.util.OrderCodeFactory;
import com.chen.mall.util.QRCodeGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private UserService userService;

    @Value("${file.upload.ip}")
    private String ip;



    // ???????????????  ?????????????????????????????????
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq){
        // ????????????id
        Integer userId = UserFilter.currentUser.getId();
        // ???????????????????????????????????????
        List<CartVo> cartVoList = cartService.getCartList(userId);
        ArrayList<CartVo> cartVoListTemp = new ArrayList<CartVo>();
        for (CartVo cartVo : cartVoList) {
            if (cartVo.getSelected().equals(Constant.Cart.CHECK)){
                cartVoListTemp.add(cartVo);
            }
        }
        cartVoList = cartVoListTemp;
        if (CollectionUtils.isEmpty(cartVoList)){
            throw new BusinessException(ExceptionEnum.CART_EMPTY);
        }
        // ???????????????????????????????????????????????????
        validSaleStatusAndStock(cartVoList);
        // ??????????????????????????????item??????
        List<OrderItem> orderItemList = cartVoListToOrderItemList(cartVoList);
        // ?????????
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0){
                throw new BusinessException(ExceptionEnum.NOT_ENOUGH_STOCK);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        // ???????????????????????????????????????
        cleanCart(cartVoList);
        // ????????????
        Order order = new Order();
        // ???????????????
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        // ?????????order??????
        orderMapper.insertSelective(order);
        // ???????????????????????????order_item??????
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        // ????????????
        return orderNo;
    }

    @Override
    public OrderVo detail(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            throw new BusinessException(ExceptionEnum.NO_ORDER);
        }
        // ?????????????????????????????????
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)){
            throw new BusinessException(ExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVo orderVo = getOrderVo(order);
        return orderVo;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize){
        Integer userId = UserFilter.currentUser.getId();
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVo> orderVoList = orderListToOrderVoList(orderList);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(orderVoList);
        return pageInfo;

    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVo> orderVoList = orderListToOrderVoList(orderList);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(orderVoList);
        return pageInfo;
    }

    @Override
    public void cancel(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            // ???????????????  ??????????????????
            throw new BusinessException(ExceptionEnum.NO_ORDER);
        }
        // ??????????????????
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)){
            throw new BusinessException(ExceptionEnum.NOT_YOUR_ORDER);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            throw new BusinessException(ExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // ??????????????????????????????????????????
/*        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/
        String address = ip + request.getLocalPort();
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generatorQRCodeImage(payUrl,350,350, Constant.FILE_UPLOAD_DIR+orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/"+orderNo + ".png";
        return pngAddress;
    }

    private List<OrderVo> orderListToOrderVoList(List<Order> orderList) {
        ArrayList<OrderVo> orderVoList = new ArrayList<OrderVo>();
        for (Order order : orderList) {
            OrderVo orderVo = getOrderVo(order);
            orderVoList.add(orderVo);
        }

        return orderVoList;
    }

    private OrderVo getOrderVo(Order order) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo);
        // ?????????????????????orderItemVoList
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNO(order.getOrderNo());
        ArrayList<OrderItemVo> orderItemVoList = new ArrayList<>();
        for (OrderItemVo orderItemVo : orderItemVoList) {
            OrderItemVo itemVo = new OrderItemVo();
            BeanUtils.copyProperties(orderItemVo,itemVo);
            orderItemVoList.add(itemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVo.getOrderStatus()).getValue());
        return orderVo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVo> cartVoList) {
        for (CartVo cartVo : cartVoList) {
            cartMapper.deleteByPrimaryKey(cartVo.getId());
        }
    }

    private List<OrderItem> cartVoListToOrderItemList(List<CartVo> cartVoList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartVo cartVo : cartVoList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVo.getProductId());
            // ????????????????????????
            orderItem.setProductName(cartVo.getProductName());
            orderItem.setProductImg(cartVo.getProductImage());
            orderItem.setUnitPrice(cartVo.getPrice());
            orderItem.setQuantity(cartVo.getQuantity());
            orderItem.setTotalPrice(cartVo.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVo> cartVoList) {
        for (CartVo cartVo : cartVoList) {
            Product product = productMapper.selectByPrimaryKey(cartVo.getProductId());
            if (product == null || product.getStatus().equals(Constant.ProductStatus.TAKE_DOWN)){
                throw new BusinessException(ExceptionEnum.NOT_ENOUGH_STOCK);
            }
            if (cartVo.getQuantity() > product.getStock()){
                throw new BusinessException(ExceptionEnum.NOT_ENOUGH_STOCK);
            }
        }
    }

    @Override
    public void pay(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            // ???????????????  ??????????????????
            throw new BusinessException(ExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        throw new BusinessException(ExceptionEnum.WRONG_ORDER_STATUS);
    }

    // ??????
    @Override
    public void deliver(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            // ???????????????  ??????????????????
            throw new BusinessException(ExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        throw new BusinessException(ExceptionEnum.WRONG_ORDER_STATUS);
    }

    // ????????????
    @Override
    public void finish(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            // ???????????????  ??????????????????
            throw new BusinessException(ExceptionEnum.NO_ORDER);
        }
        // ???????????????????????????????????????????????????
        if (!userService.checkAdminRole(UserFilter.currentUser)
                &&
                !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new BusinessException(ExceptionEnum.NOT_YOUR_ORDER);
        }

        if (order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.FINISH.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        throw new BusinessException(ExceptionEnum.WRONG_ORDER_STATUS);
    }
}
