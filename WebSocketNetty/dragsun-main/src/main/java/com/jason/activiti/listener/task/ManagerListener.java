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
public class ManagerListener implements TaskListener {

    public Expression desc;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(String.format(" - ManagerListener - taskId : %s , assignee : %s  , desc : %s , eventName : %s "
                , delegateTask.getId()
                , delegateTask.getAssignee()
                , desc.getExpressionText()
                , delegateTask.getEventName()));
        if ("create".equals(delegateTask.getEventName())) {
        }
    }


}
