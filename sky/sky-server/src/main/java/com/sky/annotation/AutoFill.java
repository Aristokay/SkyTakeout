package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sky.enumeration.OperationType;

/**
 * 自定义注解?!用于标识公共字段自动填充功能
 * 在项目中主要填充创建/更新时间、创建/更新用户这些公共字段。
 */
@Target(ElementType.METHOD) //加在方法上
@Retention(RetentionPolicy.RUNTIME) //保留到程序运行时，JVM 能看到，可以通过反射读取
public @interface AutoFill {

    //见类的定义，操作类型：update、insert
    OperationType value();
}
