package com.jason.core.shiro;

import java.util.Date;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/7/27
 */
public class User{

    private static final long serialVersionUID = 1L;

    /**id */
    protected Integer id;
    private String name;
    private String password;
    private String type;

    private String status;

    private Integer roleId;
    private String roleName;

    /**	权限列表*/
    private Map<String,Object> urlMap;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getRoleId() {
        return roleId;
    }
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Map<String, Object> getUrlMap() {
        return urlMap;
    }
    public void setUrlMap(Map<String, Object> urlMap) {
        this.urlMap = urlMap;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
