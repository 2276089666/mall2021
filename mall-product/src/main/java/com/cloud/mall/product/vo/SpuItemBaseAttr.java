package com.cloud.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author ws
 * @Date 2021/3/16 17:11
 * @Version 1.0
 */
@Data
@ToString
public class SpuItemBaseAttr{
    private String groupName;
    private List<Attr> attrs;
}
