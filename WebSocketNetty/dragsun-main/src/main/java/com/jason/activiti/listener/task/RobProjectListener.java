package com.jason.activiti.listener.task;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/8/6
 */
public class RobProjectListener implements TaskListener {

    public Expression desc;

    private boolean isDongjian1 = false;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(" - RobProjectListener - name : " + desc.getExpressionText() + " eventName : " + delegateTask.getEventName());
        if ("create".equals(delegateTask.getEventName())) {
            System.out.println(" - RobProjectListener - 添加用户组 - ");
            if (!isDongjian1) {
                System.out.println("--1--");
                delegateTask.addCandidateUser("MR_dongjian1");
                isDongjian1 = true;
            } else {
                System.out.println("--2--");
                delegateTask.addCandidateUser("MR_dongjian2");
            }
        }

    }


}
