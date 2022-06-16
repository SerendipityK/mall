package com.chen.mall.common;

import com.chen.mall.enums.ExceptionEnum;
import com.chen.mall.exception.BusinessException;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 常量值
 */
@Component
public class Constant {
    public static final String SALT = "dsadgh,;l531.-";
    public static final String USER = "user";


    public static String FILE_UPLOAD_DIR;


    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir){
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc","price asc");
    }


    public interface ProductStatus{
        int ON_THE_SHELF = 1;  // 商品上架
        int TAKE_DOWN = 0;  // 商品下架
    }

    public interface Cart{
        int CHECK = 1;  // 勾选了
        int NOT_CHECK = 0; // 未勾选
    }

    @Getter
    public enum OrderStatusEnum{
        CANCELED(0,"用户已取消"),
        NOT_PAID(10,"未付款"),
        PAID(20,"已付款"),
        DELIVERED(30,"已发货"),
        FINISH(40,"交易完成")
        ;
        private int code;
        private String value;

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum:values()){
                if (orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new BusinessException(ExceptionEnum.NO_ENUM);
        }
    }
}
