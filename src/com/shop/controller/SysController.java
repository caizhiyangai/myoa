package com.shop.controller;

import com.shop.pojo.*;
import com.shop.service.EmployeeService;
import com.shop.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SysController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SysService sysService;

    @RequestMapping("findUserList ")
    public ModelAndView findUserList(){
        ModelAndView mv = new ModelAndView();
        List<EmployeeCustom> employeeCustom=sysService.findUserAndRoleList();
        List<SysRole> roles = sysService.findAllRoles();
        mv.addObject("userList", employeeCustom);
        mv.addObject("allRoles", roles);
        mv.setViewName("userlist");
        return mv;
    }

    /**
     * 查询用户的权限
     * @param userName 用户名字
     * @return 页面跳转
     */
    @RequestMapping("/viewPermissionByUser")
    public @ResponseBody
    SysRole viewPermissionByUser(String userName){
        return sysService.findRolesAndPermissionsByUserName(userName);
    }

    /**
     * Ajax修改用户管理的角色分配
     * @param roleId 角色id，值是数字
     * @param userId 用户id，值是用户名
     * @return 修改是否成功反馈
     */
    @RequestMapping("/assignRole")
    @ResponseBody
    public Map<String,String> assignRole(String roleId, String userId){
        System.out.println("roleId"+roleId);
        System.out.println("userId"+userId);
        Map<String,String> map=new HashMap<>();
        try {
            employeeService.updateEmployeeRole(roleId, userId);
            map.put("msg", "分配角色成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "分配角色失败");
        }
        return map;
    }

    /**
     * 根据员工级别查找上级信息
     * @param level 员工级别即roleId
     * @return 上级信息
     */
    @RequestMapping("/findNextManager")
    @ResponseBody
    public List<Employee> findNextManager(String level){

        //System.out.println("控制层的level = " + level);
        return sysService.findNextManager(level);
    }

    /**
     * 添加并保存用户
     * @param employee 用户信息
     * @return 跳转到用户展示页面
     */
    @RequestMapping("/saveUser")
    public String saveUser(Employee employee) {
        //如果是总经理
        if(employee.getRole().equals("3")) {
            employee.setManagerId(1L);
        }
        //设置盐
        employee.setSalt("eteokues");
        employeeService.saveEmployee(employee);
        return "redirect:/findUserList";
    }

    /**
     * 角色管理信息展示
     * @return
     */
    @RequestMapping("/toAddRole")
    public ModelAndView toAddRole(){
        ModelAndView mv = new ModelAndView();
        //查询所有主菜单和子菜单权限
        List<MenuTree> list = sysService.getAllMenuAndPermission();
        System.out.println("主菜单和子菜单权限list = " + list);
        //查询所有menu，用于添加权限
        List<SysPermission> allMenus = sysService.findAllMenus();
        mv.addObject("allPermissions", list);
        mv.addObject("menuTypes", allMenus);
        mv.setViewName("rolelist");
        return mv;
    }

    /**
     * Ajax判断是否已存在角色
     * @param roleName 角色名
     * @return 是否存在
     */
    @RequestMapping("/checkRoleName")
    @ResponseBody
    public String checkRoleName(String roleName){
        //查找是否已存在该角色名
        if(sysService.findRoleByName(roleName)!=null) {
            return "no";
        }
        return "yes";
    }

    /**
     * 保存角色和权限
     * @param role 角色名
     * @return 跳转页面
     */
    @RequestMapping("/saveRoleAndPermissions")
    public String saveRole(SysRole role,int[] permissionIds) {
        //添加角色和权限
        sysService.addRoleAndPermissions(role, permissionIds);
        return "redirect:/toAddRole";
    }

    /**
     * 添加权限
     * @param sysPermission 用户权限
     * @return 跳转页面
     */
    @RequestMapping("/saveSubmitPermission")
    public String saveSubmitPermission(SysPermission sysPermission) {
        sysService.addSysPermission(sysPermission);
        return "redirect:/toAddRole";
    }

    /**
     * 查询角色列表
     * @return 所有角色信息
     */
    @RequestMapping("/findRoles")
    public ModelAndView findRoles(){
        ModelAndView mv = new ModelAndView();
        //查询所有角色
        List<SysRole> roles = sysService.findAllRoles();
        //查询所有菜单和子菜单权限
        List<MenuTree> permissions = sysService.getAllMenuAndPermission();
        //返回结果给界面
        mv.addObject("allMenuAndPermissions", permissions);
        mv.addObject("allRoles", roles);
        mv.setViewName("permissionlist");
        return mv;
    }

    /**
     * 删除角色
     * @param roleId 角色id
     * @return 跳转页面
     */
    @RequestMapping("/delRole")
    public String delRole(String roleId) {
        sysService.deleteSysRoleByPrimaryKey(roleId);
        return "redirect:/findRoles";
    }

    /**
     * 编辑角色的权限
     * @param roleId 角色id
     * @return 角色权限
     */
    @RequestMapping("/loadMyPermissions")
    @ResponseBody
    public List<SysPermission> loadMyPermissions(String roleId){
        return sysService.findPermissionsByRoleId(roleId);
    }

    /**
     * 修改角色权限
     * @param roleId 角色id
     * @param permissionIds 权限集合
     * @return 跳转页面
     */
    @RequestMapping("/updateRoleAndPermission")
    public String updateRoleAndPermission(String roleId,int[] permissionIds) {
        sysService.updateRoleAndPermissions(roleId, permissionIds);
        return "redirect:/findRoles";
    }

}
