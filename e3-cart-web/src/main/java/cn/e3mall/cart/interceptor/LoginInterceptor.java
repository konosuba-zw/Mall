package cn.e3mall.cart.interceptor;

import cn.e3mall.common.pojo.TbUser;
import cn.e3mall.common.util.CookieUtils;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录处理拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //前处理，执行handler之前执行
        //从cookie中取token
        String token = CookieUtils.getCookieValue(request, "token");
        //如果没有token，未登录状态，直接放行
        if(StringUtils.isBlank(token)){
            return true;
        }
        //取到token，需要调用sso系统的服务，根据token取用户信息
        E3Result e3Result = tokenService.getUserByToken(token);
        //取到用户信息，登录过期，直接放行
        if(e3Result.getStatus()!=200){
            return true;
        }
        //取到用户信息，登录状态
        TbUser user = (TbUser) e3Result.getData();
        //把用户信息放入request中，只需要在Controller中判断request中是否包含user信息，放行
        request.setAttribute("user",user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //handler执行之后，返回ModeAndView之前
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //完成处理，返回ModelAndView之后
        //可以在此处理异常
    }
}
