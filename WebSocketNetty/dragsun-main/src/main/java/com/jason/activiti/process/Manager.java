package com.jason.activiti.process;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * 洞见管理员
 * @Date: Created in 2018/8/6
 */
public class Manager {

    private ProcessEngine processEngine;

    private String userGroup = "Manager";
    private String taskAssignee = "manager_pass_project";
    private String taskAssignee2 = "manager_pass_bid";



    public Manager(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    /**
     * 审核ar需求，并设置服务提供商选择
     * @author zhuangjiesen
     * @date 2018/8/7 下午9:16
     * @param
     * @return
     */
    public void passArProject() {
        List<Task> list = null;
        list = (processEngine.getTaskService()).createTaskQuery().taskAssignee(taskAssignee).list();
        if (list != null) {
            System.out.println("Manager - 洞见管理员 - 审核ar需求，并设置服务提供商选择 - list : " + list.size());
            list.forEach(new Consumer<Task>() {
                @Override
                public void accept(Task task) {
                    HashMap<String ,Object> variable = new HashMap<String, Object>();
                    variable.put("pass" , "true");
                    System.out.println("Manager - 洞见管理员 - 审核ar需求，并设置服务提供商选择  - start ... " );
                    processEngine.getTaskService().complete(task.getId() , variable );
                    System.out.println("Manager - 洞见管理员 - 审核ar需求，并设置服务提供商选择  - taskId : " + task.getId() + " taskName : " + task.getName() + " finished ... " );
                }
            });
        } else {
            System.out.println("Manager - 洞见管理员 - 审核ar需求，并设置服务提供商选择 - 当前没有任务 !");
        }
    }





    /**
     * 报价审核
     * @author zhuangjiesen
     * @date 2018/8/7 下午9:16
     * @param
     * @return
     */
    public void passArProjectPrice() {
        List<Task> list = null;
        list = (processEngine.getTaskService()).createTaskQuery().taskAssignee(taskAssignee2).list();
        if (list != null) {
            System.out.println("Manager - 洞见管理员 - 对ISV报价审核 - list : " + list.size());
            list.forEach(new Consumer<Task>() {
                @Override
                public void accept(Task task) {
                    HashMap<String ,Object> variable = new HashMap<String, Object>();
                    variable.put("pass" , "true");
                    processEngine.getTaskService().complete(task.getId() , variable );
                    System.out.println("Manager - 洞见管理员 - 对ISV报价审核  - taskId : " + task.getId() + " taskName : " + task.getName() );
                }
            });
        } else {
            System.out.println("Manager - 洞见管理员 - 对ISV报价审核 - 当前没有任务 !");
        }
    }








}