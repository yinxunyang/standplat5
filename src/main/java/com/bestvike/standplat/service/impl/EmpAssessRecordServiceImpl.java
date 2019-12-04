package com.bestvike.standplat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bestvike.commons.exception.ServiceException;
import com.bestvike.commons.utils.ConvertUtils;
import com.bestvike.commons.utils.ExcelUtils;
import com.bestvike.commons.utils.FileUtils;
import com.bestvike.commons.utils.StringUtils;
import com.bestvike.standplat.dao.*;
import com.bestvike.standplat.data.*;
import com.bestvike.standplat.service.BaseService;
import com.bestvike.standplat.service.EmpAssessRecordService;
import com.bestvike.standplat.support.DateUtil;
import com.bestvike.standplat.support.ExcelUtil;
import com.bestvike.standplat.support.MybatisUtils;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class EmpAssessRecordServiceImpl extends BaseService implements EmpAssessRecordService {
    @Autowired
    private EmpAssessRecordService empAssessRecordService;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    @Value("${app.file.importPath:}")
    private String uploadPath;
    @Value("${app.file.template-path:}")
    private String templatePath;
    @Autowired
    private EmpAssessRecordDao empAssessRecordDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private DeptInfoDao deptInfoDao;
    @Autowired
    private EmpInfoDao empInfoDao;
    @Autowired
    private PostInfoDao postInfoDao;
    @Autowired
    private AssessReportInfoDao assessReportInfoDao;
    @Autowired
    private AcoreAdjustRecordDao acoreAdjustRecordDao;
    @Autowired
    private EmpAssessLogDao empAssessLogDao;

    @Override
    public List<EmpAssessRecord> fetchCord(EmpAssessRecord empAssessRecord) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("id", empAssessRecord.getEmpId());
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        empAssessRecord.setDeptId(sysUser.getDeptId());
        empAssessRecord.setEmpId(sysUser.getEmpId());
        final StringBuilder fuzzyname = new StringBuilder("");
        if (null != empAssessRecord.getEmpName()) {
            fuzzyname.append(empAssessRecord.getEmpName());
        }
        //查询登录用户所在部门等级比自己低的、二级部门所有 （必须参与考核）
        PageInfo<EmpAssessRecord> es = MybatisUtils.page(empAssessRecord, new ISelect() {
            @Override
            public void doSelect() {
                List<EmpAssessRecord> empAssessRecords = empAssessRecordDao.fetchAll(empAssessRecord);
                Iterator<EmpAssessRecord> it = empAssessRecords.iterator();
                while (it.hasNext()) {
                    EmpAssessRecord obj = it.next();
                    //根据员工id查姓名和岗位
                    EmpInfo empInfo = empInfoDao.selectByPrimaryKey(obj.getEmpId());
                    if (null != empInfo) {
                        obj.setEmpName(empInfo.getName());
                        //根据字典查员工等级
//                        Example example1 = new Example(SysDict.class);
//                        example1.createCriteria().andEqualTo("code","employeeGrade");
//                        SysDict sysDict = sysDictDao.selectOneByExample(example1);

                        if (null != empInfo.getGrade() && !StringUtils.isEmpty(empInfo.getPostCode())) {
                            String code = empInfo.getPostCode();
                            PostInfo postInfo = postInfoDao.selectByPrimaryKey(code);
                            if (null != postInfo) {
                                obj.setGradeName(postInfo.getName());
                            }

                        }
                        //查部门
                        DeptInfo deptInfo = deptInfoDao.selectByPrimaryKey(empInfo.getDeptId());
                        if (null != deptInfo) {
                            obj.setDeptName(deptInfo.getName());
                        }
                    }
                    if (null != empAssessRecord.getFuzzy()) {
                        String fuzzy = empAssessRecord.getFuzzy();
                        obj.setAssessRemark(obj.getAssessRemark() == null ? "" : obj.getAssessRemark());
                        obj.setDeptName(obj.getDeptName() == null ? "" : obj.getDeptName());
                        obj.setEmpName(obj.getEmpName() == null ? "" : obj.getEmpName());
                        obj.setGradeName(obj.getGradeName() == null ? "" : obj.getGradeName());
                        if (!obj.getEmpName().contains(fuzzy) && !obj.getDeptName().contains(fuzzy) && !obj.getEmpId().contains(fuzzy) && !obj.getGradeName().contains(fuzzy) && !obj.getAssessRemark().contains(fuzzy)) {
                            it.remove();
                        }
                    }
                    if (!"".equals(fuzzyname.toString())) {
                        obj.setEmpName(obj.getEmpName() == null ? "" : obj.getEmpName());
                        if (!obj.getEmpName().contains(fuzzyname.toString())) {
                            it.remove();
                        }
                    }
                }

            }
        });

        return es.getList();
    }

    //员工绩效导入
    @Override
    public Map<String, Object> importSale(MultipartFile file, String fileSource, String path, String manageUser) {
        logger.info("上传员工绩效信息开始");
        Map<String, Object> resultMap = new HashMap<>();
        //上传文件  读取文件内容
        InputStream inputStream = null;// 定义一个输入流
        MultipartFile multipartFile = file;
        String tempName = fileSource;//  tempName
        String fileName = multipartFile.getOriginalFilename();//fileName 上传的文件名；
        logger.info("upload filename: " + tempName + "-" + fileName);
        if (fileName == null || fileName.trim().equals("")) {
            logger.info("上传文件名为空");
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "文件上传文件名为空");
            return resultMap;
        }
        try {
            logger.info("获取上传文件流");
            inputStream = multipartFile.getInputStream();// 获取上传文件的输入流
        } catch (IOException e) {
            e.printStackTrace();
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "获取输入流失败");
            logger.info("importTravel end 获取文件流失败");
            return resultMap;
        }
        logger.info("开始进行文件上传");
        byte[] bytes = null; // 定义一个数组；
        try {
            bytes = FileUtils.read(inputStream);
        } catch (Exception e) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "上传文件读取文件流异常");
            logger.info("importTravel end 上传文件读取文件流异常");
            return resultMap;
        }
        int fileSize = bytes.length;
        long maxSize = 0L;
        if (!StringUtils.isEmpty(this.maxFileSize)) {
            this.maxFileSize = this.maxFileSize.toUpperCase();
            maxSize = this.maxFileSize.endsWith("KB")
                    ? Long.valueOf(this.maxFileSize.substring(0, this.maxFileSize.length() - 2))
                    .longValue() * 1024L
                    : (this.maxFileSize.endsWith("MB")
                    ? Long.valueOf(this.maxFileSize.substring(0, this.maxFileSize.length() - 2))
                    .longValue() * 1024L * 1024L
                    : Long.valueOf(this.maxFileSize).longValue());
        }
        if (maxSize > 0L && (long) fileSize > maxSize) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "文件大小不能超过" + this.maxFileSize);
            logger.info("上传操作文件大小不能超过" + this.maxFileSize);
            return resultMap;
        }
        String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String filePath = FileUtils.filePath(uploadPath + path);
        String upFileName = StringUtils.guid() + "." + extName;
        try {
            FileUtils.save(bytes, new FileOutputStream(filePath + upFileName));
        } catch (Exception e) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "上传文件异常");
            logger.info("上传文件异常", e);
            return resultMap;
        }
        logger.info("文件上传完成");
        Map<String, Object> varMap = new HashMap<String, Object>();
        List<EmpAssessRecord> recordList = new ArrayList<>();
        varMap.put("recordList", recordList);
        try {
            FileInputStream is = new FileInputStream(filePath + upFileName);
            ExcelUtils.importFile(templatePath, "empRecordImport.xml", is, varMap);
        } catch (Exception e) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "warning");
            resultMap.put("retMsg", "导入文件格式有误，请使用模板导入");
            ExcelUtil.deleteFileDirectory(filePath + upFileName);
            return resultMap;
        }

        if (recordList.size() == 0) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "warning");
            resultMap.put("retMsg", "导入数据为空");
            ExcelUtil.deleteFileDirectory(filePath + upFileName);
            return resultMap;
        }
        Map<String, Object> resultStr = empAssessRecordService.checkEmpAssRecord(recordList);
        String msgStr = resultStr.get("msg").toString();
        if (msgStr.length() != 0) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", msgStr);
            return resultMap;
        }
        ExcelUtil.deleteFileDirectory(filePath + upFileName);
        resultMap.put("retCode", "0000");
        resultMap.put("list", varMap.get("recordList"));
        resultMap.put("justList", resultStr.get("justList"));
        resultMap.put("level", "success");
        return resultMap;
    }

    @Override
    public Map<String, Object> checkEmpAssRecord(List<EmpAssessRecord> list) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "");
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM");
        String format = sdf.format(new Date());
        //利用map去重
        ArrayList<AcoreAdjustRecord> justList = new ArrayList<AcoreAdjustRecord>();
        HashMap<String, BigDecimal> maps = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            EmpAssessRecord obj = list.get(i);

            if (null == obj.getAssessMonth()) {
                map.put("msg", "第" + (i + 1) + "行年月份为必填项");
                return map;
            }
            if (null == obj.getDeptName()) {
                map.put("msg", "第" + (i + 1) + "行部门名称为必填项");
                return map;
            }
            if (null == obj.getEmpName()) {
                map.put("msg", "第" + (i + 1) + "行员工名称为必填项");
                return map;
            }
            if (null == obj.getTmpEmpScore()) {
                map.put("msg", "第" + (i + 1) + "行评分为必填项");
                return map;
            }
            if (null == obj.getJurName()) {
                map.put("msg", "第" + (i + 1) + "行评委名称为必填项");
                return map;
            }
            if (null == obj.getRecordTime()) {
                map.put("msg", "第" + (i + 1) + "行日期为必填项");
                return map;
            }

            obj.setEmpScore(new BigDecimal(obj.getTmpEmpScore()));

            //判断月份是否符合
            String ret = "19|20\\d{2}-((0([1-9]))|(1(0|1|2)))";
            if (format.compareTo(obj.getAssessMonth()) <= 0 || !obj.getAssessMonth().matches(ret)) {
                map.put("msg", "第" + (i + 1) + "行年月份不正确");
                return map;
            }
            obj.setAssessMonth(obj.getAssessMonth().replace("-", ""));
            BigDecimal empScore = obj.getEmpScore();
            if (empScore.compareTo(new BigDecimal(0)) <= 0) {
                map.put("msg", "第" + (i + 1) + "行评分不能小于0");
                return map;
            }
            double floor = Math.floor(Double.parseDouble(String.valueOf(empScore)));
            BigDecimal bigDecimal = new BigDecimal(floor);
            BigDecimal bigDecimal01 = new BigDecimal(floor + 0.5);
            //判断分数是否正确
            if (bigDecimal.compareTo(empScore) != 0 && bigDecimal01.compareTo(empScore) != 0) {
                map.put("msg", "第" + (i + 1) + "行评分必须是整数或整数加0.5");
                return map;
            }
            //查询调整分数   预先调整
            Example example1 = new Example(AcoreAdjustRecord.class);
            example1.createCriteria().andEqualTo("assessMonth", obj.getAssessMonth());
            AcoreAdjustRecord acoreAdjustRecord = acoreAdjustRecordDao.selectOneByExample(example1);
            if (null == acoreAdjustRecord) {
                map.put("msg", "第" + (i + 1) + "行该月份还没有调整分");
                return map;
            }
            obj.setEmpScore(obj.getEmpScore().add(acoreAdjustRecord.getAdjustScore()));

            if (null == maps.get(acoreAdjustRecord.getAssessMonth())) {
                maps.put(acoreAdjustRecord.getAssessMonth(), acoreAdjustRecord.getAdjustScore());
                justList.add(acoreAdjustRecord);
            }


            //判断是否能找到部门
            Example example = new Example(DeptInfo.class);
            example.createCriteria().andEqualTo("name", obj.getDeptName());
            DeptInfo deptInfo = deptInfoDao.selectOneByExample(example);
            if (null == deptInfo) {
                map.put("msg", "第" + (i + 1) + "行没有找到名称为" + obj.getDeptName() + "的部门");
                return map;
            }
            obj.setDeptId(deptInfo.getId());
            //判断是否能找到评委
            example = new Example(SysUser.class);
            example.createCriteria().andEqualTo("name", obj.getJurName());
            SysUser sysUser = sysUserDao.selectOneByExample(example);
            if (null == sysUser) {
                map.put("msg", "第" + (i + 1) + "行没有找到名称为" + obj.getJurName() + "的评委");
                return map;
            }
            obj.setJuryId(sysUser.getEmpId());
            example = new Example(EmpInfo.class);
            //判断能否找到员工
            example.createCriteria().andEqualTo("name", obj.getEmpName());
            EmpInfo empInfos = empInfoDao.selectOneByExample(example);
            if (null == empInfos) {
                map.put("msg", "第" + (i + 1) + "行没有找到名称为" + obj.getEmpName() + "的员工");
                return map;
            }
            obj.setEmpId(empInfos.getId());
            if (!empInfos.getDeptId().equals(obj.getDeptId())) {
                map.put("msg", "第" + (i + 1) + "行名称为" + obj.getEmpName() + "的员工不属于" + obj.getDeptName());
                return map;
            }

        }
        map.put("justList", JSONArray.toJSON(justList));
        return map;
    }

    //开始插入
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> insertObj(String userId, Map<String, List<Object>> map) {
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            //插入单条评价记录
            List<Object> recordlist = map.get("recordlist");
            for (Object obj : recordlist) {
                EmpAssessRecord empAssessRecord = JSON.parseObject(JSON.toJSONString(obj), EmpAssessRecord.class);
                empAssessRecord.setAssessMonth(empAssessRecord.getAssessMonth().replace("-", ""));
                empAssessRecord.setId(UUID.randomUUID().toString().substring(0, 7));
                empAssessRecord.setSource("02");
                empAssessRecord.setManageUser(this.getSysUserById(userId).getEmpId());
                empAssessRecord.setManageTime(DateUtil.getTime());
                empAssessRecordDao.insert(empAssessRecord);
            }
        } catch (Exception e) {
            System.out.println(e);
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "导入失败");
            return resultMap;
        }
        resultMap.put("retCode", "0000");
        resultMap.put("level", "success");
        resultMap.put("retMsg", "导入成功");
        return resultMap;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> saveEmprecord(List<EmpAssessRecord> list) {
        Map<String, Object> resultMap = new HashMap<>();
        Example exampleEmpInfo = new Example(EmpInfo.class);
        exampleEmpInfo.createCriteria().andEqualTo("needScore", "1").andNotEqualTo("deptId", "1");
        int countEmp = empInfoDao.selectCountByExample(exampleEmpInfo);

        Example exampleEmpAss = new Example(EmpAssessRecord.class);
        exampleEmpAss.createCriteria().andEqualTo("status", "00").andEqualTo("assessMonth",  getLastDate());
        int countEmpAss = empAssessRecordDao.selectCountByExample(exampleEmpAss);

        Example exampleEmpAss2 = new Example(EmpAssessRecord.class);
        exampleEmpAss2.createCriteria().andEqualTo("status", "03").andEqualTo("assessMonth",  getLastDate());
        int countEmpAss2 = empAssessRecordDao.selectCountByExample(exampleEmpAss2);
        //此入口测试临近尾声的时候打开
        if((countEmp-countEmpAss)!=0){
            if(countEmpAss2==0){
                resultMap.put("retCode", "9999");
                resultMap.put("level", "error");
                resultMap.put("retMsg", "该月份有员工尚未评价完成,不能进行上报");
                return resultMap;
            }
            if(countEmpAss2==countEmp){
                resultMap.put("retCode", "0000");
                resultMap.put("level", "success");
                resultMap.put("retMsg", "该月份已经完成上报");
                return resultMap;
            }
        }
        try {
            list.stream().forEach(obj -> {
                obj.setStatus("03");
                empAssessRecordDao.updateByPrimaryKey(obj);
            });
        } catch (Exception e) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "上报失败");
            return resultMap;
        }
        resultMap.put("retCode", "0000");
        resultMap.put("level", "success");
        resultMap.put("retMsg", "上报成功");
        return resultMap;
    }

    @Override
    public Boolean checkIsRecord() {
        Boolean checkIsRecord=false;
        Example exampleEmpInfo = new Example(EmpInfo.class);
        exampleEmpInfo.createCriteria().andEqualTo("needScore", "1").andNotEqualTo("deptId", "1");
        int countEmp = empInfoDao.selectCountByExample(exampleEmpInfo);

        Example exampleEmpAss2 = new Example(EmpAssessRecord.class);
        exampleEmpAss2.createCriteria().andEqualTo("status", "03").andEqualTo("assessMonth",  getLastDate());
        int countEmpAss2 = empAssessRecordDao.selectCountByExample(exampleEmpAss2);
        if(countEmp==countEmpAss2){
            checkIsRecord=true;
        }
        return checkIsRecord;
    }

   /* @Override
    public int getEmpNumIsOn() {
        //增加判断条件
        Example exampleEmpInfo = new Example(EmpInfo.class);
        exampleEmpInfo.createCriteria().andEqualTo("needScore", "1").andNotEqualTo("deptId", "1");
        int countEmp = empInfoDao.selectCountByExample(exampleEmpInfo);
        return countEmp;
    }*/

    @Override
    public List<EmpAssessRecord> fetch(EmpAssessRecord empAssessRecord, String user) {
        //需求调整去掉....新增三级职员
        /*EmpInfo empInfo = empInfoDao.fetchEmpThreeLevel(new EmpInfo()).get(0);
        if(!StringUtils.isEmpty(empInfo)){
            EmpAssessRecord empAssessRecordThree = new EmpAssessRecord();
            empAssessRecordThree.setId(UUID.randomUUID().toString().substring(0, 7));
            empAssessRecordThree.setEmpId(empInfo.getId());
            empAssessRecordThree.setDeptId(empInfo.getDeptId());
            empAssessRecordThree.setAssessMonth(getLastDate());
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                empAssessRecordThree.setManageTime(dateFormat.format(date));
            } catch (Exception e) {
                e.printStackTrace();
            }
            empAssessRecordThree.setManageUser(user);
            int count = empAssessRecordDao.fetchThreeLevel(empAssessRecordThree);
            if(count == 0){
                empAssessRecordDao.insert(empAssessRecordThree);
            }
        }*/
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("empId", user);
        SysUser sysUser = sysUserDao.selectOneByExample(example);
//        String date = getLastDate();
        /*String deptId = empAssessRecord.getDeptId();
        if(StringUtils.isEmpty(deptId)) {
            empAssessRecord.setDeptId(sysUser.getDeptId());
        }*/
        String assessMonth = empAssessRecord.getAssessMonth();
        if (StringUtils.isEmpty(assessMonth)) {
            empAssessRecord.setAssessMonth(getLastDate());
        }
        PageInfo<EmpAssessRecord> page = MybatisUtils.page(empAssessRecord, new ISelect() {
            @Override
            public void doSelect() {
                String sort = empAssessRecord.getSort();
                if (sort != null && !"".equals(sort)) {
                    String arr[] = sort.split(",");
                    if (arr.length == 2) {
                        empAssessRecord.setSort(arr[1]);
                    }
                }
                empAssessRecordDao.fetch(empAssessRecord);
            }
        });
        List<EmpAssessRecord> list = page.getList();
        return list;
    }

    private SysUser getSysUserById(String loginUserId) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", loginUserId);
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        return sysUser;
    }

    @Override
    public Map<String, Object> fetchDeptScore(String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("score", "");
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("id", userId);
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        if (null != sysUser) {
            Example examples = new Example(AssessReportInfo.class);
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            examples.createCriteria().andEqualTo("assessMonth", String.valueOf(year) + String.valueOf(month)).andEqualTo("depId", sysUser.getDeptId());
            AssessReportInfo assessReportInfo = assessReportInfoDao.selectOneByExample(examples);
            if (null != assessReportInfo && !StringUtils.isEmpty(assessReportInfo.getAssessScore())) {
                map.put("score", new BigDecimal(numericalTradeOff(assessReportInfo.getAssessScore().doubleValue())));
            }
        }
        return map;
    }


    /**
     * 暂存员工评价
     *
     * @param user
     * @param data
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> dealEmpAssess(String user, String data) {
        HashMap<String, Object> resultMap = new HashMap<>();
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("id", user);
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        List<EmpAssessRecord> dataList = ConvertUtils.getBeanList(data, EmpAssessRecord.class);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        String assessMonth = String.valueOf(year) + String.valueOf(month);
        if (dataList.size() != 0) {
            int num = 0;
            for (EmpAssessRecord empAssessRecord : dataList) {
                if (empAssessRecord.getEmpScore() != null) {
                    Example examples = new Example(EmpAssessRecord.class);
                    examples.createCriteria().andEqualTo("empId", empAssessRecord.getId()).andEqualTo("assessMonth", assessMonth);
                    EmpAssessRecord empAssessRecordTemp = empAssessRecordDao.selectOneByExample(examples);
                    boolean flag = true;
                    // 已经暂存过的数据再次点击暂存、保存
                    if (null == empAssessRecordTemp) {
                        flag = false;
                        empAssessRecordTemp = new EmpAssessRecord();
                        empAssessRecordTemp.setDeptId(sysUser.getDeptId());
                        empAssessRecordTemp.setAssessMonth(assessMonth);
                        empAssessRecordTemp.setId(UUID.randomUUID().toString().substring(0, 7));
                    }
                    empAssessRecordTemp.setEmpId(empAssessRecord.getId());
                    empAssessRecordTemp.setEmpScore(empAssessRecord.getEmpScore());
                    empAssessRecordTemp.setAssessRemark(empAssessRecord.getAssessRemark());
                    empAssessRecordTemp.setStatus(empAssessRecord.getStatus());
                    empAssessRecordTemp.setManageTime(DateUtil.getTime());
                    empAssessRecordTemp.setJuryId(sysUser.getEmpId());
                    try {
                        if (!flag) {
                            empAssessRecordDao.insert(empAssessRecordTemp);
                        } else {
                            if (empAssessRecordTemp.getStatus().equals("00")) {
                                num++;
                            }
                            empAssessRecordDao.updateByPrimaryKey(empAssessRecordTemp);
                        }
                    } catch (Exception e) {
                        resultMap.put("retCode", "9999");
                        resultMap.put("level", "error");
                        resultMap.put("retMsg", "失败");
                        return resultMap;
                    }
                }
            }
            Example exampleEmp = new Example(EmpInfo.class);
            exampleEmp.createCriteria().andEqualTo("deptId", sysUser.getDeptId()).andEqualTo("needScore", "1");
            int i = empInfoDao.selectCountByExample(exampleEmp);

            Example exampleEmpAss = new Example(EmpAssessRecord.class);
            exampleEmpAss.createCriteria().andEqualTo("deptId", sysUser.getDeptId()).andEqualTo("assessMonth", assessMonth);
            int j = empAssessRecordDao.selectCountByExample(exampleEmpAss);

            if (i != dataList.size() && i != j) {
                Example examples = new Example(EmpInfo.class);
                examples.createCriteria().andEqualTo("deptId", sysUser.getDeptId()).andEqualTo("needScore", "1").andNotEqualTo("status", "9999");
                List<EmpInfo> empInfos = empInfoDao.selectByExample(examples);
                List<String> asList = Arrays.asList("三级职员", "协理员", "处长");
                for (EmpInfo e : empInfos) {
                    PostInfo postInfo = postInfoDao.selectByPrimaryKey(e.getPostCode());
                    if (null != postInfo && asList.contains(postInfo.getName())) {
                        EmpAssessRecord empAssessRecord = new EmpAssessRecord();
                        empAssessRecord.setId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
                        empAssessRecord.setDeptId(sysUser.getDeptId());
                        empAssessRecord.setAssessMonth(assessMonth);
                        empAssessRecord.setEmpId(e.getId());
                        empAssessRecord.setJuryId(sysUser.getEmpId());
                        empAssessRecord.setManageTime(DateUtil.getTime());
                        empAssessRecord.setStatus("00");
                        empAssessRecord.setManageUser(sysUser.getEmpId());

                        BigDecimal average=new BigDecimal("0");
                        Example exampleDept = new Example(DeptInfo.class);
                        exampleDept.createCriteria().andEqualTo("needAssessment", "1");
                        int k = deptInfoDao.selectCountByExample(exampleDept);
                        List<AssessReportInfo> reportInfoList = assessReportInfoDao.selectNeedAss(getLastDate());
                        if(reportInfoList.size()==k){
                            double sum = 0.0;
                            for (AssessReportInfo assessReport : reportInfoList) {
                                sum += assessReport.getAssessScore().doubleValue();
                            }
                            average = new BigDecimal(numericalTradeOff(sum / reportInfoList.size()));
                        }

                        if (postInfo.getName().equals("协理员")) {
                            empAssessRecord.setEmpScore(new BigDecimal(100));
                        } else if(postInfo.getName().equals("处长")) {
                            Example exampleDept2 = new Example(AssessReportInfo.class);
                            exampleDept2.createCriteria().andEqualTo("depId", sysUser.getDeptId()).andEqualTo("assessMonth", assessMonth);
                            AssessReportInfo assessReportInfo = assessReportInfoDao.selectOneByExample(exampleDept2);
                            DeptInfo deptInfo = deptInfoDao.selectByPrimaryKey(assessReportInfo.getDepId());

                            if("1".equals(deptInfo.getNeedAssessment())){
                                if(null!=assessReportInfo) {
                                    empAssessRecord.setEmpScore(assessReportInfo.getAssessScore());
                                }
                            }else{
                                empAssessRecord.setEmpScore(average==new BigDecimal("0")?null:average);
                            }

                        }else{
                            empAssessRecord.setEmpScore(average);
                        }
                        empAssessRecordDao.insert(empAssessRecord);
                    }
                }

            }
        }
        resultMap.put("retCode", "0000");
        resultMap.put("level", "success");
        resultMap.put("retMsg", "成功");
        return resultMap;
    }

    /**
     * 员工考核上报之修改
     *
     * @Param: [loginUserId, data]
     * @return: java.util.Map<java.lang.String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.Object>
     * @Date: 2019/11/22
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateEmpAssRecord(String loginUserId, String data) {
        //修改员工原始得分记录,然后更新员工评价记录表,然后导入日志表
        logger.info("data" + data);
        EmpAssessRecord bean = ConvertUtils.getBean(data, EmpAssessRecord.class);
        String id = bean.getId();
        BigDecimal empScore = bean.getEmpScore();
        EmpAssessRecord empAssessRecord = empAssessRecordDao.selectByPrimaryKey(id);
        logger.info("先开始导入日志表");
        EmpAssessLog empAssessLog = new EmpAssessLog();
        empAssessLog.setId(createUuid());
        empAssessLog.setEmpAssessId(id);
        empAssessLog.setPreEmpScore(empAssessRecord.getEmpScore());
        //  empAssessLog.setEmpScore(new BigDecimal(empScore));
        empAssessLog.setEmpScore(empScore);
        empAssessLog.setManageTime(DateUtil.getDate());
        int insert = empAssessLogDao.insert(empAssessLog);
        if (insert == 1) {
            logger.info("存入日志表成功");
        } else {
            logger.info("存入日志表失败");
        }
        empAssessRecord.setEmpScore(bean.getEmpScore());
        int i = empAssessRecordDao.updateByPrimaryKeySelective(empAssessRecord);
        if (i == 1) {
            logger.info("修改评价记录表成功");
        } else {
            logger.info("修改评价记录表失败");
        }
    }

    /**
     * 员工考核上报之测算
     *
     * @Param: [loginUserId, data]
     * @return: java.util.Map<java.lang.String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.Object>
     * @Date: 2019/11/22
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reportingMeasurement(String loginUserId, String data) {
        // ConvertUtils.getBeanList(data)
        logger.info("data数据为" + data);
        List<EmpAssessRecord> beanList = ConvertUtils.getBeanList(data, EmpAssessRecord.class);
        List<AssessReportInfo> reportInfoList = assessReportInfoDao.selectNeedAss(getLastDate());
        Example exampleDept = new Example(DeptInfo.class);
        exampleDept.createCriteria().andEqualTo("needAssessment","1");
        int i = deptInfoDao.selectCountByExample(exampleDept);
        if(reportInfoList.size()<i){
            throw new ServiceException("部门评价尚未完成,不能进行测算!");
        }
        double sum = 0.0;
        for (AssessReportInfo assessReportInfo : reportInfoList) {
            sum += assessReportInfo.getAssessScore().doubleValue();
        }
        logger.info("系统内考核部门的总得分为" + sum);
        logger.info("计算系统内考核部门的平均分,并对小数点进行处理");
        BigDecimal average = new BigDecimal(numericalTradeOff(sum / reportInfoList.size()));
        logger.info("部门平均分为" + average);
        for (EmpAssessRecord empAssessRecord : beanList) {
            Example example = new Example(DeptInfo.class);
            example.createCriteria().andEqualTo("id", empAssessRecord.getDeptId());
            DeptInfo deptInfo = deptInfoDao.selectOneByExample(example);
            logger.info("首先判断他所在部门是系统外还是系统内的");
            if ("1".equals(deptInfo.getNeedAssessment())) {
                empAssessRecord.setScore(empAssessRecord.getEmpScore());
                empAssessRecord.setRewardScore(empAssessRecord.getEmpScore().subtract(new BigDecimal("100")));
                int insert = empAssessRecordDao.updateByPrimaryKeySelective(empAssessRecord);
                if (insert == 1) {
                    logger.info("存入员工考核记录表成功");
                } else {
                    logger.info("存入员工考核记录表失败");
                }
            } else {
                logger.info("该员工所在部门为系统外部门-------------------------------");
                Example exampleEmp = new Example(EmpInfo.class);
                exampleEmp.createCriteria().andEqualTo("id", empAssessRecord.getEmpId());
                EmpInfo empInfo = empInfoDao.selectOneByExample(exampleEmp);
                logger.info("判断是否为处长或者协理员或者已评价员工");
                if ("17".equals(empInfo.getPostCode())) {
                    logger.info("此员工为处长,所以为部门平均分");
                    empAssessRecord.setScore(new BigDecimal(numericalTradeOff(average.doubleValue())));
                    double rewardScore = average.doubleValue() - 100.00;
                    logger.info("内部奖惩得分" + rewardScore);
                    empAssessRecord.setRewardScore(new BigDecimal(rewardScore));
                    empAssessRecord.setEmpScore(new BigDecimal(numericalTradeOff(average.doubleValue())));
                    logger.info("将数据存到员工考核记录表中");
                    int insert = empAssessRecordDao.updateByPrimaryKeySelective(empAssessRecord);
                    if (insert == 1) {
                        logger.info("存入员工考核记录表成功");
                    } else {
                        logger.info("存入员工考核记录表失败");
                    }
                } else if ("19".equals(empInfo.getPostCode())) {
                    logger.info("该员工为协理员");
                    empAssessRecord.setScore(new BigDecimal(100));
                    empAssessRecord.setRewardScore(new BigDecimal(0));

                    int insert = empAssessRecordDao.updateByPrimaryKeySelective(empAssessRecord);
                    if (insert == 1) {
                        logger.info("存入员工考核记录表成功");
                    } else {
                        logger.info("存入员工考核记录表失败");
                    }
                } else if ("34".equals(empInfo.getPostCode())) {
                    logger.info("该员工为三级职员");
                    empAssessRecord.setScore(average);
                    empAssessRecord.setEmpScore(average);
                    double v = average.doubleValue() - 100.00;
                    empAssessRecord.setRewardScore(new BigDecimal(v));
                    int insert = empAssessRecordDao.updateByPrimaryKeySelective(empAssessRecord);
                    if (insert == 1) {
                        logger.info("存入员工考核记录表成功");
                    } else {
                        logger.info("存入员工考核记录表失败");
                    }
                } else {
                    logger.info("该员工为已评价员工-------------,其得分为 评分+（系统内考核部门的平均分-100）");
                    double score = average.doubleValue() - 100;
                    if (!StringUtils.isEmpty(empAssessRecord.getEmpScore())) {
                        double endScore = score + empAssessRecord.getEmpScore().doubleValue();
                        logger.info("最终得分为" + endScore);
                        empAssessRecord.setScore(new BigDecimal(numericalTradeOff(endScore)));
                        double v = endScore - 100.00;
                        logger.info("奖惩分为" + v);
                        empAssessRecord.setRewardScore(new BigDecimal(v));
                    }
                    int insert = empAssessRecordDao.updateByPrimaryKeySelective(empAssessRecord);
                    if (insert == 1) {
                        logger.info("存入员工考核记录表成功");
                    } else {
                        logger.info("存入员工考核记录表失败");
                    }
                }
            }
        }

    }

    private String formatDate() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        String formatDate = dateFormat.format(date);
        return formatDate;
    }

    private String getLastDate() {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(1);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMM");
        return formatters.format(today);
    }


    /**
     * 十位随机数
     *
     * @Param: []
     * @return: java.lang.String
     * @Date: 2019/11/8
     */
    private String createUuid() {
        String val = "";
        int length = 10;
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

    /**
     * 小数点取舍
     *
     * @Param: [currentScore]
     * @return: double
     * @Date: 2019/11/23
     */
    private double numericalTradeOff(double currentScore) {
        double floor = Math.floor(currentScore);
        if (floor + 0.5 < currentScore) {
            currentScore = floor + 1;
        } else if (floor + 0.5 == currentScore + 0.5) {
            currentScore = floor;
        } else {
            currentScore = floor + 0.5;
        }
        return currentScore;
    }

}
