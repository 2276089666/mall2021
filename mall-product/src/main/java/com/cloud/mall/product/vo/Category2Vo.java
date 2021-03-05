package com.cloud.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/5 18:22
 * @Version 1.0
 */
@Data
public class Category2Vo {
    private String catalog1Id;
    private List<catalog3> catalog3List;
    private String id;
    private String name;

    @Data
    public static class catalog3{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
