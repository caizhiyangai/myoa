package com.shop.controller;

import com.shop.pojo.ActiveUser;
import com.shop.pojo.Baoxiaobill;
import com.shop.service.BaoXiaoService;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WorkFlowController {
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private BaoXiaoService baoXiaoService;

    /**
     * 发布流程
     * @param processName 用户定义的流程名
     * @param fileName 用户上传的流程文件
     * @return 跳转到发布成功界面
     */
    @RequestMapping("/deployProcess")
    public String deployProcess(String processName,MultipartFile fileName){
        try {
            workFlowService.saveNewDeploy(fileName.getInputStream(),processName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //跳转到流程管理路径
        return "redirect:/processDefinitionList";
    }

    /**
     * 流程管理展示流程信息
     * @param model 用于存放信息到前端
     * @return 跳转到流程管理界面
     */
    @RequestMapping("/processDefinitionList")
    public String processDefinition(Model model){
        //查询部署信息管理列表
        List<Deployment> deploymentList = workFlowService.findDeploymentList();
        //查询流程定义信息列表
        List<ProcessDefinition> definition = workFlowService.findProcessesDefinition();
        //保存到浏览器
        model.addAttribute("depList",deploymentList);
        model.addAttribute("pdList",definition);
        //跳转到流程管理界面
        return "workflow_list";
    }

    /**
     * 删除流程
     * @param deploymentId 流程id
     * @return 跳转到新界面
     */
    @RequestMapping("delDeployment")
    public String delDeployment(String deploymentId){
        workFlowService.delDeployment(deploymentId);
        return "redirect:/processDefinitionList";
    }


    /**
     * 保存报销单，并且启动流程图
     * @param bill 报销单信息
     * @param session 获取全局对象
     * @return 跳转到新界面
     */
    @RequestMapping("/saveStartBaoxiao")
    public String saveStartBaoxiao(Baoxiaobill bill, HttpSession session){
        //设置当前时间为申请时间
        bill.setCreatdate(new Date());
        //设置申请单状态
        bill.setState(1);
        //获取当前用户id
        //ActiveUser activeUser = (ActiveUser) session.getAttribute(Constants.GLOBLE_USER_SESSION);
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        bill.setUserId((int)activeUser.getId());
        //保存报销单到数据库
        baoXiaoService.addBaoXiao(bill);
        //启动流程
        workFlowService.startBaoxiaoFlow(Long.valueOf(bill.getId()),activeUser.getUsername());
        return "redirect:/myTaskList";
    }

    /**
     * 展示我的待办事务界面
     * @param model 用于存放数据到前端
     * @param session 用于获取当前登录人
     * @return 报销单信息
     */
    @RequestMapping("/myTaskList")
    public String baoxiaoTaskList(Model model,HttpSession session){
        //ActiveUser activeUser = (ActiveUser) session.getAttribute(Constants.GLOBLE_USER_SESSION);
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        String username = activeUser.getUsername();
        List<Task> list = workFlowService.findTaskListByName(username);
        model.addAttribute("taskList",list);
        return "workflow_task";
    }

    /**
     * 查看报销审核
     * @param id 报销账单id
     * @param model 存放数据
     * @return
     */
    @RequestMapping("viewHisComment")
    public String viewHisComment(long id, ModelMap model){
        //根据报销单ID，查询报销单信息
        Baoxiaobill bill = baoXiaoService.findBaoxiaoBillById(String.valueOf(id));
        model.addAttribute("baoxiaoBill", bill);
        //根据请假单ID，查询历史的批注信息
        List<Comment> commentList = workFlowService.findCommentById(id);
        if (commentList==null){

        }
        model.addAttribute("commentList", commentList);
        return "workflow_commentlist";

    }

    /**
     * 处理报销任务
     * @param taskId 任务id
     * @return
     */
    @RequestMapping("/viewTaskForm")
    public ModelAndView viewTaskForm(String taskId){
        ModelAndView mv = new ModelAndView();
        //报销单信息
        Baoxiaobill bill = workFlowService.findBaoxiaoBillByTaskId(taskId);
        //历史备注信息
        List<Comment> list = workFlowService.findCommentByBaoxiaoBillId(taskId);
        System.out.println("bill = " + bill);
        System.out.println("list = " + list);
        List<String> outcomeList = workFlowService.findOutComeListByTaskId(taskId);
        System.out.println("outcomeList = " + outcomeList);
        mv.addObject("baoxiaoBill", bill);
        mv.addObject("commentList", list);
        mv.addObject("outcomeList", outcomeList);
        mv.addObject("taskId", taskId);
        mv.setViewName("approve_baoxiao");
        return mv;
    }

    /**
     * 提交任务
     * @param id
     * @param taskId
     * @param comment
     * @param outcome
     * @param session
     * @return
     */
    @RequestMapping("/submitBaoxiaoTask")
    public String submitBaoxiaoTask(long id,String taskId,String comment,String outcome,HttpSession session){
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        String username = activeUser.getUsername();
        workFlowService.saveSubmitTask(id, taskId, comment, outcome, username);
        return "redirect:/myTaskList";
    }

    /**
     *  删除报销记录
     * @param id 报销单id
     * @return
     */
    @RequestMapping("/baoxiaoBillAction_delete")
    public String delBaoxiaoBill(Integer id) {
        baoXiaoService.delBillById(id);
        return "redirect:/myBaoxiaoBill";
    }

    /**
     * 查看我的报销单里的流程图
     * @param billId 报销单id
     * @param model
     * @return
     */
    @RequestMapping("/viewCurrentImageByBaoxiaoBill")
    public String viewCurrentImage(Integer billId,Model model) {
        //显示当前活动流程图
        HashMap<String, Object> map = workFlowService.findCoordingByBill(billId);
        //获取流程定义对象
        String bussinessKey = Constants.Baoxiao_KEY + "." + billId;
        Task task = workFlowService.findTaskByBussinessKey(bussinessKey);
        ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(task.getId());
        //返回坐标等值和显示流程图的部署id和部署名称
        model.addAttribute("acs",map);
        model.addAttribute("deploymentId", pd.getDeploymentId());
        model.addAttribute("imageName", pd.getDiagramResourceName());
        return "viewimage";
    }

    @RequestMapping("/viewCurrentImage")
    public String viewCurrentImage(String taskId,Model model) {
        Map<String, Object> map = workFlowService.findCoordingByTask(taskId);
        ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(taskId);
        //返回坐标等值和显示流程图的部署id和部署名称
        model.addAttribute("acs",map);
        model.addAttribute("deploymentId", pd.getDeploymentId());
        model.addAttribute("imageName", pd.getDiagramResourceName());
        return "viewimage";
    }
    /**
     * 展示流程图
     * @param deploymentId 部署id
     * @param imageName 图片名
     * @param response 输出到浏览器
     */
    @RequestMapping("/viewImage")
    public void  viewImage(String deploymentId,String imageName,HttpServletResponse response) {
        System.out.println("进入viewImage");
        //显示流程图
        try(InputStream in = workFlowService.findImageInputStream(deploymentId, imageName);
            ServletOutputStream out = response.getOutputStream();) {
            int len=0;
            byte[] buffer=new byte[1024];
            while((len=in.read(buffer))!=-1) {
                out.write(buffer,0, len);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


}
