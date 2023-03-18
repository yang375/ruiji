package com.example.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.dto.DishDto;
import com.example.waimai.entity.Dish;
import com.example.waimai.entity.DishFlavor;
import com.example.waimai.mapper.DishMapper;
import com.example.waimai.service.DishFlavorService;
import com.example.waimai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
//新增菜品同时 保存口味数据
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存基本信息到菜品dish
        this.save(dishDto);
        Long dishId=dishDto.getId();
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors =flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);

    }


    public DishDto getByIdWithFlavor(String id) {
        //查询菜品基本信息从dish表查
        Dish dish =this.getById(id);
        //dish为null？
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);


        //查询当前菜品对应的口味信息  从dish_flavor查
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }
}
