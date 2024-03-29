package com.nowcoder.community.util;

public class CommunityConstant {

    /**
     * 激活成功
     */
    public static final int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    public static final int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    public static final int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的超时时间
     */
    public static final int DEFAULT_EXPIRED_SECONDS = 60 * 60 * 12;//半天

    /**
     * 记住密码的超时时间
     */
    public static final int REMEMBER_EXPIRED_SECONDS = 60 * 60 * 24 * 100;//一百天

    /**
     * 实体类型：帖子
     */
    public static final int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    public static final int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    public static final int ENTITY_TYPE_USER = 3;
}
