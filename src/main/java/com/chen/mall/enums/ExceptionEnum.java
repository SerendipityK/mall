package com.chen.mall.enums;

/**
 *  异常枚举
 */
public enum  ExceptionEnum {
    NEED_USER_NAME(10001,"用户名不能为空"),
    NEED_USER_PASSWORD(10002,"密码不能为空"),
    PASSWORD_TO_SHORT(10003,"密码长度不能小于8位"),
    NAME_EXISTED(10004,"不允许重名"),
    INSERT_FAILED(10005,"插入数据失败"),
    USERNAME_PASSWORD_FAILED(10006,"用户名或密码有误"),
    NEED_LOGIN(10007,"用户未登录"),
    UPDATE_FAILED(10008,"更新失败"),
    ADMIN_USER_FAILED(10009,"没有管理员权限"),
    CATEGORY_TYPE_OUT(10010,"分类目录层级超过了3级"),
    PARAM_IS_NULL(10011,"参数为空"),
    CATEGORY_NAME_EXISTED(10012,"分类名称已存在"),
    REQUEST_PARAM_ERROR(10013,"参数错误"),
    DELETE_ERROR(10014,"删除失败"),
    UPLOAD_IMAGE_FAILED(10015,"图片上传失败"),
    MKDIR_FAILED(10015,"文件夹创建失败"),
    PRODUCT__NOT_BUY(10016,"商品暂未出售"),
    NOT_ENOUGH_STOCK(10017,"商品库存不够了"),
    CART_IS_EMPTY(10018,"购物车里面什么也没有"),
    PRODUCT_NOT_FIND(10019,"该商品移除购物车失败"),
    CART_EMPTY(10019,"购物车已勾选的商品为空"),
    NO_ENUM(10020,"没有找到对应的枚举"),
    NO_ORDER(10021,"该订单不存在"),
    NOT_YOUR_ORDER(10022,"该订单不属于你"),
    WRONG_ORDER_STATUS(10022,"订单状态不符合"),


    SYSTEM_ERROR(20000,"系统异常");
    /**
     * 异常码
     */
    Integer code;

    /**
     * 异常信息
     */
    String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
