package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.waimai.common.R;
import com.example.waimai.entity.User;
import com.example.waimai.service.UserService;
import com.example.waimai.utils.SendSms;
import com.example.waimai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            String code= ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}",code);

            //SendSms.sendMessage("阿里云短信测试",);
            session.setAttribute(phone,code);


            //将生成的验证码缓存到redis
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);


            return R.success("收集验证码发送成功");
        }
        return R.error("短信发送失败");



        //生成思维验证码

        //调用aliyun接口


        //验证码保存起来到Session


    }
//移动用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
       //获取手机号
        String phone=map.get("phone").toString();
        //
        String code=map.get("code").toString();

        //Object codeInSession=session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object codeInSession=redisTemplate.opsForValue().get(phone);

        if(codeInSession!=null&&codeInSession.equals(code)){

            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user=userService.getOne(queryWrapper);
            if(user==null){

                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //如果用户登录成功删除redis中缓存中的验证码
            redisTemplate.delete(phone);
            return R.success(user);

        }
       return R.error("失败");
    }
}
