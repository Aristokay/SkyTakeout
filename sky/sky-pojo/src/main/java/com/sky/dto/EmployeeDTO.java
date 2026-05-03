package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * （1）Data Transfer Object，只用来和前端传输数据的小类
 * （2）只要把对象存到 Session 里（登录用户信息）
            把对象在网络上传输
            把对象写入文件、缓存（Redis）
            分布式项目、微服务之间传递对象
        就必须加Serializable
 */
@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
