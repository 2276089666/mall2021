package com.cloud.common.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author ws
 * @Date 2021/1/31 13:53
 * @Version 1.0
 */

/**
 * ListValue:我们的注解
 * Integer:我们要校验的数据类型
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    Set<Integer> set=new HashSet<>();

    /**
     * 初始化
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        //我们指定的value范围
        int[] values = constraintAnnotation.values();
        //将我们指定的值(范围),添加进我们的set
        if (values.length>0){
            for (int value : values) {
                set.add(value);
            }
        }
    }

    /**
     * 判断是否校验成功
     * @param integer  提交过来的值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        //看提交过来的值是否在我们的set中,在就返回true,不在返回false
        return set.contains(integer);
    }
}
