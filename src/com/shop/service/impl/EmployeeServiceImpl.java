package com.shop.service.impl;

import com.shop.mapper.EmployeeMapper;
import com.shop.mapper.SysUserRoleMapper;
import com.shop.pojo.Employee;
import com.shop.pojo.EmployeeExample;
import com.shop.pojo.SysUserRole;
import com.shop.pojo.SysUserRoleExample;
import com.shop.service.EmployeeService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author DELL
 */
@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public Employee findEmployeeByName(String name) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        List<Employee> list = employeeMapper.selectByExample(example);
        if (list!=null&&!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    @Override
    public Employee findEmployeeManager(Long managerId) {
        return employeeMapper.selectByPrimaryKey(managerId);
    }

    @Override
    public void updateEmployeeRole(String roleId, String userId) {
        SysUserRoleExample example = new SysUserRoleExample();
        SysUserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andSysUserIdEqualTo(userId);

        SysUserRole userRole = userRoleMapper.selectByExample(example).get(0);
        userRole.setSysRoleId(roleId);

        userRoleMapper.updateByPrimaryKey(userRole);
    }

    @Override
    public void saveEmployee(Employee employee) {
        //取出盐和密码加密
        String salt = employee.getSalt();
        String password = employee.getPassword();
        Md5Hash md5Hash = new Md5Hash(password,salt,2);
        //再将加密密码存进对象
        employee.setPassword(md5Hash.toString());
        //将该员工数据添加到角色表
        SysUserRole userRole = new SysUserRole();
        //查出sys_user_role表总条数
        int i = userRoleMapper.countByExample(null);
        //总条数加1作为新数据的id
        int id=i+1;
        userRole.setId(String.valueOf(id));
        userRole.setSysUserId(employee.getName());
        userRole.setSysRoleId(employee.getRole());
        userRoleMapper.insert(userRole);
        //保存到数据到employee表
        employeeMapper.insert(employee);
    }
}
