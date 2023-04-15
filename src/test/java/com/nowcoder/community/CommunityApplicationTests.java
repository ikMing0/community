package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testByUserName(){
        User liubei = userMapper.selectByName("liubei");
        System.out.println(liubei);
    }

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void selectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 1, 10);
        int i = discussPostMapper.selectDiscussPostRows(0);
        for (DiscussPost post : discussPosts){
            System.out.println(post);
        }
        System.out.println(i);
    }

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void textService(){
        List<DiscussPost> discussPost = discussPostService.findDiscussPost(0, 0, 10);
        for (DiscussPost post:discussPost){
            System.out.println(post);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(CommunityApplicationTests.class);

    @Test
    public void logText(){
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
    }

    @Autowired
    private MailClient mailClient;
    @Test
    public void mailTest(){
        mailClient.sendMail("2321271246@qq.com","你好啊","这里是内容");
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testLoginTicketMapper(){
        //插入
//        LoginTicket loginTicket = new LoginTicket();
//        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
//        loginTicket.setStatus(0);
//        loginTicket.setUserId(101);
//        loginTicket.setTicket("abc");
//        loginTicketMapper.insertLoginTicket(loginTicket);

        //查询
//        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
//        System.out.println(loginTicket);

        //更改
        loginTicketMapper.updateStatus("abc",1);
    }

    @Test
    public void md5test(){
        System.out.println(CommunityUtil.md5("123960ad"));
    }
}
