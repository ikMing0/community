package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断handler是不是HandlerMethod类对象
        if (handler instanceof HandlerMethod){
            //如果是，把他转为HandlerMethod
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            //获取到这个方法
            Method method = handlerMethod.getMethod();
            //判断这个方法带不带有@LoginRequired
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (loginRequired != null && hostHolder.getUser()==null){
                //判断到这个方法上的路径是需要登录才能访问的，并且用户还没有登录
                //设置重定向到登录页面
                response.sendRedirect(request.getContextPath() + "/login");
                //不允许通过
                return false;
            }

        }
        return true;
    }

    /**
     * 在Spring MVC中，handler不仅可以是HandlerMethod类型的对象，还可以是其他类型的对象。简单来说，它可以是任何对象，只要该对象实现了Handler接口或者声明了@Controller注解。
     *
     * 具体来说，以下是handler所支持的类型：
     *
     * HandlerMethod对象：用于表示控制器类中的某个处理方法。
     * HttpRequestHandler对象：用于实现基于Servlet API的处理逻辑，如文件下载等。
     * SimpleControllerHandlerAdapter.ReturningSimpleNameHandler对象：用于返回请求的URL路径。
     * Callable对象：用于异步处理请求，返回一个Future对象。
     * DeferredResult对象：用于Delayed Result Processing，它表示一个异步结果，并且允许在随后的时间点进行结果处理。
     * WebSocketHandler对象：用于处理WebSocket通信。
     */
}
