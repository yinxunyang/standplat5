package com.bestvike.standplat.service;

import com.bestvike.standplat.data.EmpAssessRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@Service
public interface EmpAssessRecordService {
    List<EmpAssessRecord> fetch(EmpAssessRecord empAssessRecord, String user);
    List<EmpAssessRecord> fetchCord(EmpAssessRecord empAssessRecord);
    Map<String, Object> importSale(MultipartFile file, String fileSource, String path, String manageUser);
    Map<String, Object> checkEmpAssRecord(List<EmpAssessRecord> list);
    Map<String, Object> dealEmpAssess(String user, String data);
    Map<String, Object> fetchDeptScore(String userId);
    Map<String, Object> insertObj(String manageUser, Map<String, List<Object>> map);

   void updateEmpAssRecord(String loginUserId, String data);

    void reportingMeasurement(String loginUserId, String data);
    Map<String,Object> saveEmprecord(List<EmpAssessRecord> list);

    Boolean checkIsRecord();

//    int getEmpNumIsOn();
}
