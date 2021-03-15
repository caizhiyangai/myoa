package com.shop.service.impl;

import com.shop.mapper.BaoxiaobillMapper;
import com.shop.pojo.Baoxiaobill;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service("workFlowService")
public class WorkFlowServiceImpl implements WorkFlowService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BaoxiaobillMapper baoxiaoBillMapper;

    @Override
    public void saveNewDeploy(InputStream in, String fileName) {
        ZipInputStream zip = new ZipInputStream(in);
        repositoryService.createDeployment()
                .name(fileName)
                .addZipInputStream(zip)
                .deploy();
    }

    @Override
    public List<Deployment> findDeploymentList() {
        return repositoryService.createDeploymentQuery().list();
    }

    @Override
    public List<ProcessDefinition> findProcessesDefinition() {
        return repositoryService.createProcessDefinitionQuery().list();
    }

    @Override
    public void delDeployment(String deploymentId) {
        //使用级联删除
        repositoryService.deleteDeployment(deploymentId,true);
    }

    @Override
    public void startBaoxiaoFlow(Long baoxiaoId, String assigneeName) {
        //得到流程key
        String baoxiaokey = Constants.Baoxiao_KEY;
        //拼接流程key和流程id，保存为business_key
        String business_key= baoxiaokey+"."+baoxiaoId;
        //设置代办人信息
        Map map=new HashMap<String, Object>();
        map.put("userName",assigneeName);
        //将business_key保存到map集合
        map.put("business_key",business_key);
        //启动流程
        runtimeService.startProcessInstanceByKey(baoxiaokey,business_key,map);
    }

    @Override
    public List<Task> findTaskListByName(String name) {
        //根据用户名查找当前用户申请表
        List<Task> list = taskService.createTaskQuery().taskAssignee(name).list();
        return list;
    }

    @Override
    public List<Comment> findCommentByBaoxiaoBillId(String id) {
        //根据任务id获取任务实例
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        System.out.println("id"+id);
        String processInstanceId = task.getProcessInstanceId();
        List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);

        return list;
    }

    @Override
    public Baoxiaobill findBaoxiaoBillByTaskId(String taskId) {
        //根据id得到任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 再根据任务中流程实例id 取出 流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        String businessKey = processInstance.getBusinessKey();
        String id="";
        if (businessKey!=null&&!"".equals(businessKey)){
            //分割成数组
            id=businessKey.split("\\.")[1];
        }
        //再根据报销单id查出报销单信息
        Baoxiaobill baoxiaobill = baoxiaoBillMapper.selectByPrimaryKey(Integer.valueOf(id));
        return baoxiaobill;
    }

    //查询流程连线集合
    @Override
    public List<String> findOutComeListByTaskId(String taskId) {
        //返回存放连线的名称集合
        List<String> list = new ArrayList<>();
        //1:使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 2：获取流程定义ID
        String processDefinitionId = task.getProcessDefinitionId();
        // 3：查询ProcessDefinitionEntiy对象
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processDefinitionId);
        // 使用任务对象Task获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        // 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
                .processInstanceId(processInstanceId)// 使用流程实例ID查询
                .singleResult();
        // 获取当前活动的id
        String activityId = pi.getActivityId();
        // 4：获取当前的活动
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
        // 5：获取当前活动完成之后连线的名称
        List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
        if(pvmList!=null && pvmList.size()>0){
            for(PvmTransition pvm:pvmList){
                String name = (String) pvm.getProperty("name");
                if(StringUtils.isNotBlank(name)){
                    list.add(name);
                } else{
                    list.add("默认提交");
                }
            }
        }
        return list;
    }

    @Override
    public void saveSubmitTask(long id, String taskId, String comment, String outcome, String username) {
        //获取任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //根据任务获取实例id
        String processInstanceId = task.getProcessInstanceId();
        //设置批注人
        Authentication.setAuthenticatedUserId(username);
        //添加批注
        taskService.addComment(taskId,processInstanceId,comment);
        Map map=new HashMap<String,Object>();
        //添加流程变量
        map.put("message",outcome);
        if(outcome!=null && !outcome.equals("默认提交")){
            //如果不是默认提交
            map.put("message", outcome);
            //3：使用任务ID，完成当前人的个人任务，同时设置流程变量
            taskService.complete(taskId, map);
        } else {
            taskService.complete(taskId);
        }
        //判断流程是否结束，如果结束，改变报销单状态
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                                    .processInstanceId(processInstanceId).singleResult();
        if (processInstance==null){
            //更新请假单表的状态从1变成2
            Baoxiaobill bill = baoxiaoBillMapper.selectByPrimaryKey((int) id);
            bill.setState(2);
            baoxiaoBillMapper.updateByPrimaryKey(bill);
            System.out.println("bill = " + bill);
        }
    }


    @Override
    public List<Comment> findCommentById(long id) {
        System.out.println("findCommentById"+id);
        String bussiness_key = Constants.Baoxiao_KEY +"."+id;
        HistoricProcessInstance pi = this.historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(bussiness_key).singleResult();
        System.out.println("pi = " + pi);
        //用于处理查看展示分页的表单数据
        if (pi==null) return null;
        List<Comment> commentList = this.taskService.getProcessInstanceComments(pi.getId());

        return commentList;
    }

    @Override
    public HashMap<String, Object> findCoordingByTask(String taskId) {
        //1.获取任务实例
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //2:获取流程定义ID
        String processDefinitionId = task.getProcessDefinitionId();
        // 3:查询ProcessDefinitionEntity对象
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processDefinitionId);
        // 获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        // 获取流程实例
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        // 获取当前活动ID
        String activityId = pi.getActivityId();
        //4：获取当前的活动
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
        //获取坐标 和 长宽
        HashMap<String, Object> map = new HashMap<>();
        map.put("x", activityImpl.getX());
        map.put("y", activityImpl.getY());
        map.put("width", activityImpl.getWidth());
        map.put("height", activityImpl.getHeight());
        return map;
    }

    @Override
    public HashMap<String, Object> findCoordingByBill(Integer billId) {
        String bussinessKey = Constants.Baoxiao_KEY + "." + billId;
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(bussinessKey).singleResult();
        return findCoordingByTask(task.getId());
    }

    @Override
    public Task findTaskByBussinessKey(String bussinessKey) {
        return taskService.createTaskQuery().processInstanceBusinessKey(bussinessKey).singleResult();
    }

    @Override
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        return repositoryService.getProcessDefinition(processDefinitionId);
    }

    @Override
    public InputStream findImageInputStream(String deploymentId, String imageName) {
        return repositoryService.getResourceAsStream(deploymentId , imageName);
    }
}
