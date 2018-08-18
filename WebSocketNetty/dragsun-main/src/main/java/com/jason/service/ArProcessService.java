package com.jason.service;

import com.jason.activiti.constants.ArProjectStatus;
import com.jason.activiti.constants.UserGroup;
import com.jason.activiti.constants.VariableKey;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * ar项目操作类
 * @Date: Created in 2018/8/8
 */
@Service
public class ArProcessService {

    @Autowired
    private TaskService taskService;


    @Autowired
    private RuntimeService runtimeService;

    /**
     * 完成任务并指派
     * @author zhuangjiesen
     * @date 2018/8/8 下午7:04
     * @param
     * @return
     */
    public void comleteAndDelegate(String taskId , UserGroup userGroup , String userId , Map<String, Object> variables){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            if (userGroup != null) {
                taskService.setVariable(taskId , VariableKey.DELEGATE_USER_GROUP, userGroup.getCode());
            } else {
                taskService.setVariable(taskId , VariableKey.DELEGATE_USER_GROUP, null);
            }
            if (userId != null) {
                taskService.setVariable(taskId , VariableKey.DELEGATE_USER_LIST , userId );
            } else {
                taskService.setVariable(taskId , VariableKey.DELEGATE_USER_LIST , null );
            }
            if (variables != null) {
                taskService.complete(taskId , variables);
            } else {
                taskService.complete(taskId);
            }
            delegateTask();
        }

    }



    public void delegateTask(){
        //给新的task指派任务接受者
        List<Task> taskList = this.getTaskList( 0 , 500);
        if (taskList != null) {
            System.out.println(" - delegateTask - taskList : " + taskList.size());
            Map<String , Integer> taskMap = new HashMap<>();
            for (Task taskItem : taskList) {
                //防止 Assignee 不为空导致指派不了用户
                taskService.setAssignee(taskItem.getId() , null);
                System.out.println(String.format("task - name : %s , taskId : %s " , taskItem.getName() , taskItem.getId()));
                String userList = (String) taskService.getVariable(taskItem.getId() , VariableKey.DELEGATE_USER_LIST);
                if (StringUtils.isNotEmpty(userList)) {
                    String[] userArray = userList.split(",");
                    if (userArray.length == 1) {
                        System.out.println("delegate - CandidateUser : " + userArray[0]);
                        taskService.addCandidateUser(taskItem.getId() , userArray[0]);
                    } else if (userArray.length > 1){
                        int index = 0;
                        if (!taskMap.containsKey(userList)) {
                            taskMap.put(userList , index );
                        } else {
                            index = taskMap.get(userList) + 1;
                            taskMap.put(userList , index);
                        }
                        System.out.println("delegate - CandidateUser : " + userArray[index] + " , index : " + index);
                        taskService.addCandidateUser(taskItem.getId() , userArray[index]);
                    }
                } else {
                    String userGroupVar = (String) taskService.getVariable(taskItem.getId() , VariableKey.DELEGATE_USER_GROUP);
                    System.out.println("delegate - CandidateGroup : " + userGroupVar);
                    if (StringUtils.isNotEmpty(userGroupVar)) {
                        taskService.addCandidateGroup(taskItem.getId() , userGroupVar);
                    }
                }
            }
        }
    }

    public void comleteAndDelegate(String taskId , UserGroup userGroup , String userId) {
        this.comleteAndDelegate(taskId, userGroup , userId , null);
    }


    public void comleteAndDelegate(String taskId , UserGroup userGroup ){
        this.comleteAndDelegate(taskId, userGroup , null , null);
    }


    public void comleteAndDelegate(String taskId , UserGroup userGroup , Map<String, Object> variables ){
        this.comleteAndDelegate(taskId, userGroup , null , variables);
    }




    /**
     * 查询任务列表方法
     * @author zhuangjiesen
     * @date 2018/8/8 下午5:10
     * @param
     * @return
     */
    public List<Task> getTaskList( ArProjectStatus arProjectStatus , UserGroup userGroup , String userId , int start , int size){
        List<Task> list = null;
        TaskQuery taskQuery  = taskService.createTaskQuery();
        taskQuery.orderByTaskCreateTime().desc();
        if (arProjectStatus != null) {
            taskQuery.taskDefinitionKey(arProjectStatus.getCode());
        }
        if (userId != null) {
            taskQuery.taskCandidateUser(userId);
        } else if (userGroup != null) {
            taskQuery.taskCandidateGroup(userGroup.getCode());
//            taskQuery.taskAssignee(userGroup.getCode());
        }

        list = taskQuery.listPage(start , size);
        return list;
    }

    public Task getTask(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }





    public List<Task> getTaskList(int start , int size){
        return this.getTaskList(null , null , null , start , size);
    }



}
