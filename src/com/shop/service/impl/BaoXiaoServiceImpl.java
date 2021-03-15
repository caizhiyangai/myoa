package com.shop.service.impl;

import com.shop.mapper.BaoxiaobillMapper;
import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.BaoxiaobillExample;
import com.shop.service.BaoXiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("baoXiaoService")
public class BaoXiaoServiceImpl implements BaoXiaoService {
    @Autowired
    private BaoxiaobillMapper baoxiaobillMapper;

    @Override
    public void addBaoXiao(Baoxiaobill bill) {
        //获取请假单ID,判断是否已存在报销单
        if( bill.getId()==null){
           //不存在报销单，则新增加
            baoxiaobillMapper.insert(bill);
        }
        else{
            //存在，则执行update的操作，完成更新
            System.out.println(bill);
            baoxiaobillMapper.updateByPrimaryKey(bill);
        }
    }

    @Override
    public List<Baoxiaobill> findBaoxiaoBillListByUser(long id) {
        BaoxiaobillExample example = new BaoxiaobillExample();
        BaoxiaobillExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo((int) id);
        return baoxiaobillMapper.selectByExample(example);
    }

    @Override
    public Baoxiaobill findBaoxiaoBillById(String id) {
        return baoxiaobillMapper.selectByPrimaryKey(Integer.parseInt(id));
    }

    @Override
    public void delBillById(Integer id) {
        baoxiaobillMapper.deleteByPrimaryKey(id);
    }


}
