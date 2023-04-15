package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.nowcoder.community.util.CommunityConstant.DEFAULT_EXPIRED_SECONDS;
import static com.nowcoder.community.util.CommunityConstant.REMEMBER_EXPIRED_SECONDS;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/register")
    public String getRegisterPage() {return "/site/register";}

    @GetMapping("/login")
    public String getLoginPage() {return "/site/login";}

    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);

        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送一封激活邮件，请前往激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    // http://localhost:8888/community/activation/101/code
    @GetMapping("activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId")int userId, @PathVariable("code")String code){
        int result = userService.activation(userId, code);
        if (result == CommunityConstant.ACTIVATION_SUCCESS) {
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用");
            model.addAttribute("target","/login");
        }else if (result == CommunityConstant.ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已经激活");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，您提供的激活码错误");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"png",out);
        } catch (IOException e) {
            logger.error("相应验证码失败"+e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username,String password,String code,boolean rememberme,Model model,HttpSession session,HttpServletResponse response){
        //检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        //验证账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }

    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
