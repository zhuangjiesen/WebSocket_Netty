package com.jason.activiti.constants;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/8/8
 */
public enum UserGroup {

    CUSTOMER(1, "customer" , "客户方"),
    MANAGER(2 , "manager" , "管理员"),
    MRCLIENT(3 , "mrclient" , "ISV服务商"),
    ;

    private int type;
    private String code;
    private String desc;

    UserGroup(int type, String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
