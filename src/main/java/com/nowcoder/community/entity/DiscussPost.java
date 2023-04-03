package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (DiscussPost)实体类
 *
 * @author makejava
 * @since 2023-04-02 17:36:51
 */
@Data
public class DiscussPost implements Serializable {
    private static final long serialVersionUID = 348290475217590635L;
    
    private Integer id;
    
    private Integer userId;
    
    private String title;
    
    private String content;
    /**
     * 0-普通; 1-置顶;
     */
    private Integer type;
    /**
     * 0-正常; 1-精华; 2-拉黑;
     */
    private Integer status;
    
    private Date createTime;
    
    private Integer commentCount;
    
    private double score;
}

