package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);
}
