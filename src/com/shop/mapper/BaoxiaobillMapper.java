package com.shop.mapper;

import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.BaoxiaobillExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaoxiaobillMapper {
    int countByExample(BaoxiaobillExample example);

    int deleteByExample(BaoxiaobillExample example);

    int deleteByPrimaryKey(Integer id);

    /**
     * 插入数据
     * @param record 表单对象
     * @return 影响行数
     */
    int insert(Baoxiaobill record);

    int insertSelective(Baoxiaobill record);

    List<Baoxiaobill> selectByExample(BaoxiaobillExample example);

    /**
     * 查找报销表数据
     * @param id 表单id
     * @return 封装的表单对象
     */
    Baoxiaobill selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Baoxiaobill record, @Param("example") BaoxiaobillExample example);

    int updateByExample(@Param("record") Baoxiaobill record, @Param("example") BaoxiaobillExample example);

    int updateByPrimaryKeySelective(Baoxiaobill record);

    /**
     * 修改报销表
     * @param record 表单对象
     * @return
     */
    int updateByPrimaryKey(Baoxiaobill record);
}