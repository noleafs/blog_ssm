package com.hf.common.interceptor;

import com.hf.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

// 登录拦截器
public class RegisterInterceptor implements HandlerInterceptor {

    @Autowired
    @Qualifier("interview")
    private List<String> urls;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        //包含在里面,就代表需要登录
        if (urls.contains(uri)){
            User user = (User)request.getSession().getAttribute("user");
            if (user == null){
//                response.sendRedirect("/login.html");
                response.setStatus(401);
                return false;
            }
        }
        return true;
    }
}
