package com.bestvike.standplat.service.impl;

import com.bestvike.commons.exception.ServiceException;
import com.bestvike.commons.utils.StringUtils;
import com.bestvike.standplat.dao.DeptInfoDao;
import com.bestvike.standplat.dao.EmpInfoDao;
import com.bestvike.standplat.data.DeptInfo;
import com.bestvike.standplat.data.EmpInfo;
import com.bestvike.standplat.service.BaseService;
import com.bestvike.standplat.service.DeptInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class DeptInfoServiceImpl extends BaseService implements DeptInfoService {
    @Autowired
    private DeptInfoDao deptInfoDao;
    @Autowired
    private EmpInfoDao empInfoDao;

    /**
     * 查询全部部门
     * @return
     */
    @Override
    public List<DeptInfo> fetchAll() {
        Example example = new Example(DeptInfo.class);
        example.createCriteria().andEqualTo("status", "0000");
        return deptInfoDao.selectByExample(example);
    }

    /**
     * 按分级查询部门
     * @param deptInfos
     * @return
     */
    @Override
    public List<DeptInfo> fetchTree(DeptInfo deptInfos) {
        Example example = new Example(DeptInfo.class);
        example.createCriteria().andEqualTo("status", "0000");
        List<DeptInfo> deptInfoList = deptInfoDao.selectByExample(example);
        if (deptInfoList != null && deptInfoList.size() > 0) {
            List<DeptInfo> resultList = new ArrayList<>();
            // 先找顶级部门
            for (DeptInfo deptInfo : deptInfoList) {
                if(null!=deptInfo.getLeader()){
                    Example exampleEmp = new Example(EmpInfo.class);
                    exampleEmp.createCriteria().andEqualTo("id", deptInfo.getLeader());
                    EmpInfo empInfo = empInfoDao.selectOneByExample(exampleEmp);
                    if(null!=empInfo){
                        deptInfo.setLeader(empInfo.getName());
                    }
                }
                if (StringUtils.isEmpty(deptInfo.getParent())) {
                    resultList.add(deptInfo);
                }
            }
            // 目前只考虑两级
            for (DeptInfo deptInfo : deptInfoList) {
                if (!StringUtils.isEmpty(deptInfo.getParent())) {
                    for (int i=0; i<resultList.size(); i++) {
                        DeptInfo temp = resultList.get(i);
                        if (temp.getId().equals(deptInfo.getParent())) {
                            if (temp.getChildren() == null) {
                                temp.setChildren(new ArrayList<>());
                            }
                            temp.getChildren().add(deptInfo);
                            resultList.set(i, temp);
                            break;
                        }
                    }
                }
            }
            if(null != deptInfos && !StringUtils.isEmpty(deptInfos.getFuzzy())){
                Iterator<DeptInfo> it = resultList.iterator();
                while(it.hasNext()){
                    DeptInfo deptInfo = it.next();
                    deptInfo.setLeader(deptInfo.getLeader()==null?"":deptInfo.getLeader());
                    if(!deptInfo.getName().contains(deptInfos.getFuzzy()) && !deptInfo.getLeader().contains(deptInfos.getFuzzy())){
                        it.remove();
                    }
                }
            }
            return resultList;
        }
        return null;
    }

    /** 新增部门信息
     * @param deptInfo
     * @param user
     * @return
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int create(DeptInfo deptInfo,String user) {
        if (StringUtils.isEmpty(deptInfo.getId())) {
            throw new ServiceException("请填写部门编号");
        }
        if (StringUtils.isEmpty(deptInfo.getStatus())) {
            deptInfo.setStatus("0000");
        }
        // 判断数据库是否是已经有这个代码
        Example example = new Example(DeptInfo.class);
        example.createCriteria().andEqualTo("id", deptInfo.getId());
        int cnt = deptInfoDao.selectCountByExample(example);
        if (cnt > 0) {
            throw new ServiceException("部门编号重复");
        }
        if(StringUtils.isEmpty(deptInfo.getLeader())){
            deptInfo.setLeader("");
        }
        if(StringUtils.isEmpty(deptInfo.getParent())) {
            deptInfo.setParent("");
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            deptInfo.setManageTime(dateFormat.parse(dateFormat.format(date)));
        } catch (Exception e){
            e.printStackTrace();
        }
        deptInfo.setManageUser(user);
        int ret = deptInfoDao.insert(deptInfo);

        return ret;
    }

    /**
     * 修改部门信息
     * @param deptInfo
     * @return
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int modify(DeptInfo deptInfo) {
        int ret = deptInfoDao.updateByPrimaryKey(deptInfo);

        return ret;
    }

    /**
     * 删除部门信息
     * @param ids
     * @return
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int remove(String ids) {
        Example example = new Example(DeptInfo.class);
        example.createCriteria().andIn("id", Arrays.asList(ids.split(",")));
        int ret =  deptInfoDao.deleteByExample(example);

        return ret;
    }

}
