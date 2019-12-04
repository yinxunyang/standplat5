package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.SysUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface SysUserDao extends Mapper<SysUser> {
    Integer selectId();
    int resetPassword(@Param("id") String id, @Param("password") String password);
    //修改用户表的销户状态为9999
    int cancelAccount(String id);
    int saveGrants(@Param("userId") String userId, @Param("grants") String grants);
    int selectIsGrantCount(@Param("roles") List<String> roles);

    SysUser selectTwoUser();
}
