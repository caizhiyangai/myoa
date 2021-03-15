package com.shop.service;

import com.shop.pojo.Employee;

public interface EmployeeService {
    /**
     * 根据名字查找用户信息，用于登录
     * @param name 用户名
     * @return 用户信息
     */
    Employee findEmployeeByName(String name);

    /**
     * 根据用户的上级id查找上级
     * @param managerId 上级id
     * @return 上级信息
     */
    Employee findEmployeeManager(Long managerId);

    /**
     * ajax修改角色
     * @param roleId 角色id
     * @param userId 用户id
     */
    void updateEmployeeRole(String roleId, String userId);

    /**
     * 添加新员工信息
     * @param employee 新员工
     */
    void saveEmployee(Employee employee);
}
