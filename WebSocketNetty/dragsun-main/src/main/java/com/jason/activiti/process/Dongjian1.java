package com.jason.activiti.process;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.function.Consumer;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * ISV对象
 * @Date: Created in 2018/8/6
 */
public class Dongjian1 implements MRClient {

    private ProcessEngine processEngine;

    public Dongjian1(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    private String userId = "MR_dongjian1";


    @Override
    public void bidProject() {
        List<Task> list = null;
        list = (processEngine.getTaskService()).createTaskQuery().taskCandidateUser(userId).list();
        if (list != null) {
            System.out.println("Dongjian1 - 洞见MR-ISV用户，竞标，报价、提案 - list : " + list.size());
            list.forEach(new Consumer<Task>() {
                @Override
                public void accept(Task task) {
                    System.out.println("Dongjian1 - 洞见MR-ISV用户，竞标，报价、提案  - start ");
                    processEngine.getTaskService().complete(task.getId());
                    System.out.println("Dongjian1 - 洞见MR-ISV用户，竞标，报价、提案  - taskId : " + task.getId() + " taskName : " + task.getName() );
                }
            });
        } else {
            System.out.println("Dongjian1 - 洞见MR-ISV用户，竞标，报价、提案 - 当前没有任务 !");
        }
    }
}
