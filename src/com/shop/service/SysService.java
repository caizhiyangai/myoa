package com.shop.service;

import com.shop.pojo.*;

import java.util.List;

public interface SysService {
    /**
     * 加载菜单
     * @return
     */
    List<MenuTree> loadMenuTree();

    /**
     * 通过用户id查找用户权限
     * @param userId 用户id
     * @return 用户权限
     */
    List<SysPermission> findPermissionListByUserId(String userId);

    /**
     * 查找所有员工的所有信息
     * @return 所有员工三张表的全部信息
     */
    List<EmployeeCustom> findUserAndRoleList();

    /**
     * 查找所有的角色
     * @return 角色信息集合
     */
    List<SysRole> findAllRoles();

    /**
     * 查找用户权限，用于权限查看
     * @param userName 用户名
     * @return
     */
    SysRole findRolesAndPermissionsByUserName(String userName);

    /**
     * 根据员工等级查找上级领导
     * @param level 员工等级即角色id
     * @return 员工上级
     */
    List<Employee> findNextManager(String level);

    /**
     * 查找所有的主菜单和子菜单权限
     * @return
     */
    List<MenuTree> getAllMenuAndPermission();

    /**
     * 查找出子菜单和权限
     * @return
     */
    List<SysPermission> findAllMenus();

    /**
     * 查找角色名
     * @param roleName 角色名字
     * @return 角色信息
     */
    SysRole findRoleByName(String roleName);

    /**
     * 添加新角色
     * @param role 角色对象
     */
    void addRole(SysRole role);

    /**
     * 添加权限
     * @param sysPermission 权限对象信息
     */
    void addSysPermission(SysPermission sysPermission);

    /**
     * 删除角色
     * @param roleId 角色id
     */
    void deleteSysRoleByPrimaryKey(String roleId);

    /**
     * 查找权限
     * @param roleId 角色id
     * @return 权限信息
     */
    List<SysPermission> findPermissionsByRoleId(String roleId);

    /**
     * 修改权限
     * @param roleId 角色id
     * @param permissionIds 权限数组
     */
    void updateRoleAndPermissions(String roleId, int[] permissionIds);

    /**
     * 添加权限
     * @param role 添加权限的角色
     * @param permissionIds 添加的权限
     */
    void addRoleAndPermissions(SysRole role, int[] permissionIds);
}
