package com.chen.mall.controller;

import com.chen.mall.common.ApiRestResponse;
import com.chen.mall.common.Constant;
import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.chen.mall.model.pojo.Product;
import com.chen.mall.model.requst.AddProductReq;
import com.chen.mall.model.requst.UpdateProductReq;
import com.chen.mall.service.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
public class ProductAdminController {
    @Autowired
    private ProductService productService;

    @ApiOperation(value = "添加商品")
    @PostMapping("/admin/product/add")
    public ApiRestResponse add(@Valid @RequestBody AddProductReq productReq){
        productService.addProduct(productReq);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, MultipartFile file){
        String fileName = file.getOriginalFilename();
        // 后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;

        // 创建文件
        File fileDir = new File(Constant.FILE_UPLOAD_DIR);
        // 目标文件
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if (!fileDir.exists()){
            if (fileDir.mkdir()) {
                // 如果创建文件夹失败了就抛出异常
                throw new BusinessException(ExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL()+"")) +"/images/"+ newFileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ApiRestResponse.error(ExceptionEnum.UPLOAD_IMAGE_FAILED);
    }

    /**
     * 剔除多余uri信息
     * @param uri
     * @return
     */
    private URI getHost(URI uri){
        URI effectiveURI;
        try {
            effectiveURI = new URI(
                    uri.getScheme(),
                    uri.getUserInfo(),
                    uri.getHost(),uri.getPort(),
                    null,null,null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @PostMapping("/admin/product/update")
    public ApiRestResponse update(@Valid @RequestBody UpdateProductReq updateProductReq){
        Product productNew = new Product();
        BeanUtils.copyProperties(updateProductReq,productNew);
        productService.update(productNew);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/product/delete")
    public ApiRestResponse delete(Integer id){
        if (id == null || id == 0){
            throw new BusinessException(ExceptionEnum.PARAM_IS_NULL);
        }
        productService.deleteProduct(id);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(Integer[] ids,Integer sellStatus){
        productService.deleteBatch(ids,sellStatus);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/product/list")
    public ApiRestResponse list(Integer pageNum,Integer pageSize){
        PageInfo pageInfo = productService.listForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }


}
