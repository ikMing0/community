package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }


    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile img, Model model){
        Map<String, Object> map = userService.uploadImg(img);
        if (map.containsKey("url")){
            model.addAttribute(map);
            return "redirect:/index";
        }else {
            model.addAttribute(map);
            return "/site/setting";
        }
    }


    @PostMapping("/updatePassword")
    public String updatePassword(Model model,String oldPassword,String newPassword,@CookieValue("ticket")String ticket){
        Map<String, Object> map = userService.updatePassword(oldPassword, newPassword);
        if (map.containsKey("oldPasswordMsg")){
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            return "/site/setting";
        }if (map.containsKey("newPasswordMsg")){
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            return "/site/setting";
        }else {
            model.addAttribute(map);
            userService.logout(ticket);
            return "redirect:/login";
        }
    }
}
