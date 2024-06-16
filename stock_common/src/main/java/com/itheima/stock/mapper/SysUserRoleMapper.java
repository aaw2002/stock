package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysUserRole;

/**
* @author aianwei
* @description 针对表【sys_user_role(用户角色表)】的数据库操作Mapper
* @createDate 2024-05-22 18:22:31
* @Entity com.itheima.pojo.entity.SysUserRole
*/
public interface SysUserRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

}
