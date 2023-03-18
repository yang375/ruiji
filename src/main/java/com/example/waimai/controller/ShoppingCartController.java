package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.R;
import com.example.waimai.entity.ShoppingCart;
import com.example.waimai.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

@PostMapping ("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //设置用户id 指定当前是哪个用户的购物车
        Long currentId= BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查当前菜品和套餐是否在购物车
        Long dishId=shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId!=null){
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else {
            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

        }
        ShoppingCart cartServiceOne= shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne!=null){
            //已经存在
            Integer number=cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //不存在
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }


        return R.success(cartServiceOne);
    }

    //查看购物car
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

    LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
    queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
    List<ShoppingCart> list =shoppingCartService.list(queryWrapper);
    return R.success(list);
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
    LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
    queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

    shoppingCartService.remove(queryWrapper);
    return  R.success("清空购物车成功");

    }



}
