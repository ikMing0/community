package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (LoginTicket)实体类
 *
 * @author makejava
 * @since 2023-04-11 11:12:34
 */
@Data
public class LoginTicket implements Serializable {
    private static final long serialVersionUID = -13006434012531319L;
    
    private Integer id;
    
    private Integer userId;
    
    private String ticket;
    /**
     * 0-有效; 1-无效;
     */
    private Integer status;
    
    private Date expired;



}

