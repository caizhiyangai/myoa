package com.shop.mapper;

import java.util.List;

import com.shop.pojo.EmployeeCustom;
import com.shop.pojo.MenuTree;
import com.shop.pojo.SysPermission;
import com.shop.pojo.SysRole;


public interface SysPermissionMapperCustom {

	/**
	 * 根据用户id查询菜单
	 * @param userid 用户id
	 * @return 用户权限菜单
	 * @throws Exception
	 */
	public List<SysPermission> findMenuListByUserId(String userid)throws Exception;

	/**
	 * 查询权限url
	 * @param userid 用户id
	 * @return 用户权限url
	 * @throws Exception
	 */
	public List<SysPermission> findPermissionListByUserId(String userid)throws Exception;

	/**
	 * 获取左侧的三个大菜单标题
	 * 	报销管理
	 * 	流程管理
	 * 	系统管理
	 * @return
	 */
	 List<MenuTree> getMenuTree();

	/**
	 * 查找出各个主菜单的子菜单和权限
	 * @return
	 */
	 List<SysPermission> getSubMenu();

	/**
	 * 查找出用户的所有信息
	 * @return 用户三张表的全部信息
	 */
	 List<EmployeeCustom> findUserAndRoleList();

	/**
	 * 查找用户权限
	 * @param userId 用户名字
	 * @return
	 */
	 SysRole findRoleAndPermissionListByUserId(String userId);
	
	 List<SysRole> findRoleAndPermissionList();
	
	 List<SysPermission> findMenuAndPermissionByUserId(String userId);

	/**
	 * 查找出所有主菜单和子菜单权限
	 * @return
	 */
	 List<MenuTree> getAllMenuAndPermision();

	/**
	 * 查询该角色的的权限
	 * @param roleId 角色ID
	 * @return
	 */
	 List<SysPermission> findPermissionsByRoleId(String roleId);
}
