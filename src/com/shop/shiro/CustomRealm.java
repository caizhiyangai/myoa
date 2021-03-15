package com.shop.shiro;

import java.util.ArrayList;
import java.util.List;

import com.shop.service.EmployeeService;
import com.shop.service.SysService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.shop.pojo.ActiveUser;
import com.shop.pojo.Employee;
import com.shop.pojo.MenuTree;
import com.shop.pojo.SysPermission;

public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SysService sysService;

	/**
	 * 认证
	 * @param token 用户对象
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("正在认证用户.......");
		//用户输入的账号
		String username =  token.getPrincipal().toString();
		Employee user  = employeeService.findEmployeeByName(username);
		if(user==null) {
			return null;
		}

		List<MenuTree> menuTree = sysService.loadMenuTree();

		//将需要的信息保存在自定义ActiveUser中
		ActiveUser au = new ActiveUser();
		au.setId(user.getId());
		au.setUserid(user.getName());
		au.setUsercode(user.getName());
		au.setUsername(user.getName());
		au.setManagerId(user.getManagerId());
		au.setMenuTree(menuTree);

		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(au, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), "CustomRealm");
		return info;
	}

	/**
	 * 授权
	 * @param principal 登录用户信息
	 * @return
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
		ActiveUser user =  (ActiveUser) principal.getPrimaryPrincipal();
		//通过用户查询出权限
		List<SysPermission> permissions = null;
		try {
			permissions = sysService.findPermissionListByUserId(user.getUsercode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//存储权限percode
		List<String> permissionList = new ArrayList<>();
		for (SysPermission permi : permissions) {
			permissionList.add(permi.getPercode());
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addStringPermissions(permissionList);
		return info;
	}

}
