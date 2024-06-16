package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysUser;

/**
* @author aianwei
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2024-05-22 18:22:31
* @Entity com.itheima.pojo.entity.SysUser
*/
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser getUserByUsername( String username);
}
