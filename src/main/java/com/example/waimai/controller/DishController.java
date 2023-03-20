package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.dto.DishDto;
import com.example.waimai.entity.Category;
import com.example.waimai.entity.Dish;
import com.example.waimai.entity.DishFlavor;
import com.example.waimai.service.CategoryService;
import com.example.waimai.service.DishFlavorService;
import com.example.waimai.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    //新增菜品
    @PostMapping
     public R<String> save(@RequestBody DishDto dishDto){



        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");

}
//分页
    @GetMapping("/page")
public R<Page> page(int page,int pageSize,String name){
       //分页构造器对象
       Page<Dish> pageInfo=new Page<>(page,pageSize);
       Page<DishDto> dishDtoPage=new Page<>();
       //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records= pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId=item.getCategoryId();

            Category category=categoryService.getById(categoryId);

            if(category!=null){
                String categoryName=category.getName();
                dishDto.setCategoryName(categoryName);

            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
}
//修改   --根据id查询菜品信息和对应的口味信息
@GetMapping("/{id}")
public R<DishDto> get(@PathVariable String id){
        //查两张表
    DishDto dishDto = dishService.getByIdWithFlavor(id);
    return R.success(dishDto);
}
//根据条件查询对应的菜品数据
/*@GetMapping("/list")
public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询起售状态的
        //queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    List<Dish> list=dishService.list(queryWrapper);
    return R.success(list);
}*/

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;

        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //先从redis获取数据
        dishDtoList=(List<DishDto>)redisTemplate.opsForValue().get(key);

        if(dishDtoList!=null){
            //若果存在 直接返回无需查询数据库
            return  R.success(dishDtoList);
        }



        //如果不存在  查询数据库  查到的菜品缓存到redis


        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询起售状态的
        //queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list=dishService.list(queryWrapper);

         dishDtoList=list.stream().map((item)->{
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId=item.getCategoryId();

            Category category=categoryService.getById(categoryId);

            if(category!=null){
                String categoryName=category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId= item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> dishFlavorList= dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

         //如果不存在 需要查数据库 将查到的菜品数据缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

}
