package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.SysDict;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SysDictDao extends Mapper<SysDict> {
}
