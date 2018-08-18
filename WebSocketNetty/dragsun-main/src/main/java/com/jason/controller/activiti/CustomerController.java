package com.jason.controller.activiti;

import com.alibaba.fastjson.JSONObject;
import com.jason.activiti.SignComplete;
import com.jason.activiti.constants.ArProjectStatus;
import com.jason.activiti.constants.UserGroup;
import com.jason.activiti.constants.VariableKey;
import com.jason.activiti.vo.ArProject;
import com.jason.activiti.vo.BidPlan;
import com.jason.service.ArProcessService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
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
@RequestMapping("/activiti/customer")
public class CustomerController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ArProcessService arProcessService;

    @RequestMapping("/submitArProject")
    @ResponseBody
    public Object submitArProject(@RequestBody ArProject arProject , HttpServletRequest request , HttpServletResponse response){
        System.out.println(String.format("- submitArProject - arProject : " , JSONObject.toJSONString(arProject)));
        Map<String, Object> variables = new HashMap();
        variables.put("signComplete" , new SignComplete());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("pd_process" , variables);
        System.out.println(String.format("processInstance - id : %s , processInstanceId : %s "
                , processInstance.getId()
                , processInstance.getProcessInstanceId()));
        runtimeService.setVariable(processInstance.getId() , VariableKey.ENTITY_ARPROJECT , arProject);

        Task task = taskService.createTaskQuery().executionId(processInstance.getId()).taskDefinitionKey(ArProjectStatus.CUSTOMER_SUBMIT_PJT.getCode()).singleResult();
        arProcessService.comleteAndDelegate(task.getId() , UserGroup.MANAGER );

        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("code" , 200);
        return result;
    }



    /**
     * 客户方 - 获取通过需求列表
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/getPassedArProjectList")
    @ResponseBody
    public Object getPassedArProjectList(HttpServletRequest request , HttpServletResponse response){
        String userId = request.getHeader("userId");
        System.out.println("- getPassedArProjectList - userId : " + userId);
        List<Task> taskList = arProcessService.getTaskList(ArProjectStatus.CUSTOMER_CHOOSE_ISV , UserGroup.CUSTOMER , userId , 0 , 10 );
        System.out.println("- getPassedArProjectList — taskList : " + taskList.size());


        List<ArProject> arProjectList = new ArrayList<>();
        for (Task task : taskList) {
            System.out.println(String.format("- getPassedArProjectList — task , taskId : %s , name : %s "
                    , task.getId()
                    , task.getName() ));
            ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
            System.out.println("- getPassedArProjectList — arProject : " + JSONObject.toJSONString(arProject));
            System.out.println("- getPassedArProjectList — CreateUserId : " + arProject.getCreateUserId());

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
     * 客户方 - 发送需求
     * @author zhuangjiesen
     * @date 2018/8/9 上午8:49
     * @param
     * @return
     */
    @RequestMapping("/publishArProject")
    @ResponseBody
    public Object publishArProject(String taskId , String isvServerIds , HttpServletRequest request , HttpServletResponse response){
        System.out.println("- publishArProject - isvServerIds : " + isvServerIds);
        String userId = request.getHeader("userId");

        Task task = arProcessService.getTask(taskId);
        System.out.println(String.format("- getPassedArProjectList — task , taskId : %s , name : %s "
                , task.getId()
                , task.getName() ));
        ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
        System.out.println("- publishArProject — arProject : " + JSONObject.toJSONString(arProject));
        System.out.println("- publishArProject — CreateUserId : " + arProject.getCreateUserId());
        //发送需求并选择3家服务商

        if (StringUtils.isNotEmpty(isvServerIds)) {
            String[] isvServerIdArr = isvServerIds.split(",");
            runtimeService.setVariable(task.getExecutionId() , "subprocessCount" , isvServerIdArr.length);
            arProcessService.comleteAndDelegate(taskId , UserGroup.MRCLIENT , isvServerIds);
        }

        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("code" , 200);
        return result;
    }





    /**
     * 客户方 - 获取审核通过的报价列表
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/getPassedBidPlanList")
    @ResponseBody
    public Object getPassedBidPlanList(HttpServletRequest request , HttpServletResponse response){
        String userId = request.getHeader("userId");
        System.out.println("- getPassedBidPlanList - userId : " + userId);
        List<Task> taskList = arProcessService.getTaskList(ArProjectStatus.CUSTOMER_CHOOSE_SUCCESS , UserGroup.CUSTOMER , userId , 0 , 10 );
        System.out.println("- getPassedBidPlanList — taskList : " + taskList.size());

        List<ArProject> arProjectList = new ArrayList<>();
        for (Task task : taskList) {
            System.out.println(String.format("- getPassedBidPlanList — task , taskId : %s , name : %s "
                    , task.getId()
                    , task.getName() ));
            ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
            System.out.println("- getPassedBidPlanList — arProject : " + JSONObject.toJSONString(arProject));
            System.out.println("- getPassedBidPlanList — CreateUserId : " + arProject.getCreateUserId());

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
     * 客户方 - 选择ar服务商
     * @author zhuangjiesen
     * @date 2018/8/9 上午8:49
     * @param
     * @return
     */
    @RequestMapping("/chooseIveSuccess")
    @ResponseBody
    public Object chooseIveSuccess(String taskId , Long bidPlanId , String isvServerIds , HttpServletRequest request , HttpServletResponse response){
        System.out.println("- publishArProject - isvServerIds : " + isvServerIds);
        String userId = request.getHeader("userId");

        Task task = arProcessService.getTask(taskId);
        ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
        System.out.println("- publishArProject — arProject : " + JSONObject.toJSONString(arProject));
        List<BidPlan> bidPlanList = arProject.getBidPlanList();
        for (BidPlan bidPlan : bidPlanList) {
            if (bidPlan.getId().equals(bidPlanId)) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("choose" , "true");
                arProcessService.comleteAndDelegate(taskId , UserGroup.MRCLIENT , bidPlan.getCreateUserId() , variables);
            }
        }


        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("code" , 200);
        return result;
    }






}
