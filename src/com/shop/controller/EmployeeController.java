package com.shop.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import com.shop.pojo.ActiveUser;
import com.shop.utils.Constants;
import java.io.IOException;

@Controller
public class EmployeeController {

	/**
	 * 验证码生成
	 * @param response 用于输出验证码到浏览器
	 * @param session  保存验证码
	 * @throws IOException
	 */
    @RequestMapping("/checkCode")
	public void getCode(HttpServletResponse response, HttpSession session) throws IOException {

        LineCaptcha cap = CaptchaUtil.createLineCaptcha (120, 50);
        cap.write(response.getOutputStream());
        String code = cap.getCode();
        session.setAttribute("getcode", code);
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model) {

        String exceptionName = (String) request.getAttribute("shiroLoginFailure");
        System.out.println("进入login语句："+exceptionName);
        if (exceptionName != null) {
            if (UnknownAccountException.class.getName().equals(exceptionName)) {
                model.addAttribute("errorMsg", "用户账号不存在");
            } else if (IncorrectCredentialsException.class.getName().equals(exceptionName)) {
                model.addAttribute("errorMsg", "密码不正确");
            } else if ("randomCodeError".equals(exceptionName)) {
                model.addAttribute("errorMsg", "验证码不正确");
            } else {
                model.addAttribute("errorMsg", "未知错误");
            }

        }
        return "login";
    }

    @RequestMapping("/main")
    public String main(ModelMap model, HttpSession session) {
        System.out.println("进入主页跳转.....");
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        session.setAttribute(Constants.GLOBLE_USER_SESSION, activeUser);
        model.addAttribute("activeUser", activeUser);
        return "index";

    }
	

}
