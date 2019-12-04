package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.AssessReportInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface AssessReportInfoDao extends Mapper<AssessReportInfo> {
    List<AssessReportInfo>  fetchOtherDepartment(AssessReportInfo assessReportInfo);

    List<AssessReportInfo> selectNeedAss(@Param("assessMonth") String assessMonth);
}
