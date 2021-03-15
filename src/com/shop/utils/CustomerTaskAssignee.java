package com.shop.utils;

import javax.servlet.http.HttpServletRequest;

import com.shop.service.EmployeeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shop.pojo.ActiveUser;
import com.shop.pojo.Employee;

public class CustomerTaskAssignee implements TaskListener {

    private static final long serialVersionUID = 1L;


    @Override
    public void notify(DelegateTask delegateTask) {


        //spring容器
        WebApplicationContext context =ContextLoader.getCurrentWebApplicationContext();
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

        /*从新查询当前用户，再获取当前用户对应的领导*/
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();

        EmployeeService employeeService = (EmployeeService) context.getBean("employeeService");
        Employee manager = employeeService.findEmployeeManager(activeUser.getManagerId());
        //设置个人任务的办理人
        delegateTask.setAssignee(manager.getName());
        delegateTask.setAssignee(manager.getName());


    }

}
