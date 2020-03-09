package com.puyoma.maxwell.dao;

import com.puyoma.maxwell.entity.Bootstrap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * (Bootstrap)表数据库访问层
 *
 * @author makejava
 * @since 2020-03-04 18:03:56
 */
@Mapper
public interface BootstrapDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Bootstrap queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<Bootstrap> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param paramMap 实例对象map
     * @return 对象列表
     */
    List<Bootstrap> queryAll(Map paramMap);

    /**
     * 新增数据
     *
     * @param bootstrap 实例对象
     * @return 影响行数
     */
    int insert(Bootstrap bootstrap);

    /**
     * 修改数据
     *
     * @param bootstrap 实例对象
     * @return 影响行数
     */
    int update(Bootstrap bootstrap);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}