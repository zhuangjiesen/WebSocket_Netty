package com.jason.activiti.listener.execution;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/8/6
 */
public class SubEndExecutionListener implements ExecutionListener {
    public Expression desc;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        System.out.println("SubEndExecutionListener - name : " + desc.getExpressionText() + " , eventName : " + execution.getEventName() + " , id : " + execution.getId());
    }
}
