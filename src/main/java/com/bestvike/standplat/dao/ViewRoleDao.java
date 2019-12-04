package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.ViewRole;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ViewRoleDao extends Mapper<ViewRole> {
    List<ViewRole> fetch(ViewRole sysRole);
}
