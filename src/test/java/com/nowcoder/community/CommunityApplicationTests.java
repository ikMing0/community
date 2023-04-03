package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
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

}
