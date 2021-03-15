package com.shop.service;

import com.shop.pojo.Baoxiaobill;
import org.activiti.engine.task.Task;

import java.util.List;

public interface BaoXiaoService {
    /**
     * 添加报销申请
     * @param bill 报销申请对象信息
     */
    void addBaoXiao(Baoxiaobill bill);

    /**
     * 查找当前用户的报销单
     * @param id 用户id
     * @return 报销单
     */
    List<Baoxiaobill> findBaoxiaoBillListByUser(long id);

    /**
     * 根据报销单id查找报销单
     * @param id 报销单id
     * @return 报销单信息
     */
    Baoxiaobill findBaoxiaoBillById(String id);

    /**
     * 根据id删除报销单
     * @param id
     */
    void delBillById(Integer id);
}
