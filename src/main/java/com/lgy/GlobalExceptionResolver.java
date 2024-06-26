package com.lgy;

import com.lgy.base.ResultInfo;
import com.lgy.exceptions.AuthException;
import com.lgy.exceptions.NoLoginException;
import com.lgy.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    /**
     * ⽅法返回值类型
     * 视图
     * JSON
     * 如何判断⽅法的返回类型：
     * 如果⽅法级别配置了 @ResponseBody 注解，表示⽅法返回的是JSON；
     * 反之，返回的是视图⻚⾯
     *
     * @param req
     * @param resp
     * @param handler
     * @param e
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception e) {
        /**
         * 判断异常类型
         * 如果是未登录异常，则先执⾏相关的拦截操作
         */
        if (e instanceof NoLoginException) {
            // 如果捕获的是未登录异常，则重定向到登录⻚⾯
            ModelAndView mv = new ModelAndView("redirect:/index");
            return mv;
        }

        //设置默认异常处理
        ModelAndView mv = new ModelAndView();
        mv.setViewName("");
        mv.addObject("code", 400);
        mv.addObject("msg", "系统异常，请稍后再试...");
        //判断HandlerMethod
        if (handler instanceof HandlerMethod) {
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取方法上面的ResponseBody注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断ResponseBody注解是否存在（如果不存在，表示返回视图，否则，返回JSON）
            if (responseBody == null) {
                //返回视图
                if (e instanceof ParamsException) {
                    ParamsException pe = (ParamsException) e;
                    mv.addObject("code", pe.getCode());
                    mv.addObject("msg", pe.getMsg());
                } else if (e instanceof AuthException) {//认证异常
                    AuthException a = (AuthException) e;
                    mv.addObject("code", a.getCode());
                    mv.addObject("msg", a.getMsg());
                }
                return mv;
            } else {


                //返回JSON
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试");
                //如果捕获的是自定义异常
                if (e instanceof ParamsException) {
                    ParamsException pe = (ParamsException) e;
                    resultInfo.setCode( pe.getCode());
                    resultInfo.setMsg( pe.getMsg());
                }else if (e instanceof AuthException) {//认证异常
                    AuthException a = (AuthException) e;
                    resultInfo.setCode(a.getCode());
                    resultInfo.setMsg( a.getMsg());
                }
                //设置响应类型和编码格式（响应JSON格式）
                resp.setContentType("application/json;charset=utf-8");
                //得到流
                PrintWriter out = null;
                try {
                    out = resp.getWriter();
                    out.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return null;
            }
        }
        return mv;
    }

}
