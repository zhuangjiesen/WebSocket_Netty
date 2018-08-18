package com.jason.activiti.configure;

import com.jason.activiti.listener.activiti.GlobalActivitiEventListener;
import org.activiti.engine.*;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/8/8
 */
@Configuration
public class ActivitiConfig  {


    @Autowired
    private ProcessEngine processEngine;


    @Bean
    public ProcessEngine processEngine() {

        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        StandaloneProcessEngineConfiguration standaloneProcessEngineConfiguration = null;
        if (processEngineConfiguration instanceof StandaloneProcessEngineConfiguration) {
            standaloneProcessEngineConfiguration = (StandaloneProcessEngineConfiguration)processEngineConfiguration;
            //连接数据库的配置
            standaloneProcessEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
            standaloneProcessEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/activiti_pro?useUnicode=true&characterEncoding=utf8");
            standaloneProcessEngineConfiguration.setJdbcUsername("root");
            standaloneProcessEngineConfiguration.setJdbcPassword("123");
            standaloneProcessEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

            List<ActivitiEventListener> listeners = new ArrayList<>();
            listeners.add(new GlobalActivitiEventListener());
//            standaloneProcessEngineConfiguration.setEventListeners(listeners);

            //工作流的核心对象，ProcessEnginee对象
            ProcessEngine processEngine = standaloneProcessEngineConfiguration.buildProcessEngine();

            //3.通过processEngine对象获取activiti的service
            RepositoryService repositoryService = processEngine.getRepositoryService();
            //4.通过RepositoryService发布流程到数据库
            repositoryService.createDeployment().addClasspathResource("process/pd_project.bpmn").deploy();
            return processEngine;

        } else {
            throw new RuntimeException("Activiti 流初始化出错 ！");
        }

    }



    @Bean
    public RuntimeService runtimeService(){
        return processEngine.getRuntimeService();
    }


    @Bean
    public HistoryService historyService(){
        return processEngine.getHistoryService();
    }


    @Bean
    public TaskService taskService() {
        return processEngine.getTaskService();
    }





}
