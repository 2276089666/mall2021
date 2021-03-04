package com.cloud.common.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author ws
 * @Date 2021/1/31 13:43
 * @Version 1.0
 */
@Documented
@Constraint(
        //指定我们校验器
        validatedBy = {ListValueConstraintValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {

    //前面三个是jsr303的注解规范

    /**
     * 默认的message找配置文件的键com.cloud.common.annotation.ListValue.message的值,我定义在了ValidationMessages.properties
     * @return
     */
    String message() default "{com.cloud.common.annotation.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int []values() default {};
}
