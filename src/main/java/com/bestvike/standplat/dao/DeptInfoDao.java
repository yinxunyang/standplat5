package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.DeptInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface DeptInfoDao extends Mapper<DeptInfo> {
    String getDeptId(@Param("deptName") String deptName);
}
