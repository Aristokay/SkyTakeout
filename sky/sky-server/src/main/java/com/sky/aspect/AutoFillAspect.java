package com.sky.aspect;

import java.lang.reflect.Method;
import java.security.Signature;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义切面类
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点：对哪些类/方法进行拦截  &&  必须加上AutoFill注解
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autofillPointCut() {
    }

    /**
     * 前置通知
     */
    @Before("autofillPointCut()")
    public void atuofill(JoinPoint joinPoint) {
        log.info("开始进行公共字段的自动填充...");
        //1.获取被拦截的方法的数据库操作类型，update or insert？
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象？
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = annotation.value();//获得数据库操作类型

        //2.获取到当前被拦截的方法的参数，
        //也就是void insert(Employee employee);的employee实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];//传参的第一个对象

        //3.准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4.
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreatimeMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,
                        LocalDateTime.class);
                Method setCreateUserMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,
                        Long.class);
                Method setUpdateTimeMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,
                        LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,
                        Long.class);

                //通过反射为对象entity的属性赋值
                setCreatimeMethod.invoke(entity, now);
                setCreateUserMethod.invoke(entity, currentId);
                setUpdateTimeMethod.invoke(entity, now);
                setUpdateUserMethod.invoke(entity, currentId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTimeMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,
                        LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,
                        Long.class);

                //通过反射为对象entity的属性赋值
                setUpdateTimeMethod.invoke(entity, now);
                setUpdateUserMethod.invoke(entity, currentId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
