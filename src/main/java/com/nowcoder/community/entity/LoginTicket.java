package com.nowcoder.community.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.io.Serializable;

/**
 * (LoginTicket)实体类
 *
 * @author makejava
 * @since 2023-04-11 11:12:34
 */
@Data
@Getter
@Setter
public class LoginTicket implements Serializable {

    private int id;
    
    private int userId;
    
    private String ticket;
    /**
     * 0-有效; 1-无效;
     */
    private int status;
    
    private Date expired;



}

