package com.puyoma.maxwell.dao;

import com.puyoma.maxwell.entity.Tables;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (Tables)表数据库访问层
 *
 * @author makejava
 * @since 2020-03-05 09:41:12
 */
@Mapper
public interface TablesDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Tables queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Tables> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param tables 实例对象
     * @return 对象列表
     */
    List<Tables> queryAll(Tables tables);

    /**
     * 新增数据
     *
     * @param tables 实例对象
     * @return 影响行数
     */
    int insert(Tables tables);

    /**
     * 修改数据
     *
     * @param tables 实例对象
     * @return 影响行数
     */
    int update(Tables tables);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}