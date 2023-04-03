package com.nowcoder.community.entity;

import lombok.Data;

/**
 * 封装分页的信息
 */
@Data
public class Page {

    // 当前的页码
    private Integer current = 1;
    // 显示上限
    private Integer limit = 10;
    // 数据总数(用于计算总页数)
    private Integer rows;
    //查询路径(用于复用分页连接)
    private String path;

    public void setCurrent(Integer current) {
        if (current >=1 ){
            this.current = current;
        }
    }

    public void setLimit(Integer limit) {
        if (limit > 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public void setRows(Integer rows) {
        if (rows >=1 ){
            this.rows = rows;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public Integer getOffset(){
        //current * limit - limit
        return (current - 1)*limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public Integer getTotal(){
        return rows%limit==0 ? rows/limit : rows/limit+1;
    }

    /**
     * 获取起始页码
     * @return
     */
    public Integer getFrom(){
        int from = current - 2;
        return from < 1 ? 1:from;
    }

    /**
     * 获取结束页码
     * @return
     */
    public Integer getTo(){
        Integer to = current + 2;
        Integer total = getTotal();
        return to > total ? total:to;
    }

}
