package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.service.SetmealDishService;
import com.example.waimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
@PostMapping
@ResponseBody
public R<String> save(@RequestBody SetmealDto setmealDto){
setmealService.saveWithDish(setmealDto);

    return R.success("新增套餐成功");
}
@GetMapping("/page")
public R<Page> page(int page,int pageSize,String name){

    Page<Setmeal> pageInfo=new Page<>(page,pageSize);

    LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

    queryWrapper.like(name!=null,Setmeal::getName,name);

    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    setmealService.page(pageInfo,queryWrapper);
    return null;
}
//根据条件查询套餐
@GetMapping("/list")
@ResponseBody
public R<List<Setmeal>> list(Setmeal setmeal){
    LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
    queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());

    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    List<Setmeal> list= setmealService.list(queryWrapper);


    return R.success(list);
}


}
