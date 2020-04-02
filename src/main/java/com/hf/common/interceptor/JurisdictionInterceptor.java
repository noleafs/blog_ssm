package com.hf.common.interceptor;


import com.hf.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


// 权限拦截器
public class JurisdictionInterceptor implements HandlerInterceptor {

    @Autowired
    @Qualifier("adminInterview")
    private List<String> urls;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        User user = (User) request.getSession().getAttribute("user");

        if (urls.contains(uri)){
            if (user == null){
                response.setStatus(401);
                return false;
            }
            if (user.getRole_id() !=1 && user.getRole_id() != 2){
                System.out.println("您不是管理员，或者更高权限");
                response.setStatus(403);
                return false;
            }

        }
        return true;
    }
}
