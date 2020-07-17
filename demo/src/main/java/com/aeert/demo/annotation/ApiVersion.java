package com.aeert.demo.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @Author l'amour solitaire
 * @Description 接口版本标识注解
 * @Date 2020/7/15 下午5:35
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ApiVersion {
    
    /**
     * 标识版本号
     */
    int value();
}
