package com.bestvike.standplat.service;

import com.bestvike.standplat.data.AssessReportInfo;
import com.bestvike.standplat.data.DeptAssRecord;
import com.bestvike.standplat.data.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface DeptAssRecordService {
    List<AssessReportInfo> fetch(AssessReportInfo assessReportInfo);
    List<DeptAssRecord> fetch(DeptAssRecord deptAssRecord);
    Map<String, Object> importSale(MultipartFile file, String fileSource, String path, String manageUser);
    Map<String, Object> checkDeptAssRecord(List<DeptAssRecord> list);
    Map getAllMessages(String loginUserId);

    void primaryEvaluation(String loginUserId, String data);

    Map twoLevelEvaluation(String loginUserId);

    void secondaryToSubmit(String loginUserId, String data);

    Map topEvaluation(String loginUserId, String data);

    void finalToSubmit(String loginUserId, String data);

    Map<String, Object> insertObj(String manageUser, Map<String, List<Object>> map);

    List<AssessReportInfo> fetchSelfDept(AssessReportInfo assessReportInfo, String LoginUserId);
    List<SysUser> evaluationProgress(String loginUserId);

    Map topLevel(String loginUserId);
}
