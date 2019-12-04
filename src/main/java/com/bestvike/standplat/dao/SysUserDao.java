package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.SysUser;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SysUserDao extends Mapper<SysUser> {
    Integer selectId();
}
