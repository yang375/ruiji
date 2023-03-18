package com.example.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.entity.ShoppingCart;
import com.example.waimai.mapper.ShoppingCartMapper;
import com.example.waimai.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
