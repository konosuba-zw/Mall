package cn.e3mall.order.interceptor;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.TbItem;
import cn.e3mall.common.pojo.TbUser;
import cn.e3mall.common.util.CookieUtils;
import cn.e3mall.common.util.E3Result;
import cn.e3mall.common.util.JsonUtils;
import cn.e3mall.sso.service.TokenService;
import jdk.nashorn.internal.parser.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Value("${SSO_URL}")
    private String SSO_URL;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CartService cartService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中取token
        String token = CookieUtils.getCookieValue(request, "token");
        //判断token是否存在
        if(StringUtils.isBlank(token)){
            //若token不存在，未登录状态，跳转到sso系统的登录界面。用户登录后，跳转到当前请求的url
            response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURI());
            //拦截
            return false;
        }
        //若token存在，需要调用sso系统的服务，根据token取用户信息
        E3Result e3Result = tokenService.getUserByToken(token);
        if(e3Result.getStatus() != 200){
            //如果取不到，用户登录已过期，需要登录
            response.sendRedirect(SSO_URL + "/page/login?redirect=" + request.getRequestURI());
            //拦截
            return false;
        }
        //如果取到，是登陆状态，需要把用户信息写入request
        TbUser user = (TbUser) e3Result.getData();
        request.setAttribute("user",user);
        //判断cookie中是否有购物车数据，如果有就合并到服务端
        String jsonCartList = CookieUtils.getCookieValue(request, "cart", true);
        if(StringUtils.isNotBlank(jsonCartList)){
            //合并购物车
            cartService.mergeCart(user.getId(), JsonUtils.jsonToList(jsonCartList, TbItem.class));
        }
        //放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
