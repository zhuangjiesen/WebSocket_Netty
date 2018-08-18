package com.jason.controller.activiti;

import com.alibaba.fastjson.JSONObject;
import com.jason.activiti.constants.ArProjectStatus;
import com.jason.activiti.constants.UserGroup;
import com.jason.activiti.constants.VariableKey;
import com.jason.activiti.vo.ArProject;
import com.jason.activiti.vo.BidPlan;
import com.jason.service.ArProcessService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
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

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/8/8
 */
@Controller
@RequestMapping("/activiti/mrisv")
public class MrISVController {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ArProcessService arProcessService;


    /**
     * isv - 收到需求列表
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/getCustomerArProjectList")
    @ResponseBody
    public Object getCustomerArProjectList(HttpServletRequest request , HttpServletResponse response){
        String userId = request.getHeader("userId");
        System.out.println("- getCustomerArProjectList - userId : " + userId);
        List<Task> taskList = arProcessService.getTaskList(ArProjectStatus.ISV_SUBMIT_PRICE , UserGroup.MRCLIENT , userId , 0 , 10 );
        System.out.println("- getCustomerArProjectList — taskList : " + taskList.size());


        List<ArProject> arProjectList = new ArrayList<>();
        for (Task task : taskList) {
            System.out.println(String.format("- getPassedArProjectList — task , taskId : %s , name : %s "
                    , task.getId()
                    , task.getName() ));
            ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
            System.out.println("- getCustomerArProjectList — arProject : " + JSONObject.toJSONString(arProject));
            System.out.println("- getCustomerArProjectList — CreateUserId : " + arProject.getCreateUserId());

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
     * isv - 提交竞标
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/submitBidPlan")
    @ResponseBody
    public Object submitBidPlan(String taskId , @RequestBody BidPlan bidPlan , HttpServletRequest request , HttpServletResponse response){
        String userId = request.getHeader("userId");
        System.out.println("- submitBidPlan - userId : " + userId);

        Task task = arProcessService.getTask(taskId);
        ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
        arProject.getBidPlanList().add(bidPlan);
        runtimeService.setVariable(task.getExecutionId() ,  VariableKey.ENTITY_ARPROJECT , arProject);

        System.out.println("- submitBidPlan - taskId : " + taskId + " , taskName : " + task.getName());
        arProcessService.comleteAndDelegate(taskId , UserGroup.MANAGER);

        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "操作成功");
        result.put("arProject" , arProject);
        result.put("code" , 200);
        return result;
    }





    /**
     * isv - 获取确认合作列表
     * @author zhuangjiesen
     * @date 2018/8/8 下午9:12
     * @param
     * @return
     */
    @RequestMapping("/getCoorSuccessList")
    @ResponseBody
    public Object getCoorSuccessList(HttpServletRequest request , HttpServletResponse response){
        String userId = request.getHeader("userId");
        System.out.println("- getCoorSuccessList - userId : " + userId);
        List<Task> taskList = arProcessService.getTaskList(ArProjectStatus.ISV_COOR_SUCCESS , UserGroup.MRCLIENT , userId , 0 , 10 );
        System.out.println("- getCoorSuccessList — taskList : " + taskList.size());


        List<ArProject> arProjectList = new ArrayList<>();
        for (Task task : taskList) {
            System.out.println(String.format("- getCoorSuccessList — task , taskId : %s , name : %s "
                    , task.getId()
                    , task.getName() ));
            ArProject arProject = (ArProject) runtimeService.getVariable(task.getExecutionId() , VariableKey.ENTITY_ARPROJECT );
            System.out.println("- getCoorSuccessList — arProject : " + JSONObject.toJSONString(arProject));
            System.out.println("- getCoorSuccessList — CreateUserId : " + arProject.getCreateUserId());

            arProject.setTaskId(task.getId());
            arProjectList.add(arProject);
        }

        HashMap<String , Object> result = new HashMap<>();
        result.put("message" , "确认合作完成！！");
        result.put("arProjectList" , arProjectList);
        result.put("code" , 200);
        return result;
    }









}
