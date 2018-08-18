package com.jason.activiti.vo;

import java.io.Serializable;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * 竞标对象
 * @Date: Created in 2018/8/9
 */
public class BidPlan implements Serializable {

    private Long id;
    private Long arProjectId;
    private String createUserId;
    private String createUserGroup;
    private String name;
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArProjectId() {
        return arProjectId;
    }

    public void setArProjectId(Long arProjectId) {
        this.arProjectId = arProjectId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserGroup() {
        return createUserGroup;
    }

    public void setCreateUserGroup(String createUserGroup) {
        this.createUserGroup = createUserGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
