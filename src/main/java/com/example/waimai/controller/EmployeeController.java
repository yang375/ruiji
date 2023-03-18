package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.entity.Employee;
import com.example.waimai.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
//1   将页面提交的密码 password进行md5加密
        String password=employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 查数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp= employeeService.getOne(queryWrapper);

        //3.   判断查询成功
        if(emp==null){
            return R.error("登录失败");
        }
        // 4. 密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }
        //查看状态是否锁定
        if(emp.getStatus()==0){
            return R.error("账号禁用");
        }
        //登录成功 将员工结果存入session 返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    //退出登录
    @PostMapping("/logout")
    public R<String> logOut(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    //新增员工
   @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        //初始密码123456 进行md5加密
       employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

       //employee.setCreateTime(LocalDateTime.now());
       //employee.setUpdateTime(LocalDateTime.now());
       //employee.setCreateUser((Long) request.getSession().getAttribute("employee"));

       //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

       employeeService.save(employee);
       return R.success("新增员工成功");
    }
@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //分页构造器
    Page pageInfo=new Page(page,pageSize);
    //条件构造器
    LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
    //过滤条件
    queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
    queryWrapper.orderByDesc(Employee::getUpdateTime);
    //执行查询
    employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);

    }
    //根据id修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

       // Long empId=(Long) request.getSession().getAttribute("employee");
       // employee.setUpdateTime(LocalDateTime.now());
       // employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查员工信息");
        Employee employee= employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到");
    }
}
