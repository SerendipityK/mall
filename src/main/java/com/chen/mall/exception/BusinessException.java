package com.chen.mall.exception;

import com.chen.mall.enums.ExceptionEnum;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    private final Integer code;

    private final String message;

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(ExceptionEnum ee){
        this(ee.getCode(),ee.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
