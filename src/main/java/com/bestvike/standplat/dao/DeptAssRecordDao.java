package com.bestvike.standplat.dao;


import com.bestvike.standplat.data.AssessReportInfo;
import com.bestvike.standplat.data.DeptAssRecord;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface DeptAssRecordDao extends Mapper<DeptAssRecord> {
    List<AssessReportInfo> fetchAll(AssessReportInfo assessReportInfo);
    List<DeptAssRecord> fetchCord(DeptAssRecord deptAssRecord);

    String selectAvgForInDept(String assessMonth);
}
