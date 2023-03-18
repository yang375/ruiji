package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.entity.Category;
import com.example.waimai.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//分类管理
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    //新增分类
    @PostMapping
public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
}
@GetMapping("/page")
public R<Page> page(int page, int pageSize){
        Page<Category> pageinfo =new Page<>(page,pageSize);
        //条件构造器
    LambdaQueryWrapper<Category> queryWrapper =new LambdaQueryWrapper<>();
    //添加排序条件
    queryWrapper.orderByAsc(Category::getSort);
    //分页查询
    categoryService.page(pageinfo,queryWrapper);

    return R.success(pageinfo);

}
//删除分类
@DeleteMapping
public R<String> delete(Long id){
log.info("删除分类id为 {}",id);

categoryService.remove(id);
return R.success("分类删除成功");

}
//根据条件查询分类数据
@GetMapping("/list")
public R<List<Category>> list(Category category){
     //条件构造器
    LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

    //添加条件
    queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
    //添加排序条件
    queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

    List<Category> list=categoryService.list(queryWrapper);
        return R.success(list);

}



}
