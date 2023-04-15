package com.nowcoder.community.service;

import com.google.gson.Gson;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.*;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.Cookie;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Autowired
    private HostHolder hostHolder;

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap();
        //空值处理
        if (user==null){
            throw  new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号
        User u1 = userMapper.selectByName(user.getUsername());
        if (u1!=null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        //验证邮箱
        User u2 = userMapper.selectByEmail(user.getEmail());
        if (u2!=null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl("http://rsn5scn46.hn-bkt.clouddn.com/good.png");
        user.setCreateTime(new Date());
        int i = userMapper.insertUser(user);
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8888/community/activation/101/code
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"账号激活",content);
        return map;
    }


    public int activation(int userId,String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            //重复激活
            return CommunityConstant.ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)) {
            //激活成功
            userMapper.updateStatus(userId,1);
            return CommunityConstant.ACTIVATION_SUCCESS;
        }else {
            //激活码错误
            return CommunityConstant.ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username,String password, int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg","账号不存在");
            return map;
        }

        //验证状态
        if (user.getStatus() == 0){
            map.put("usernameMsg","账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!password.equals(user.getPassword())){
            map.put("passwordMsg","密码错误！");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }


    public Map<String,Object> uploadImg(MultipartFile img) {
        Map<String,Object> map = new HashMap<>();
        //判断上传类型
        //获取原始文件名
        String originalFilename = img.getOriginalFilename();
        //对原始文件名判断
        if (!originalFilename.endsWith(".png")){
            map.put("usernameMsg","文件格式错误");
//            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }
        //更改图片文件存放位置，并且添加uuid
        String filePath = PathUtils.generateFilePath(originalFilename);
        //上传图片
        String url = uploadOss(img,filePath);
        User user = hostHolder.getUser();
        userMapper.updateHeader(user.getId(),url);
        map.put("url",url);
//        userMapper.updateHeader()
//        return ResponseResult.okResult(url);
        return map;
    }


    @Value("${oss.accessKey}")
    private String accessKey;
    @Value("${oss.secretKey}")
    private String secretKey;
    @Value("${oss.bucket}")
    private String bucket;


    private String uploadOss(MultipartFile img, String filePath){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filePath;

        try {
            InputStream inputStream = img.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return "http://rsn5scn46.hn-bkt.clouddn.com/"+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return "上传失败,请刷新后再次尝试";
    }

    public Map<String,Object> updatePassword(String lodPassword, String newPassword){
        Map<String,Object> map = new HashMap<>();
        User user = hostHolder.getUser();
        if (StringUtils.isBlank(lodPassword)) {
            map.put("oldPasswordMsg","密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg","密码不能为空！");
            return map;
        }
        //新密码和用户对比，如果为true，直接返回
        if (user.getPassword().equals(CommunityUtil.md5(newPassword+user.getSalt()))){
            map.put("newPasswordMsg","新密码不可与旧密码一致！");
            return map;
        }
        //旧密码和密码做比较，如果是true 那么修改密码，如果为false则返回旧密码不正确
        String userPassword = CommunityUtil.md5(lodPassword + user.getSalt());
        if (userPassword.equals(user.getPassword())) {
            userMapper.updatePassword(user.getId(),CommunityUtil.md5(newPassword+user.getSalt()));
        }else {
            map.put("oldPasswordMsg","密码不正确");
        }
        return map;
    }
}
