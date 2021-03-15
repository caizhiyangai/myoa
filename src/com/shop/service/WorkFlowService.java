package com.shop.service;

import com.shop.pojo.Baoxiaobill;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface WorkFlowService {
    /**
     * 部署流程
     * @param in io输入流，用于读取文件
     * @param fileName 部署流程名
     */
    void  saveNewDeploy(InputStream in, String fileName);

    /**
     * 查找部署流程
     * @return 部署流程信息集合
     */
    List<Deployment> findDeploymentList();

    /**
     * 查找流程定义
     * @return 流程定义信息
     */
    List<ProcessDefinition> findProcessesDefinition();

    /**
     * 删除流程
     * @param deploymentId 流程id
     */
    void delDeployment(String deploymentId);

    /**
     * 启动流程
     * @param baoxiaoId 流程id
     * @param assigneeName 提交申请的代办人
     */
    void startBaoxiaoFlow(Long baoxiaoId,String assigneeName);

    /**
     * 查找用户申请表单
     * @param name 当前用户名
     * @return 申请表单
     */
    List<Task> findTaskListByName(String name);

    /**
     * 根据报销单任务id查找历史批注
     * @param id 报销单id
     * @return 报销单信息
     */
    List<Comment> findCommentByBaoxiaoBillId(String id);

    /**
     * 查找报销表单信息
     * @param taskId 表单id，从business_key分割
     * @return
     */
    Baoxiaobill findBaoxiaoBillByTaskId(String taskId);

    //TODo
    List<String> findOutComeListByTaskId(String taskId);

    /**
     * 处理报销单
     * @param id 当前报销单id
     * @param taskId 任务id
     * @param comment 当前批注信息
     * @param outcome 按钮属性值
     * @param username 当前登录用户名
     */
    void saveSubmitTask(long id, String taskId, String comment, String outcome, String username);

    /**
     * 根据报销单id查找历史批注
     * @param id 报销单id
     * @return
     */
    List<Comment> findCommentById(long id);

    /**
     * 有id查找出bussinessKey在取出taskId
     * @param billId
     * @return
     */
    HashMap<String, Object> findCoordingByBill(Integer billId);

    /**
     * 通过bussinessKey获取task对象
     * @param bussinessKey
     * @return
     */
    Task findTaskByBussinessKey(String bussinessKey);

    /**
     * 获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
     * @param id 任务ID
     * @return
     */
    ProcessDefinition findProcessDefinitionByTaskId(String taskId);

    /**
     * 根据部署id和文件名获取io流
     * @param deploymentId 部署id
     * @param imageName 文件名
     * @return
     */
    InputStream findImageInputStream(String deploymentId, String imageName);

    HashMap<String, Object> findCoordingByTask(String taskId);
}
