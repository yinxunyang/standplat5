package com.bestvike.standplat.service;

import com.bestvike.standplat.data.EmpInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface EmpInfoService {
    List<EmpInfo> fetchAllEmp();
    List<EmpInfo> fetchDeptEmp(EmpInfo empInfo, String userId);
    List<EmpInfo> fetchEmpNotInUser(String deptId);
    List<EmpInfo> fetch(EmpInfo empInfo);


    /**
     * 新增员工信息
     * @param empInfo
     * @return
     */
    int create(EmpInfo empInfo, String user);

    /**
     * 修改员工信息
     * @param empInfo
     * @return
     */
    int modify(EmpInfo empInfo);

    /**
     * 删除员工信息
     * @param ids
     * @return
     */
    void remove(String ids);

    /**
     * 员工信息导入
     * @param file
     * @param fileSource
     * @param path
     * @param manageUser
     * @return
     */
    Map<String,String> importEmpInfo(MultipartFile file, String fileSource, String path, String manageUser);
}
