package com.shop.service.impl;

import com.shop.mapper.*;
import com.shop.pojo.*;
import com.shop.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("sysService")
public class SysServiceImpl implements SysService {
    @Autowired
    private SysPermissionMapperCustom sysPermissionMapperCustom;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;
    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public List<MenuTree> loadMenuTree() {
        return sysPermissionMapperCustom.getMenuTree();
    }

    @Override
    public List<SysPermission> findPermissionListByUserId(String userId) {
        try {
            return sysPermissionMapperCustom.findPermissionListByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<EmployeeCustom> findUserAndRoleList() {
        return sysPermissionMapperCustom.findUserAndRoleList();
    }

    @Override
    public List<SysRole> findAllRoles() {
        return sysRoleMapper.selectByExample(null);
    }

    @Override
    public SysRole findRolesAndPermissionsByUserName(String userName) {
        return sysPermissionMapperCustom.findRoleAndPermissionListByUserId(userName);
    }

    @Override
    public List<Employee> findNextManager(String level) {
       /* if (level==3) {
            //总经理为空
            System.out.println("level的值是：3");
            return null;
        }*/
        System.out.println("level = " + level);
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        if (level.length()>=2){
            System.out.println("level的长度大于2");
            criteria.andRoleEqualTo(String.valueOf(3));
            List<Employee> list = employeeMapper.selectByExample(example);
            System.out.println("level长度大于2的集合list = " + list);
            return list;
        }else {
            int managerId=Integer.parseInt(level)+1;
            criteria.andRoleEqualTo(String.valueOf(managerId));
            List<Employee> list = employeeMapper.selectByExample(example);
            System.out.println("其他的level的集合list = " + list);
            return list;
        }


    }

    @Override
    public List<MenuTree> getAllMenuAndPermission() {
        return sysPermissionMapperCustom.getAllMenuAndPermision();
    }

    @Override
    public List<SysPermission> findAllMenus() {
        SysPermissionExample example = new SysPermissionExample();
        SysPermissionExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo("menu");
        return sysPermissionMapper.selectByExample(example);
    }

    @Override
    public SysRole findRoleByName(String roleName) {
        SysRoleExample example = new SysRoleExample();
        SysRoleExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(roleName);
        List<SysRole> list = sysRoleMapper.selectByExample(example);
        if(list!=null&&list.size()>0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void addRole(SysRole role) {
        System.out.println("role信息："+role);
        //查询是否已存在该角色，存在则不添加
        if (findRoleByName(role.getName())==null){
            //设置总数据数加1为id
            int i = sysRoleMapper.countByExample(null);
            int id=i+1;
            role.setAvailable("1");
            role.setId(String.valueOf(id));
            sysRoleMapper.insert(role);
        }

    }

    @Override
    public void addSysPermission(SysPermission sysPermission) {
        if(sysPermission.getAvailable()==null) {
            sysPermission.setAvailable("0");
        }
        sysPermissionMapper.insert(sysPermission);
    }

    @Override
    public void deleteSysRoleByPrimaryKey(String roleId) {
        sysRoleMapper.deleteByPrimaryKey(roleId);
    }

    @Override
    public List<SysPermission> findPermissionsByRoleId(String roleId) {
         return sysPermissionMapperCustom.findPermissionsByRoleId(roleId);
    }

    @Override
    public void updateRoleAndPermissions(String roleId, int[] permissionIds) {
        //先删除角色权限关系表中角色的权限关系
        SysRolePermissionExample example = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);
        rolePermissionMapper.deleteByExample(example);
        //重新创建角色权限关系
        for (Integer pid : permissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            String uuid = UUID.randomUUID().toString();
            rolePermission.setId(uuid);
            rolePermission.setSysRoleId(roleId);
            rolePermission.setSysPermissionId(pid.toString());

            rolePermissionMapper.insert(rolePermission);
        }
    }

    @Override
    public void addRoleAndPermissions(SysRole role, int[] permissionIds) {
        //不存在该角色才添加
        if (findRoleByName(role.getName()) == null) {
            //添加角色
            role.setId(UUID.randomUUID().toString());
            role.setAvailable("1");
            roleMapper.insert(role);
            //添加角色和权限关系表
            for (int i = 0; i < permissionIds.length; i++) {
                SysRolePermission rolePermission = new SysRolePermission();
                //16进制随机码
                String uuid = UUID.randomUUID().toString();
                rolePermission.setId(uuid);
                rolePermission.setSysRoleId(role.getId());
                rolePermission.setSysPermissionId(permissionIds[i] + "");
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }
}
