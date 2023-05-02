package com.nowcoder.community.entity;

import java.util.Date;

import lombok.Data;
/**
 * (Comment)表实体类
 *
 * @author makejava
 * @since 2023-04-21 16:53:10
 */
@SuppressWarnings("serial")
@Data
public class Comment  {
    private int id;

    private int userId;
    
    private int entityType;
    
    private int entityId;
    
    private int targetId;
    
    private String content;
    
    private int status;
    
    private Date createTime;

}

