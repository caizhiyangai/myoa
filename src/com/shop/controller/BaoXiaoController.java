package com.shop.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shop.pojo.ActiveUser;
import com.shop.pojo.Baoxiaobill;
import com.shop.service.BaoXiaoService;
import com.shop.service.WorkFlowService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class BaoXiaoController {
    @Autowired
    private BaoXiaoService baoxiaoService;

    @Autowired
    private WorkFlowService workFlowService;

    /**
     * 查询报销单数据，并分页显示
     * @param pageNum 当前页码
     * @param model 储存值到前端
     * @param session 取出全局用户
     * @return 跳转到新页面
     */
    @RequestMapping("/myBaoxiaoBill")
    public String myBaoxiaoBill(@RequestParam(defaultValue = "1")int pageNum,ModelMap model, HttpSession session){
        //每页10条数据
        PageHelper.startPage(pageNum,10);
        //获取当前用户信息
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        //根据名字查找报销单
        List<Baoxiaobill> list = baoxiaoService.findBaoxiaoBillListByUser(activeUser.getId());
        PageInfo<Baoxiaobill> pageInfo = new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        //放置到前端
        model.addAttribute("baoxiaoList", list);
        return "baoxiaobill";
    }

}
