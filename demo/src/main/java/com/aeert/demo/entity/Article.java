package com.aeert.demo.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author l'amour solitaire
 * @Description 文章Entity
 * @Date 2020/7/17 下午1:32
 **/
@Data
@Builder
@Accessors(chain = true)
public class Article implements Serializable {

    /**
     * ID
     **/
    private Long id;

    /**
     * 标题
     **/
    private String title;

    /**
     * 内容
     **/
    private String content;

    /**
     * 作者
     **/
    private String authorName;

    /**
     * 创建时间
     **/
    private Date createTime;

}
