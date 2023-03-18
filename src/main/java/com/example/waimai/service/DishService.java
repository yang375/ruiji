package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.dto.DishDto;
import com.example.waimai.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品 对应的口味数据 需要操作两张表 dish dish flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(String id);
}
