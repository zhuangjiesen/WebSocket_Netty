package com.jason.activiti.listener.activiti;

import com.jason.activiti.constants.VariableKey;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * 全局事件监听
 * @Date: Created in 2018/8/8
 */
@Component
public class GlobalActivitiEventListener implements ActivitiEventListener {


    @Autowired
    private ProcessEngine processEngine;

    @Override
    public void onEvent(ActivitiEvent event) {


        System.out.println(
        String.format(" - GlobalActivitiEventListener - onEvent , name : %s , ProcessDefinitionId : %s "
                , event.getType().name()
                , event.getProcessDefinitionId()
        ));
        if ("TASK_COMPLETED".equals(event.getType().name())) {

            System.out.println(
                String.format(" - GlobalActivitiEventListener - onEvent , name : %s , ProcessDefinitionId : %s , ExecutionId : %s , ProcessInstanceId : %s "
                    , event.getType().name()
                    , event.getProcessDefinitionId()
                        , event.getExecutionId()
                        , event.getProcessInstanceId()
            ));
            TaskService taskService = event.getEngineServices().getTaskService();

            //给新的task指派任务接受者
            List<Task> taskList = taskService.createTaskQuery().executionId(event.getExecutionId()).list();
            if (taskList != null) {
                System.out.println(" - - GlobalActivitiEventListener - - taskList : " + taskList.size());
                Map<String , Integer> taskMap = new HashMap<>();
                for (Task taskItem : taskList) {
                    //防止 Assignee 不为空导致指派不了用户
                    taskService.setAssignee(taskItem.getId() , null);
                    System.out.println(String.format("- GlobalActivitiEventListener - task - name : %s , taskId : %s " , taskItem.getName() , taskItem.getId()));
                    String userList = (String) taskService.getVariable(taskItem.getId() , VariableKey.DELEGATE_USER_LIST);
                    if (StringUtils.isNotEmpty(userList)) {
                        String[] userArray = userList.split(",");
                        if (userArray.length == 1) {
                            System.out.println("- GlobalActivitiEventListener - delegate - CandidateUser : " + userArray[0]);
                            taskService.addCandidateUser(taskItem.getId() , userArray[0]);
                        } else if (userArray.length > 1){
                            int index = 0;
                            if (!taskMap.containsKey(userList)) {
                                taskMap.put(userList , index );
                            } else {
                                index = taskMap.get(userList) + 1;
                                taskMap.put(userList , index);
                            }
                            System.out.println("- GlobalActivitiEventListener - delegate - CandidateUser : " + userArray[index] + " , index : " + index);
                            taskService.addCandidateUser(taskItem.getId() , userArray[index]);
                        }
                    } else {
                        String userGroupVar = (String) taskService.getVariable(taskItem.getId() , VariableKey.DELEGATE_USER_GROUP);
                        System.out.println("- GlobalActivitiEventListener - delegate - CandidateGroup : " + userGroupVar);
                        if (StringUtils.isNotEmpty(userGroupVar)) {
                            taskService.addCandidateGroup(taskItem.getId() , userGroupVar);
                        }
                    }
                }
            }
        }
    }






    public void delegateTask(){

    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
