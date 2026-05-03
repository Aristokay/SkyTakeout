package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController //表现层就是controller
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关的接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工登出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * 因为提交过来的是json格式数据，所以要加RequestBody
     */
    @PostMapping()
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工的信息：{}", employeeDTO);
        System.out.println("当前线程的id:" + Thread.currentThread().getId());

        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * 在前端设计中，返回类型不是json，而是Query，因此不需要加RequestBody
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询") //可用于接口文档调试！
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询员工信息：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用/禁用员工账号
     * （1）Long id为前端路径query参数，已写好！所以不需要加注解
     * （2）因为没有返回数据，所以泛型不用加~
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用员工账号")
    public Result startOrstop(@PathVariable Integer status, Long id) {
        log.info("启用/禁用员工账号为：{},{}", status, id);
        employeeService.startOrstop(status, id);
        return Result.success();
    }

    /**
     * 修改第一步：
     * (1) 根据id查询
     */
    @GetMapping("/{id}")
    @ApiOperation("修改第一步：根据id查询员工信息")
    public Result<Employee> getByid(@PathVariable Long id) {
        Employee employee = employeeService.getByid(id);
        return Result.success(employee);
    }

    /**
     * 修改第二步：
     * (2) 修改信息
     * 因为不是查询，所以result不需要加泛型
     */
    @PutMapping //参考接口文档，不需要参数，因为已经是put
    @ApiOperation("修改第二步：修改信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("想要修改的员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
