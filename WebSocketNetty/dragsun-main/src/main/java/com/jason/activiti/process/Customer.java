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
 * 客户(广告主)
 * @Date: Created in 2018/8/6
 */
public class Customer {

    private ProcessEngine processEngine;

    private String userGroup = "Customer";
    private String taskAssignee1 = "customer_confirm_project";
    private String taskAssignee2 = "customer_pick_pass";
    private String taskAssignee3 = "customer_choose";
    private String taskAssignee4 = "choose_success";

    public Customer(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    /**
     * 发布需求
     * @author zhuangjiesen
     * @date 2018/8/7 下午9:14
     * @param
     * @return
     */
    public void arProjectSubmit() {
        List<Task> list = null;
        list = (processEngine.getTaskService()).createTaskQuery().taskAssignee(taskAssignee1).list();
        if (list != null) {
            list.forEach(new Consumer<Task>() {
                @Override
                public void accept(Task task) {
                    System.out.println("Customer - 客户方发布ar需求 - start - taskId : " + task.getId() + " taskName : " + task.getName() );
                    processEngine.getTaskService().complete(task.getId());
                    System.out.println("Customer - 客户方发布ar需求 - taskId : " + task.getId() + " finished ! ");
                }
            });
        } else {
            System.out.println("Customer - 客户方发布ar需求 - 当前没有任务 !");
        }
    }




    /**
     * 选择服务商并发布需求
     * @author zhuangjiesen
     * @date 2018/8/7 下午9:15
     * @param
     * @return
     */
    public void confirmAndPickService(){
        List<Task> list = null;
        list = (processEngine.getTaskService()).createTaskQuery().taskAssignee(taskAssignee2).list();
        if (list != null) {
            System.out.println("Customer - 客户方 - 选择服务商并发布需求 - list : " + list.size());
            list.forEach(new Consumer<Task>() {
                @Override
                public void accept(Task task) {

                    System.out.println("Customer - 客户方 - 选择服务商并发布需求 - start - taskId : " + task.getId() + " taskName : " + task.getName() );
                    processEngine.getTaskService().complete(task.getId());
                    System.out.println("Customer - 客户方 - 选择服务商并发布需求 - taskId : " + task.getId() + " finished ! ");
                }
            });
        } else {
            System.out.println("Customer - 客户方 - 选择服务商并发布需求 - 当前没有任务 !");
        }
    }




    private boolean isFirst = true;

    /**
     * 选择ISV服务商
     * @author zhuangjiesen
     * @date 2018/8/7 下午9:15
     * @param
     * @return
     */
    public void chooseOnePrice(){
        List<Task> list = null;
        list = (processEngine.getTaskService()).createTaskQuery().taskAssignee(taskAssignee3).list();
        if (list != null) {
            System.out.println("Customer - 客户选择ISV服务商，并提交 - list : " + list.size());
            list.forEach(new Consumer<Task>() {
                @Override
                public void accept(Task task) {
                    //设置参数
                    System.out.println("Customer - 客户选择ISV服务商，并提交 - start - taskId : " + task.getId() + " taskName : " + task.getName() );
                    HashMap<String , Object> variable = new HashMap<String, Object>();
                    if (isFirst) {
                        variable.put("choose" , "true");
                        isFirst = false;
                    } else {
                        variable.put("choose" , "false");
                    }
                    processEngine.getTaskService().complete(task.getId() , variable);
                    System.out.println("Customer - 客户选择ISV服务商，并提交 - taskId : " + task.getId() + " finished ! ");
                }
            });
        } else {
            System.out.println("Customer - 客户选择ISV服务商，并提交 - 当前没有任务 !");
        }
    }



    /**
     * 成功选择ISV服务商完成
     * @author zhuangjiesen
     * @date 2018/8/7 下午9:15
     * @param
     * @return
     */
    public void chooseSuccess(){
        List<Task> list = null;
        list = (processEngine.getTaskService()).createTaskQuery().taskAssignee(taskAssignee4).list();
        if (list != null) {
            System.out.println("Customer - 客户与ISV确认合作 - list : " + list.size());
            list.forEach(new Consumer<Task>() {
                @Override
                public void accept(Task task) {
                    System.out.println("Customer - 客户与ISV确认合作 - start - taskId : " + task.getId() + " taskName : " + task.getName() );
                    processEngine.getTaskService().complete(task.getId());
                    System.out.println("Customer - 客户与ISV确认合作 - taskId : " + task.getId() + " finished ! ");
                }
            });
        } else {
            System.out.println("Customer - 客户与ISV确认合作 - 当前没有任务 !");
        }
    }


}
