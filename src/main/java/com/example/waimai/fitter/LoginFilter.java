package com.example.waimai.fitter;

import com.alibaba.fastjson.JSON;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查是否通过登录进入页面
@WebFilter(filterName = "LoginFilter",urlPatterns = "/*")
@Slf4j
public class LoginFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        //1.  获取本次请求的uri
        String requestURI=request.getRequestURI();
        log.info("拦截到请求： {}",requestURI);
        //2.   本次请求是否需要检查
        String[] urls= new String[]{
                "/employee/login","/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/dish/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check=check(urls,requestURI);


//        3.如果不需要处理直接放行
        if(check){
            log.info("本次请求不需要处理： {}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        4.判断登录状态 如果已经登录放行
       if( request.getSession().getAttribute("employee")!=null){
           log.info("用户已经登录");
           Long empId=(Long) request.getSession().getAttribute("employee");
           BaseContext.setCurrentId(empId);
           filterChain.doFilter(request,response);
           return;
       }

        if( request.getSession().getAttribute("user")!=null){
            log.info("用户已经登录");
            Long userId=(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }



       log.info("用户未登录");
//       5.未登录 返回登录结果 通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
return;

    }
    //检查是否匹配的方法
    public boolean check(String[] urls,String requestURI){
        for(String url: urls){
            boolean match= PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
