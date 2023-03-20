package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Category;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.entity.SetmealDish;
import com.example.waimai.service.CategoryService;
import com.example.waimai.service.SetmealDishService;
import com.example.waimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
@PostMapping
@ResponseBody
@CacheEvict(value ="setmealCache",allEntries = true)
public R<String> save(@RequestBody SetmealDto setmealDto){
setmealService.saveWithDish(setmealDto);

    return R.success("新增套餐成功");
}
@GetMapping("/page")
@ResponseBody
public R<Page> page(int page,int pageSize,String name){

    Page<Setmeal> pageInfo=new Page<>(page,pageSize);
    Page<SetmealDto> dtoPage=new Page<>();


    LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

    queryWrapper.like(name!=null,Setmeal::getName,name);

    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    setmealService.page(pageInfo,queryWrapper);
    //d对象拷贝
    BeanUtils.copyProperties(pageInfo,dtoPage,"records");
    List<Setmeal> records=pageInfo.getRecords();

   List<SetmealDto> list= records.stream().map((item)->{
        SetmealDto setmealDto=new SetmealDto();
        //
        BeanUtils.copyProperties(item,setmealDto);
        //
        Long categoryId= item.getCategoryId();

        Category category=categoryService.getById(categoryId);
        if(category!=null){
            String categoryName=category.getName();
            setmealDto.setCategoryName(categoryName);
        }
        return setmealDto;
    }).collect(Collectors.toList());

   dtoPage.setRecords(list);
    return R.success(dtoPage);
}
//根据条件查询套餐
@GetMapping("/list")
@Cacheable(value="setmealCache",key="#setmeal.categoryId+'_'+#setmeal.status")
@ResponseBody
public R<List<Setmeal>> list(Setmeal setmeal){
    LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
    queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());

    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    List<Setmeal> list= setmealService.list(queryWrapper);


    return R.success(list);
}
//删除套餐
@DeleteMapping
@ResponseBody
@CacheEvict(value ="setmealCache",allEntries = true)
public R<String> delete(@RequestParam List<Long> ids){


    setmealService.removeWithDish(ids);
    return R.success("删除成功套餐");
}


}
