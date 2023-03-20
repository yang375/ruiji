package com.example.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.common.CustomerException;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.entity.SetmealDish;
import com.example.waimai.mapper.SetMealMapper;
import com.example.waimai.service.SetmealDishService;
import com.example.waimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetMealMapper,Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 操作setmeal 执行insert操作

        this.save(setmealDto);

        List<SetmealDish> setmealDishList=setmealDto.getSetmealDishes();
        setmealDishList.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联关系 操作setmeal-dish 执行insert
        setmealDishService.saveBatch(setmealDishList);

    }
//删除套餐同事删除关联数据
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态是否可以删除  --停售可以删除删除套餐表中数据
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count= this.count(queryWrapper);

        if(count>0){
            throw new CustomerException("套餐正在售卖中不能删除");

        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper);

        //不能删除   抛出异常
    }
}
