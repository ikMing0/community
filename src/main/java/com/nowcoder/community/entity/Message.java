package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (Message)实体类
 *
 * @author makejava
 * @since 2023-04-22 22:36:36
 */
@Data
public class Message implements Serializable {
    private static final long serialVersionUID = 157028552445991468L;
    
    private Integer id;
    
    private Integer fromId;
    
    private Integer toId;
    
    private String conversationId;
    
    private String content;
    /**
     * 0-未读;1-已读;2-删除;
     */
    private Integer status;
    
    private Date createTime;

}

