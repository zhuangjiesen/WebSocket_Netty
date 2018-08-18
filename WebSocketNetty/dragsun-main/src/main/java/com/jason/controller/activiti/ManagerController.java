package com.jason.controller.activiti;

import com.alibaba.fastjson.JSONObject;
import com.jason.activiti.SignComplete;
import com.jason.activiti.constants.ArProjectStatus;
import com.jason.activiti.constants.UserGroup;
import com.jason.activiti.constants.VariableKey;
import com.jason.activiti.vo.ArProject;
import com.jason.service.ArProcessService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/8/8
 */
@Controller
@RequestMapping("/activiti/manager")
public class ManagerController {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ArProcessService arProcessService;

    /**
     * 管理员 - 获取需求列表
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/getArProjectList")
    @ResponseBody
    public Object getArProjectList(HttpServletRequest request , HttpServletResponse response){
        List<Task> taskList = arProcessService.getTaskList(ArProjectStatus.MANAGER_PASS_PJT , UserGroup.MANAGER, 0 , 10 );
        System.out.println("- getArProjectList — taskList : " + taskList.size());

        List<ArProject> arProjectList = new ArrayList<>();
        for (Task task : taskList) {
            System.out.println(String.format("- getArProjectList — task , taskId : %s , name : %s "
                    , task.getId()
                    , task.getName() ));
            ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
            System.out.println("- getArProjectList — arProject : " + JSONObject.toJSONString(arProject));
            System.out.println("- getArProjectList — CreateUserId : " + arProject.getCreateUserId());

            arProject.setTaskId(task.getId());
            arProjectList.add(arProject);
        }

        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("arProjectList" , arProjectList);
        result.put("code" , 200);
        return result;
    }



    /**
     * 管理员 - 审核需求
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/passArProject")
    @ResponseBody
    public Object getArProjectList(String taskId , HttpServletRequest request , HttpServletResponse response){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        System.out.println(String.format("- passArProject — task , taskId : %s , name : %s "
                , task.getName()
                , task.getName() ));

        ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
        System.out.println("- passArProject - 审核需求 — arProject : " + JSONObject.toJSONString(arProject));
        System.out.println("- passArProject - 审核需求 — CreateUserId : " + arProject.getCreateUserId());


        //发给管理员自己，选择服务商
        Map<String, Object> variables = new HashMap<>();
        variables.put("pass" , "true");
        arProcessService.comleteAndDelegate(taskId , UserGroup.MANAGER , variables);

        List<Task> taskList = arProcessService.getTaskList(ArProjectStatus.MANAGER_CHOOSE_ISV , UserGroup.MANAGER, 0 , 10 );
        System.out.println("- passArProject - 选择ISV服务商 — taskList : " + taskList.size());
        if (taskList.size() > 0) {
            task = taskList.get(0);

            arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
            System.out.println("- passArProject - 选择ISV服务商 — arProject : " + JSONObject.toJSONString(arProject));
            System.out.println("- passArProject - 选择ISV服务商 — CreateUserId : " + arProject.getCreateUserId());
            arProject.setTaskId(task.getId());
            //完成选择服务商
            arProcessService.comleteAndDelegate(task.getId() , UserGroup.CUSTOMER , arProject.getCreateUserId() );
        } else {
            System.out.println("- passArProject - 啥也没有!!!");
        }


        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("arProject" , arProject);
        result.put("code" , 200);
        return result;
    }




    /**
     * 管理员 - 获取竞标列表
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/getBidPlanList")
    @ResponseBody
    public Object getBidPlanList(HttpServletRequest request , HttpServletResponse response){
        List<Task> taskList = arProcessService.getTaskList(ArProjectStatus.MANAGER_PASS_PRICE , UserGroup.MANAGER, 0 , 10 );
        System.out.println("- getBidPlanList — taskList : " + taskList.size());

        List<ArProject> arProjectList = new ArrayList<>();
        for (Task task : taskList) {
            System.out.println(String.format("- getBidPlanList — task , taskId : %s , name : %s "
                    , task.getId()
                    , task.getName() ));
            ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
            System.out.println("- getBidPlanList — arProject : " + JSONObject.toJSONString(arProject));
            System.out.println("- getBidPlanList — CreateUserId : " + arProject.getCreateUserId());

            arProject.setTaskId(task.getId());
            arProjectList.add(arProject);
        }

        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("arProjectList" , arProjectList);
        result.put("code" , 200);
        return result;
    }






    /**
     * 管理员 - 审核竞标报价
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/passBidPlan")
    @ResponseBody
    public Object passBidPlan(String taskId , HttpServletRequest request , HttpServletResponse response){
        Task task = arProcessService.getTask(taskId);
        System.out.println(String.format("- passArProject — task , taskId : %s , name : %s "
                , task.getName()
                , task.getName() ));

        ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
        System.out.println("- passBidPlan - 审核需求 — arProject : " + JSONObject.toJSONString(arProject));
        System.out.println("- passBidPlan - 审核需求 — CreateUserId : " + arProject.getCreateUserId());

        //审核通过
        Map<String, Object> variables = new HashMap<>();
        variables.put("pass" , "true");
        arProcessService.comleteAndDelegate(taskId , UserGroup.CUSTOMER , arProject.getCreateUserId() , variables);

        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("arProject" , arProject);
        result.put("code" , 200);
        return result;
    }





}
