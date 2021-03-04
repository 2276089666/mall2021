package com.cloud.common.constant;

/**
 * @Author ws
 * @Date 2021/2/17 18:25
 * @Version 1.0
 */
public class ProductConstant {
    public enum  AttributeType{
        ATTR_TYPE_BASE(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");

        private Integer code;
        private String message;
        AttributeType(Integer code,String message){
            this.code=code;
            this.message=message;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum  ProductStatus{
        SPU_NEW(0,"新建"),SPU_UP(1,"上架"),SPU_DOWN(2,"下架");

        private Integer code;
        private String message;
        ProductStatus(Integer code,String message){
            this.code=code;
            this.message=message;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
