package com.jason.activiti.listener.task;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * assignment , create , complete , delete
 * @Date: Created in 2018/8/6
 */
public class CustomerLister implements TaskListener {

    public Expression desc;


    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(String.format(" - CustomerLister -taskId : %s , desc : %s , eventName : %s , taskName : %s "
                , delegateTask.getId()
                , desc.getExpressionText()
                , delegateTask.getEventName()
                , delegateTask.getName()
        ));

        if ("create".equals(delegateTask.getEventName())) {
//            PaiDanMain.processEngine.getRuntimeService().setVariable(delegateTask.getExecutionId() , "subprocessCount" , 2);
//            System.out.println(" - CustomerLister - 添加线程数- ");
        }


    }

}
