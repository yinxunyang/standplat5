package com.bestvike.standplat.service;

import com.bestvike.standplat.data.DeptInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DeptInfoService {
    List<DeptInfo> fetchAll();
    List<DeptInfo> fetchTree(DeptInfo deptInfo);
    int create(DeptInfo deptInfo, String user);
    int modify(DeptInfo deptInfo);
    int remove(String ids);

}
