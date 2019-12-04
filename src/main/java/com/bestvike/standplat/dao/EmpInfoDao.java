package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.EmpInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface EmpInfoDao extends Mapper<EmpInfo> {
    List<EmpInfo> fetch(EmpInfo empInfo);
    List<EmpInfo> fetchEmpThreeLevel(EmpInfo empInfo);
    List<EmpInfo> fetchDeptEmp(EmpInfo empInfo);
    String fetchEmpIdMax(EmpInfo empInfo);
    List<EmpInfo> fetchEmpNotInUser(@Param("deptId") String deptId);
}
