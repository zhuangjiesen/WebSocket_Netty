package com.jason.activiti.constants;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * 1-草稿状态,
 * 2-待审核,
 * 3-审核通过，待选择ISV,
 * 4-完成管理员选择ISV，
 * 5-客户完成选择ISV,
 * 6-ISV确定合作（如果说ISV都不合作？），提供报价、方案、项目周期等信息,并且待审核
 * 7-审核通过报价信息（如果三个报价都不通过， 重新报价5）
 * 8-客户选择完成ISV（如果客户不选择所有的报价，重新报价5？），MR中为项目进行中
 * 9-项目完成，待管理员成果审核
 * 10-管理员审核通过，并且客户可以进行成果验收（如果客户不通过到8？）
 * 11-验收成功，变成已完成
 * @Date: Created in 2018/8/8
 */
public enum ArProjectStatus {


    CUSTOMER_NOT_SUBMIT(1 , "customer_not_submit" , "草稿状态"),
    CUSTOMER_SUBMIT_PJT(2 , "customer_submit_pjt" , "待审核"),
    MANAGER_PASS_PJT(3 , "manager_pass_pjt" , "审核通过，待选择ISV"),
    MANAGER_CHOOSE_ISV(4 , "manager_choose_isv" , "完成管理员选择ISV"),
    CUSTOMER_CHOOSE_ISV(5 , "customer_choose_isv" , "客户完成选择ISV"),
    ISV_SUBMIT_PRICE(6 , "isv_submit_price" , "ISV确定合作（如果说ISV都不合作？），提供报价、方案、项目周期等信息,并且待审核"),
    MANAGER_PASS_PRICE(7 , "manager_pass_price" , "审核通过报价信息（如果三个报价都不通过， 重新报价5）"),
    CUSTOMER_CHOOSE_SUCCESS(8 , "customer_choose_success" , "客户选择完成ISV（如果客户不选择所有的报价，重新报价5？），MR中为项目进行中"),
    ISV_COOR_SUCCESS(9 , "isv_coor_success" , "确认合作"),
    ISV_UPLOAD_RESULT(10 , "isv_upload_result" , "项目完成，ISV上传成果物，待管理员成果审核"),
    MANAGER_PASS_RESULT(11 , "manager_pass_result" , "管理员审核通过，并且客户可以进行成果验收（如果客户不通过到8？）"),
    CUSTOMER_CHECK_RESULT_SUCCESS(12 , "customer_check_result_success" , "客户方 - 验收成功，变成已完成"),


    ;
    private int status;
    private String code;
    private String desc;

    ArProjectStatus(int status, String code, String desc) {
        this.status = status;
        this.code = code;
        this.desc = desc;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
