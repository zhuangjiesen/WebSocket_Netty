package com.jason.activiti.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * ar需求项目
 * @Date: Created in 2018/8/6
 */
public class ArProject implements Serializable {

    private String taskId;
    private Long id;
    private String name;
    private String budget;
    private String content;
    private String status;
    private String createUserId;
    private String createUserGroup;
    /** mr上服务提供商数量 **/
    private Integer mrCount;
    private List<String> mrClientUserIdList;
    private List<BidPlan> bidPlanList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMrCount() {
        return mrCount;
    }

    public void setMrCount(Integer mrCount) {
        this.mrCount = mrCount;
    }


    public List<String> getMrClientUserIdList() {
        return mrClientUserIdList;
    }

    public void setMrClientUserIdList(List<String> mrClientUserIdList) {
        this.mrClientUserIdList = mrClientUserIdList;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public List<BidPlan> getBidPlanList() {
        return bidPlanList;
    }

    public void setBidPlanList(List<BidPlan> bidPlanList) {
        this.bidPlanList = bidPlanList;
    }
}
