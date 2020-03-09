package com.puyoma.maxwell.dao;

import com.puyoma.maxwell.entity.Databases;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (Databases)表数据库访问层
 *
 * @author makejava
 * @since 2020-03-05 09:38:49
 */
@Mapper
public interface DatabasesDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Databases queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Databases> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param databases 实例对象
     * @return 对象列表
     */
    List<Databases> queryAll(Databases databases);

    /**
     * 新增数据
     *
     * @param databases 实例对象
     * @return 影响行数
     */
    int insert(Databases databases);

    /**
     * 修改数据
     *
     * @param databases 实例对象
     * @return 影响行数
     */
    int update(Databases databases);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}