package com.bestvike.standplat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bestvike.commons.utils.ConvertUtils;
import com.bestvike.commons.utils.ExcelUtils;
import com.bestvike.commons.utils.FileUtils;
import com.bestvike.commons.utils.StringUtils;
import com.bestvike.standplat.dao.*;
import com.bestvike.standplat.data.*;
import com.bestvike.standplat.service.BaseService;
import com.bestvike.standplat.service.DeptAssRecordService;
import com.bestvike.standplat.service.DeptInfoService;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class DeptAssRecordServiceImpl extends BaseService implements DeptAssRecordService {
    @Autowired
    private DeptAssRecordService deptAssRecordService;
    @Autowired
    private AssessReportInfoDao assessReportInfoDao;
    @Autowired
    private AcoreAdjustRecordDao acoreAdjustRecordDao;
    @Autowired
    private EmpInfoDao empInfoDao;
    @Autowired
    private DeptInfoDao deptInfoDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private DeptAssRecordDao deptAssRecordDao;
    @Autowired
    private EmpAssessRecordDao empAssessRecordDao;

    @Autowired
    private DeptInfoService deptInfoService;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    @Value("${app.file.importPath:}")
    private String uploadPath;
    @Value("${app.file.template-path:}")
    private String templatePath;

    @Override
    public List<AssessReportInfo> fetch(AssessReportInfo assessReportInfo) {
        PageInfo<AssessReportInfo> as = MybatisUtils.page(assessReportInfo, new ISelect() {
            @Override
            public void doSelect() {
                List<AssessReportInfo> assessReportInfos = deptAssRecordDao.fetchAll(assessReportInfo);
                assessReportInfos.stream().filter(item -> {
                    String s = String.valueOf(item.getAssessScore());
                    double floor = Math.floor(item.getAssessScore().doubleValue());
                    if (item.getAssessScore().doubleValue() <= floor + 0.5 && item.getAssessScore().doubleValue() != floor) {
                        item.setAssessScore(new BigDecimal(floor + 0.5));
                    } else {
                        item.setAssessScore(new BigDecimal(Math.ceil(item.getAssessScore().doubleValue())));
                    }
                    return true;
                }).forEach(item -> {
                });
            }
        });
        return as.getList();
    }

    @Override
    public List<DeptAssRecord> fetch(DeptAssRecord deptAssRecord) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("id", deptAssRecord.getJuryId());
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        deptAssRecord.setJuryId(sysUser.getEmpId());
        PageInfo<DeptAssRecord> ds = MybatisUtils.page(deptAssRecord, new ISelect() {
            @Override
            public void doSelect() {
                List<DeptAssRecord> list = deptAssRecordDao.fetchCord(deptAssRecord);
                if (null != list && list.size() != 0) {
                    for (DeptAssRecord deptAssRecord1 : list) {
                        EmpInfo empInfo = empInfoDao.selectByPrimaryKey(deptAssRecord1.getJuryId());
                        if (!StringUtils.isEmpty(deptAssRecord1.getCooperateRemark())) {
                            deptAssRecord1.setCooperateRemark(deptAssRecord1.getCooperateRemark() + "(" + empInfo.getName() + ")");
                        }
                        if (!StringUtils.isEmpty(deptAssRecord1.getAssessMonth()) && !StringUtils.isEmpty(deptAssRecord1.getDeptId())) {
                            Example example = new Example(AssessReportInfo.class);
                            example.createCriteria().andEqualTo("assessMonth", deptAssRecord1.getAssessMonth()).andEqualTo("depId", deptAssRecord1.getDeptId());
                            AssessReportInfo assessReportInfo1 = assessReportInfoDao.selectOneByExample(example);
                            if (null != assessReportInfo1) {
                                deptAssRecord1.setAssessScore(new BigDecimal(numericalTradeOff(assessReportInfo1.getAssessScore().doubleValue())));
                            }
                        }

                    }
                }
            }
        });

        return ds.getList();
    }

    @Override
    public Map<String, Object> importSale(MultipartFile file, String fileSource, String path, String manageUser) {
        logger.info("上传部门绩效信息开始");
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
        List<DeptAssRecord> recordList = new ArrayList<>();
        varMap.put("recordList", recordList);
        try {
            FileInputStream is = new FileInputStream(filePath + upFileName);
            ExcelUtils.importFile(templatePath, "deptRecordImport.xml", is, varMap);
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
        Map<String, Object> resultStr = deptAssRecordService.checkDeptAssRecord(recordList);
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
        resultMap.put("depreport", resultStr.get("depreport"));
        return resultMap;
    }

    @Override
    public Map<String, Object> checkDeptAssRecord(List<DeptAssRecord> list) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", "");
        String[] arr = new String[]{"106", "103", "100", "97"};
        List<String> scoreList = Arrays.asList(arr);
        String[] arr1 = new String[]{"0", "-0.5", "-1", "-1.5", "-2"};
        List<String> subscoreList = Arrays.asList(arr1);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM");
        String format = sdf.format(new Date());
        BigDecimal bigDecimal1 = new BigDecimal(0);
        //利用map去重
        HashMap<String, BigDecimal> maps = new HashMap<>();
        ArrayList<AcoreAdjustRecord> justList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            DeptAssRecord obj = list.get(i);
            if (null == obj.getAssessMonth()) {
                map.put("msg", "第" + (i + 1) + "行年月份为必填项");
                return map;
            }
            if (null == obj.getDepName()) {
                map.put("msg", "第" + (i + 1) + "行部门名称为必填项");
                return map;
            }
            if (null == obj.getTaskPerScore()) {
                map.put("msg", "第" + (i + 1) + "行评分为必填项");
                return map;
            }
            if (null == obj.getJurName()) {
                map.put("msg", "第" + (i + 1) + "行评委名称为必填项");
                return map;
            }
            if (null == obj.getManageTime()) {
                map.put("msg", "第" + (i + 1) + "行日期为必填项");
                return map;
            }

            if (obj.getTmpCooperateScore() == null) {
                obj.setCooperateScore(new BigDecimal(0));
            } else {
                obj.setCooperateScore(new BigDecimal(obj.getTmpCooperateScore()));
            }
            //判断月份是否符合
            String ret = "19|20\\d{2}-((0([1-9]))|(1(0|1|2)))";
            if (format.compareTo(obj.getAssessMonth()) <= 0 || !obj.getAssessMonth().matches(ret)) {
                map.put("msg", "第" + (i + 1) + "行年月份不正确");
                return map;
            }
            Example example1 = new Example(AcoreAdjustRecord.class);
            example1.createCriteria().andEqualTo("assessMonth", obj.getAssessMonth().replace("-", ""));
            AcoreAdjustRecord acoreAdjustRecord = acoreAdjustRecordDao.selectOneByExample(example1);
            if (null != acoreAdjustRecord) {
                map.put("msg", "已经存在" + obj.getAssessMonth() + "的调整分");
                return map;
            }

            maps.put(obj.getAssessMonth(), bigDecimal1);

            //判断分数是否正确
            if (!scoreList.contains(String.valueOf(obj.getTaskPerScore()))) {
                map.put("msg", "第" + (i + 1) + "行评分不正确");
                return map;
            }
            if ("106".equals(obj.getTaskPerScore())) {
                obj.setTaskLevel("优秀");
            } else if ("103".equals(obj.getTaskPerScore())) {
                obj.setTaskLevel("良好");
            } else if ("100".equals(obj.getTaskPerScore())) {
                obj.setTaskLevel("合格");
            } else if ("97".equals(obj.getTaskPerScore())) {
                obj.setTaskLevel("不合格");
            }
            if (!subscoreList.contains(String.valueOf(obj.getCooperateScore()))) {
                map.put("msg", "第" + (i + 1) + "行协同评分不正确");
                return map;
            }
            //判断是否能找到部门
            Example example = new Example(DeptInfo.class);
            example.createCriteria().andEqualTo("name", obj.getDepName());
            DeptInfo deptInfo = deptInfoDao.selectOneByExample(example);
            if (null == deptInfo) {
                map.put("msg", "第" + (i + 1) + "行没有找到名称为" + obj.getDepName() + "的部门");
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
        }
        //计算相同部门相同月份的平均分
        HashMap<String, BigDecimal> mapsss = new HashMap<>();
        //初始分map
        HashMap<String, BigDecimal> mapssss = new HashMap<>();
        BigDecimal bigDecimal = new BigDecimal(0);
        for (DeptAssRecord obj : list) {
            mapsss.put(obj.getAssessMonth() + "----" + obj.getDeptId(), bigDecimal);
            mapssss.put(obj.getAssessMonth() + "----" + obj.getDeptId(), bigDecimal);
        }
        logger.info("去重年月----部门成功");
        for (String assessMonth : mapsss.keySet()) {
            BigDecimal num = new BigDecimal(0);
            BigDecimal sub = new BigDecimal(0);
            BigDecimal subs = new BigDecimal(0);
            for (DeptAssRecord obj : list) {
                if (assessMonth.equals(obj.getAssessMonth() + "----" + obj.getDeptId())) {
                    subs = sub.add(new BigDecimal(Double.parseDouble(obj.getTaskPerScore())));
                    sub = sub.add(new BigDecimal(Double.parseDouble(obj.getTaskPerScore())).add(obj.getCooperateScore()));
                    num = num.add(new BigDecimal(1));
                }
            }
            if (num.intValue() != 0) {
                BigDecimal sus = sub.divide(num, 1);
                BigDecimal suss = subs.divide(num, 1);
                mapsss.put(assessMonth, sus);
                mapssss.put(assessMonth, suss);
            }
        }

        logger.info("计算相同年月 相同部门平均分成功");
        //计算导入的数据每个月平均分
        for (String a1 : maps.keySet()) {
            BigDecimal num = new BigDecimal(0);
            BigDecimal sub = new BigDecimal(0);
            for (String a2 : mapsss.keySet()) {
                String a2tmp = a2.split("----")[0];
                if (a2tmp.equals(a1)) {
                    sub = sub.add(mapsss.get(a2));
                    num = num.add(new BigDecimal(1));
                }
            }
            if (num.intValue() != 0) {
                BigDecimal sus = sub.divide(num, 1);
                maps.put(a1, sus);
            }
        }
        logger.info("计算相同年月 不同部门平均分成功");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        ArrayList<AssessReportInfo> reportList = new ArrayList<>();
        for (String assessMonth : mapsss.keySet()) {
            AssessReportInfo assessReportInfo = new AssessReportInfo();
            assessReportInfo.setAssessMonth(assessMonth.split("----")[0]);
            assessReportInfo.setDepId(assessMonth.split("----")[1]);
            assessReportInfo.setAssessScore(mapsss.get(assessMonth));
            assessReportInfo.setCooperateRemark("");
            assessReportInfo.setInitScore(mapssss.get(assessMonth));
            ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();
            boolean flag = true;
            for (DeptAssRecord obj : list) {
                //[{"juryname":"评委1","cooperate_remark":"协同扣分原因人体人多个地方官"}]
                if (assessMonth.equals(obj.getAssessMonth() + "----" + obj.getDeptId()) && obj.getCooperateRemark() != null && obj.getCooperateRemark().trim().length() != 0) {
                    flag = false;
                    HashMap<String, String> sbmap = new HashMap<>();
                    sbmap.put("juryname", obj.getJurName());
                    sbmap.put("cooperate_remark", obj.getCooperateRemark());
                    hashMaps.add(sbmap);

                }
            }
            if (!flag) {
                Object o = JSONArray.toJSON(hashMaps);
                assessReportInfo.setCooperateRemark(o.toString());
            }

            reportList.add(assessReportInfo);
        }
        for (String assessMonth : maps.keySet()) {
            String avg = deptAssRecordDao.selectAvgForInDept(assessMonth.replace("-", ""));
            AcoreAdjustRecord acoreAdjustRecord = new AcoreAdjustRecord();
            if (null == avg) {
                acoreAdjustRecord.setSysDeptAverage(new BigDecimal(0));
                acoreAdjustRecord.setAdjustScore(new BigDecimal(0).subtract(maps.get(assessMonth)));
            } else {
                BigDecimal bigDecimal2 = new BigDecimal(avg).divide(new BigDecimal(1.0), 1);
                acoreAdjustRecord.setSysDeptAverage(new BigDecimal(nf.format(bigDecimal2)));

                acoreAdjustRecord.setAdjustScore(new BigDecimal(nf.format((bigDecimal2.subtract(maps.get(assessMonth))).doubleValue())));
            }
            acoreAdjustRecord.setImportDeptAverage(new BigDecimal(nf.format(maps.get(assessMonth))));
            acoreAdjustRecord.setAssessMonth(assessMonth);
            acoreAdjustRecord.setId(UUID.randomUUID().toString().substring(0, 7));
            justList.add(acoreAdjustRecord);
        }
        //获取depids并预先调整月报表最终分数
        for (AcoreAdjustRecord acoreAdjustRecord : justList) {
            StringBuilder sb = new StringBuilder("");
            for (AssessReportInfo assessReportInfo : reportList) {
                if (acoreAdjustRecord.getAssessMonth().equals(assessReportInfo.getAssessMonth())) {
                    //预先调整
                    assessReportInfo.setAssessScore(assessReportInfo.getAssessScore().add(acoreAdjustRecord.getAdjustScore()));
                    if ("".equals(sb.toString())) {
                        sb.append(assessReportInfo.getDepId());
                    } else {
                        sb.append("," + assessReportInfo.getDepId());
                    }
                }
            }
            acoreAdjustRecord.setDeptIds(sb.toString());
        }
        map.put("justList", justList);
        map.put("depreport", reportList);

        return map;
    }

    //开始插入
    @Override
    public Map<String, Object> insertObj(String userId, Map<String, List<Object>> map) {
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            //插入单条评价记录
            List<Object> recordlist = map.get("recordlist");
            for (Object obj : recordlist) {
                DeptAssRecord deptAssRecord = JSON.parseObject(JSON.toJSONString(obj), DeptAssRecord.class);
                deptAssRecord.setAssessMonth(deptAssRecord.getAssessMonth().replace("-", ""));
                deptAssRecord.setId(UUID.randomUUID().toString().substring(0, 7));
                deptAssRecord.setSource("02");
                deptAssRecord.setManageUser(this.getSysUserById(userId).getEmpId());
                deptAssRecord.setManageTime(DateUtil.getTime());
                deptAssRecordDao.insert(deptAssRecord);
            }
            //插入部门月报表
            List<Object> deptreportlist = map.get("depreport");
            for (Object obj : deptreportlist) {
                AssessReportInfo assessReportInfo = JSON.parseObject(JSON.toJSONString(obj), AssessReportInfo.class);
                assessReportInfo.setAssessMonth(assessReportInfo.getAssessMonth().replace("-", ""));
                assessReportInfo.setId(UUID.randomUUID().toString().substring(0, 7));
                assessReportInfo.setUserId(this.getSysUserById(userId).getEmpId());
                assessReportInfo.setManageTime(DateUtil.getTime());
                assessReportInfoDao.insert(assessReportInfo);

            }

            //插入部门月调整记录
            List<Object> justlistlist = map.get("justlist");
            for (Object obj : justlistlist) {
                AcoreAdjustRecord acoreAdjustRecord = JSON.parseObject(JSON.toJSONString(obj), AcoreAdjustRecord.class);
                acoreAdjustRecord.setAssessMonth(acoreAdjustRecord.getAssessMonth().replace("-", ""));
                acoreAdjustRecord.setManageUser(this.getSysUserById(userId).getEmpId());
                acoreAdjustRecord.setManageTime(DateUtil.getTime());
                acoreAdjustRecordDao.insert(acoreAdjustRecord);
            }
            resultMap.put("retCode", "0000");
            resultMap.put("level", "success");
            resultMap.put("retMsg", "导入成功");
        } catch (Exception e) {
            System.out.println(e);
            resultMap.put("retCode", "9999");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "导入失败");
        }
        return resultMap;
    }

    @Override
    public List<AssessReportInfo> fetchSelfDept(AssessReportInfo assessReportInfo, String loginUserId) {
        SysUser sysUser = getSysUserById(loginUserId);
        PageInfo<AssessReportInfo> parf = MybatisUtils.page(assessReportInfo, new ISelect() {
            @Override
            public void doSelect() {
                if (assessReportInfo.getFuzzy() == null || assessReportInfo.getFuzzy() == "") {
                    if (StringUtils.isEmpty(assessReportInfo.getAssessMonth())) {
                        Example example = new Example(AssessReportInfo.class);
                        example.createCriteria().andLike("assessMonth", getNowYear() + "%").andEqualTo("depId", sysUser.getDeptId());
                        List<AssessReportInfo> infoList = assessReportInfoDao.selectByExample(example);
                        logger.info("开始按照月份排序");
                        Collections.sort(infoList);
                        for (AssessReportInfo reportInfo : infoList) {
                            String str = "";
                            String otherScores = "";
                            for (AssessReportInfo info : assessReportInfoDao.fetchOtherDepartment(reportInfo)) {
                                double currentScore = info.getAssessScore().doubleValue();
                                double v = numericalTradeOff(currentScore);
                                NumberFormat nf = NumberFormat.getInstance();
                                String s = nf.format(v);
                                otherScores += s + ";";
                            }
                            logger.info("判断协同评价是否为空CooperateRemark:" + reportInfo.getCooperateRemark());
                            if (StringUtils.isEmpty(reportInfo.getCooperateRemark())) {
                                str = "";
                            } else {
                                List<CooperateRemark> beanList = ConvertUtils.getBeanList(reportInfo.getCooperateRemark(), CooperateRemark.class);
                                for (CooperateRemark remark : beanList) {
                                    if (remark != null) {
                                        str += remark.getJuryname() + "(" + remark.getCooperate_remark() + ")" + ";";
                                    }
                                }
                            }
                            String assessMonth = reportInfo.getAssessMonth();
                            StringBuilder am = new StringBuilder(assessMonth);
                            am.insert(4, "年");
                            am.insert(7, "月");
                            assessMonth = am.toString();
                            double currentScore = reportInfo.getAssessScore().doubleValue();
                            double v = numericalTradeOff(currentScore);
                            reportInfo.setAssessScore(new BigDecimal(v));
                            reportInfo.setAssessMonth(assessMonth);
                            //去除最后一个分号
                            String substring = otherScores.substring(0, otherScores.length() - 1);
                            logger.info("去除最后一个分号" + substring);
                            reportInfo.setOtherScore(substring);
                            reportInfo.setCooperateRemark(str);
                        }
                    } else {
                        //基础简单查询
                        Example example = new Example(AssessReportInfo.class);
                        example.createCriteria().andLike("assessMonth", assessReportInfo.getAssessMonth()).andEqualTo("depId", sysUser.getDeptId());
                        List<AssessReportInfo> infoList = assessReportInfoDao.selectByExample(example);
                        //按照月份排序
                        Collections.sort(infoList);
                        for (AssessReportInfo reportInfo : infoList) {
                            Example assExample = new Example(AssessReportInfo.class);
                            assExample.createCriteria().andNotEqualTo("depId", sysUser.getDeptId()).andEqualTo("assessMonth", reportInfo.getAssessMonth());
                            String otherScores = "";
                            String str = "";
                            for (AssessReportInfo info : assessReportInfoDao.selectByExample(assExample)) {
                                double currentScore = info.getAssessScore().doubleValue();
                                double v = numericalTradeOff(currentScore);
                                NumberFormat nf = NumberFormat.getInstance();
                                String s = nf.format(v);
                                otherScores += s + ";";
                            }
                            if (!StringUtils.isEmpty(reportInfo.getCooperateRemark())) {
                                List<CooperateRemark> beanList = ConvertUtils.getBeanList(reportInfo.getCooperateRemark(), CooperateRemark.class);
                                for (CooperateRemark remark : beanList) {
                                    if (remark != null) {
                                        str += remark.getJuryname() + "(" + remark.getCooperate_remark() + ")" + ";";
                                    }
                                }
                            }

                            String assessMonth = reportInfo.getAssessMonth();
                            StringBuilder am = new StringBuilder(assessMonth);
                            am.insert(4, "年");
                            am.insert(7, "月");
                            assessMonth = am.toString();
                            reportInfo.setAssessMonth(assessMonth);
                            double currentScore = reportInfo.getAssessScore().doubleValue();
                            double v = numericalTradeOff(currentScore);
                            reportInfo.setAssessScore(new BigDecimal(v));
                            String substring = otherScores.substring(0, otherScores.length() - 1);
                            reportInfo.setOtherScore(substring);
                            logger.info("去除最后一个分号substring" + substring);
                            reportInfo.setCooperateRemark(str);
                        }
                    }
                } else {
                    if (StringUtils.isEmpty(assessReportInfo.getAssessMonth())) {
                        Example example = new Example(AssessReportInfo.class);
                        example.createCriteria().andLike("assessMonth", getNowYear() + "%").andEqualTo("depId", sysUser.getDeptId()).andLike("assessScore", "%" + assessReportInfo.getFuzzy() + "%");
                        List<AssessReportInfo> infoList = assessReportInfoDao.selectByExample(example);
                        //按照月份排序
                        Collections.sort(infoList);
                        for (AssessReportInfo reportInfo : infoList) {
                            String str = "";
                            String otherScores = "";
                            for (AssessReportInfo info : assessReportInfoDao.fetchOtherDepartment(reportInfo)) {
                                double currentScore = info.getAssessScore().doubleValue();
                                double v = numericalTradeOff(currentScore);
                                NumberFormat nf = NumberFormat.getInstance();
                                String s = nf.format(v);
                                otherScores += s + ";";
                            }
                            logger.info("判断协同评价是否为空CooperateRemark:" + reportInfo.getCooperateRemark());
                            if (StringUtils.isEmpty(reportInfo.getCooperateRemark())) {
                                str = "";
                            } else {
                                List<CooperateRemark> beanList = ConvertUtils.getBeanList(reportInfo.getCooperateRemark(), CooperateRemark.class);
                                for (CooperateRemark remark : beanList) {
                                    if (remark != null) {
                                        str += remark.getJuryname() + "(" + remark.getCooperate_remark() + ")" + ";";
                                    }
                                }
                            }
                            String assessMonth = reportInfo.getAssessMonth();
                            StringBuilder am = new StringBuilder(assessMonth);
                            am.insert(4, "年");
                            am.insert(7, "月");
                            assessMonth = am.toString();
                            double currentScore = reportInfo.getAssessScore().doubleValue();
                            double v = numericalTradeOff(currentScore);
                            reportInfo.setAssessScore(new BigDecimal(v));
                            reportInfo.setAssessMonth(assessMonth);
                            String substring = otherScores.substring(0, otherScores.length() - 1);
                            reportInfo.setOtherScore(substring);
                            logger.info("去除最后一个分号substring:" + substring);
                            reportInfo.setCooperateRemark(str);
                        }
                    }
                }
            }
        });
        return parf.getList();

    }

    /**
     * 初级评委部门评价展示
     *
     * @Param: [sysUserId]
     * @return: java.util.Map
     * @Date: 2019/11/26
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map getAllMessages(String sysUserId) {
        HashMap<String, Object> resultMap = new HashMap<>();
        SysUser sysUser = getSysUserById(sysUserId);
        if (StringUtils.isEmpty(sysUser.getDeptId())) {
            return null;
        }
        Example identityExample = new Example(DeptInfo.class);
        identityExample.createCriteria().andEqualTo("id", sysUser.getDeptId());
        DeptInfo depInfo = deptInfoDao.selectOneByExample(identityExample);
        if ("0".equals(depInfo.getNeedAssessment())) {
            resultMap.put("needAssessment", "0");
        } else {
            resultMap.put("needAssessment", "1");
        }
        if (StringUtils.isEmpty(depInfo.getParent())) {
            return null;
        }
        //这个月份的所有属于同一顶级部门的平级部门
        Example example = new Example(DeptInfo.class);
        example.createCriteria().andEqualTo("parent", depInfo.getParent()).andEqualTo("needAssessment", "1");
        List<DeptInfo> SecondDepList = deptInfoDao.selectByExample(example);
        int depCount = SecondDepList.size();
        //这个月份本人评价过的部门
        Example deAssExample = new Example(DeptAssRecord.class);
        deAssExample.createCriteria().andEqualTo("assessMonth", buildOuTTradeNo()).andNotEqualTo("deptId", sysUser.getDeptId()).andEqualTo("juryId", sysUser.getEmpId());
        List<DeptAssRecord> deAssRecordList = deptAssRecordDao.selectByExample(deAssExample);
        List<DeptAssRecord> collect = removeImportedDepartment(deAssRecordList);
        // 判断本级部门是否完成评价    selfAssessment  0:本级未完成  1:本级已完成
        if ("0".equals(depInfo.getNeedAssessment())) {
            if (collect.size() == depCount) {
                resultMap.put("selfAssessment", "1");
            } else {
                resultMap.put("selfAssessment", "0");
            }
        } else {
            if (collect.size() == depCount - 1) {
                resultMap.put("selfAssessment", "1");
            } else {
                resultMap.put("selfAssessment", "0");
            }
        }
        DepartmentData[] tableData = new DepartmentData[depCount];
        for (int i = 0; i < depCount; i++) {
            DepartmentData departmentData = new DepartmentData();
            departmentData.setDepartment(SecondDepList.get(i).getName());
            tableData[i] = departmentData;
        }
        resultMap.put("tableData", tableData);
        Example empInfoExample = new Example(EmpInfo.class);
        empInfoExample.createCriteria().andEqualTo("id", sysUser.getEmpId());
        EmpInfo empInfo = empInfoDao.selectOneByExample(empInfoExample);
        if (!StringUtils.isEmpty(empInfo)) {
            Example deptExample = new Example(DeptInfo.class);
            deptExample.createCriteria().andEqualTo("id", empInfo.getDeptId());
            DeptInfo deptInfo = deptInfoDao.selectOneByExample(deptExample);
            resultMap.put("currentDepartment", deptInfo.getName());
            resultMap.put("initialScore", null);
            resultMap.put("causeInitialValue", null);
            return resultMap;

        }
        return null;
    }

    /**
     * 初级评委部门评价提交
     *
     * @Param: [loginUserId, data]
     * @return: void
     * @Date: 2019/11/26
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void primaryEvaluation(String loginUserId, String data) {
        //[{"department":"二级部门2-1","jobWork":"103","initialScore":"","causeInitialValue":""}]
        //查出此员工id
        SysUser sysUser = getSysUserById(loginUserId);
        List<PrimaryEvaluationData> dataList = ConvertUtils.getBeanList(data, PrimaryEvaluationData.class);
        for (PrimaryEvaluationData priEvalData : dataList) {
            //根据部门name查询id
            Example example = new Example(DeptInfo.class);
            example.createCriteria().andEqualTo("name", priEvalData.getDepartment());
            DeptInfo deptInfo = deptInfoDao.selectOneByExample(example);
            //先查有没有这条记录
            Example deptExample = new Example(DeptAssRecord.class);
            deptExample.createCriteria().andEqualTo("deptId", deptInfo.getId()).andEqualTo("juryId", sysUser.getEmpId()).andEqualTo("assessMonth", buildOuTTradeNo());
            DeptAssRecord deptAssRecordFind = deptAssRecordDao.selectOneByExample(deptExample);
            if (deptAssRecordFind != null) {
                deptAssRecordFind.setTaskLevel(NumChangeToChinese(priEvalData.getJobWork()));
                deptAssRecordFind.setTaskPerScore(priEvalData.getJobWork());
                if (!"".equals(priEvalData.getInitialScore())) {
                    BigDecimal cooperateScore1 = new BigDecimal(priEvalData.getInitialScore());
                    deptAssRecordFind.setCooperateScore(cooperateScore1);
                } else {
                    deptAssRecordFind.setCooperateScore(null);
                }
                if (!"".equals(priEvalData.getCauseInitialValue())) {
                    deptAssRecordFind.setCooperateRemark(priEvalData.getCauseInitialValue());
                }
                deptAssRecordFind.setManageTime(DateUtil.getTime());
                int update = deptAssRecordDao.updateByPrimaryKey(deptAssRecordFind);
                if (update == 1) {
                    logger.info("评价记录修改成功!");
                } else {
                    logger.info("评价记录修改失败!");
                }
            } else {
                //组装数据
                DeptAssRecord deptAssRecord = new DeptAssRecord();
                deptAssRecord.setDeptId(deptInfo.getId());
                deptAssRecord.setId(createUuid());
                deptAssRecord.setAssessMonth(buildOuTTradeNo());
                deptAssRecord.setTaskLevel(NumChangeToChinese(priEvalData.getJobWork()));
                deptAssRecord.setTaskPerScore(priEvalData.getJobWork());
                if (!"".equals(priEvalData.getInitialScore())) {
                    BigDecimal newScore = new BigDecimal(priEvalData.getInitialScore());
                    deptAssRecord.setCooperateScore(newScore);
                } else {
                    deptAssRecord.setCooperateScore(null);
                }
                if (!"".equals(priEvalData.getCauseInitialValue())) {
                    deptAssRecord.setCooperateRemark(priEvalData.getCauseInitialValue());
                } else {
                    deptAssRecord.setCooperateRemark(null);
                }
                deptAssRecord.setJuryId(sysUser.getEmpId());
                deptAssRecord.setSource("01");
                deptAssRecord.setManageTime(DateUtil.getTime());
                deptAssRecord.setManageUser(sysUser.getEmpId());
                int insert = deptAssRecordDao.insert(deptAssRecord);
                if (insert == 1) {
                    logger.info("评价记录保存成功!");
                } else {
                    logger.info("评价记录保存失败!");
                }
            }
        }
    }

    /**
     * 二级评委评价展示
     *
     * @Param: [loginUserId]
     * @return: java.util.Map
     * @Date: 2019/11/18
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map twoLevelEvaluation(String loginUserId) {
        HashMap<String, Object> resultMap = new HashMap<>();
        SysUser sysUser = getSysUserById(loginUserId);
        Example identityExample = new Example(DeptInfo.class);
        identityExample.createCriteria().andEqualTo("id", sysUser.getDeptId());
        DeptInfo depInfo = deptInfoDao.selectOneByExample(identityExample);
        if (!StringUtils.isEmpty(depInfo.getParent())) {
            return null;
        }
        //判断下级部门是否完成评价  首先查出先查询所管辖范围内的这个月份参与考核的二级部门(二级部门的数量*(二级部门的数量减一)与在记录表中查询所有二级部门评价记录作比较)
        //1.先查询所管辖范围内的这个月份参与考核的二级部门
        Example deptInfoExample = new Example(DeptInfo.class);
        deptInfoExample.createCriteria().andEqualTo("parent", sysUser.getDeptId()).andEqualTo("needAssessment", "1").andEqualTo("status", "0000");
        List<DeptInfo> deptInfoList = deptInfoDao.selectByExample(deptInfoExample);
        //2.二级部门的数量
        int secondDepNum = deptInfoList.size();
        //3.管辖范围内所有二级部门中所有评价记录的数量
        int recordCount = 0;
        for (DeptInfo deptInfo : deptInfoList) {
            //查询所有二级部门评价记录
            Example deptAssExample = new Example(DeptAssRecord.class);
            deptAssExample.createCriteria().andEqualTo("assessMonth", buildOuTTradeNo()).andEqualTo("deptId", deptInfo.getId()).andIsNotNull("cooperateScore");
            List<DeptAssRecord> deAssRecordList = deptAssRecordDao.selectByExample(deptAssExample);
            recordCount += deAssRecordList.size();
        }
        //4.二级部门的数量*(二级部门的数量减一)与在记录表中查询所有二级部门评价记录作比较
        int count = secondDepNum * (secondDepNum - 1);
        if (count == recordCount) {
            resultMap.put("subordinate", "1");
        } else {
            resultMap.put("subordinate", "0");
        }
        //判断本级是否评价  自己的评价记录条数与所有参与考核的部门数量比较
        Example eptAssRecordExample = new Example(DeptAssRecord.class);
        eptAssRecordExample.createCriteria().andEqualTo("assessMonth", buildOuTTradeNo()).andEqualTo("juryId", sysUser.getEmpId());
        List<DeptAssRecord> deptRecordList = deptAssRecordDao.selectByExample(eptAssRecordExample);
        List<DeptAssRecord> collect = removeImportedDepartment(deptRecordList);
        if (secondDepNum == collect.size()) {
            resultMap.put("selfAssessment", "1");
        } else {
            resultMap.put("selfAssessment", "0");
        }
        logger.info("当月所有参与考核的部门:" + deptInfoList);
        Object[] resultArr = new Object[deptInfoList.size()];
        for (int i = 0; i < deptInfoList.size(); i++) {
            ArrayList<String> evaluationList = new ArrayList<>();
            Example example = new Example(DeptAssRecord.class);
            example.createCriteria().andEqualTo("deptId", deptInfoList.get(i).getId()).andEqualTo("assessMonth", buildOuTTradeNo());
            List<DeptAssRecord> deptAssRecordList = deptAssRecordDao.selectByExample(example);
            logger.info("查询本部门的所有考核记录" + deptAssRecordList);
            double cooperateScore = 0.0;
            for (DeptAssRecord deptAssRecord : deptAssRecordList) {
                //查询部门名称
                Example empInfoExample = new Example(EmpInfo.class);
                empInfoExample.createCriteria().andEqualTo("id", deptAssRecord.getJuryId());
                EmpInfo empInfo = empInfoDao.selectOneByExample(empInfoExample);
                if (!StringUtils.isEmpty(deptAssRecord.getCooperateScore()) && !StringUtils.isEmpty(deptAssRecord.getCooperateRemark())) {
                    cooperateScore += deptAssRecord.getCooperateScore().doubleValue();
                    //首先将当前部门的所有评价存到list中,然后将list放入数组中
                    evaluationList.add("(" + deptAssRecord.getCooperateRemark() + "——" + empInfo.getName() + ")");
                }
            }
            String evaluation = String.join(",", evaluationList);
            CollaborativePoint collaborativePoint = new CollaborativePoint();
            collaborativePoint.setDepartment(deptInfoList.get(i).getName());
            collaborativePoint.setInitialScore(cooperateScore + "");
            collaborativePoint.setCauseInitialValue(evaluation);
            resultArr[i] = collaborativePoint;
        }
        logger.info("最后组装的数据为:" + resultArr);
        resultMap.put("data", resultArr);
        logger.info("数据返回前端:::" + resultMap);
        return resultMap;

    }

    /**
     * 二级评委评价提交
     *
     * @Param: [loginUserId, data]
     * @return: void
     * @Date: 2019/11/18
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void secondaryToSubmit(String loginUserId, String data) {
        SysUser sysUser = getSysUserById(loginUserId);
        List<DepartmentAndscore> beanList = ConvertUtils.getBeanList(data, DepartmentAndscore.class);
        for (DepartmentAndscore departAndSco : beanList) {
            logger.info("根据部门name查询id");
            //根据部门name查询id
            Example example = new Example(DeptInfo.class);
            example.createCriteria().andEqualTo("name", departAndSco.getDepartment());
            DeptInfo deptInfo = deptInfoDao.selectOneByExample(example);
            logger.info("先查有没有这条记录");
            //先查有没有这条记录
            Example deptExample = new Example(DeptAssRecord.class);
            deptExample.createCriteria().andEqualTo("deptId", deptInfo.getId()).andEqualTo("juryId", sysUser.getEmpId()).andEqualTo("assessMonth", buildOuTTradeNo());
            DeptAssRecord deptAssRecordFind = deptAssRecordDao.selectOneByExample(deptExample);
            if (deptAssRecordFind != null) {
                logger.info("有这条记录,去修改记录");
                deptAssRecordFind.setTaskLevel(NumChangeToChinese(departAndSco.getInitialScore()));
                deptAssRecordFind.setTaskPerScore(departAndSco.getInitialScore());
                deptAssRecordFind.setManageTime(DateUtil.getTime());
                int i = deptAssRecordDao.updateByPrimaryKey(deptAssRecordFind);
                if (i == 1) {
                    logger.info("修改数据库成功!");
                } else {
                    logger.info("修改数据库成功!");
                }
            } else {
                logger.info("无这条记录,去添加记录");
                DeptAssRecord deptAssRecord = new DeptAssRecord();
                logger.info("开始组装数据库需要的数据");
                deptAssRecord.setId(createUuid());
                deptAssRecord.setDeptId(deptInfo.getId());
                deptAssRecord.setAssessMonth(buildOuTTradeNo());
                deptAssRecord.setTaskLevel(NumChangeToChinese(departAndSco.getInitialScore()));
                deptAssRecord.setTaskPerScore(departAndSco.getInitialScore());
                deptAssRecord.setCooperateScore(null);
                deptAssRecord.setCooperateRemark(null);
                deptAssRecord.setJuryId(sysUser.getEmpId());
                deptAssRecord.setSource("01");
                deptAssRecord.setManageTime(DateUtil.getTime());
                deptAssRecord.setManageUser(sysUser.getEmpId());
                int insert = deptAssRecordDao.insert(deptAssRecord);
                if (insert == 1) {
                    logger.info("存入数据库成功!");
                } else {
                    logger.info("存入数据库失败!");
                }
            }
        }


    }

    /**
     * 顶级评委评价展示
     *
     * @Param: [loginUserId]
     * @return: java.util.Map
     * @Date: 2019/11/18
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map topLevel(String loginUserId) {
        logger.info("判断该部门副主任是否完成评价");
        // 判断该部门副主任是否完成评价
        HashMap<String, Object> resultMap = new HashMap<>();
        SysUser sysUser = getSysUserById(loginUserId);
        //先查询所管辖范围内的这个月份参与考核的二级部门
        Example deptInfoExample = new Example(DeptInfo.class);
        deptInfoExample.createCriteria().andEqualTo("parent", sysUser.getDeptId()).andEqualTo("needAssessment", "1").andEqualTo("status", "0000");
        List<DeptInfo> deptInfoList = deptInfoDao.selectByExample(deptInfoExample);
        logger.info("先查询所管辖范围内的这个月份参与考核的二级部门:" + deptInfoList);
        //查询该部门的副主任
        Example empExample = new Example(EmpInfo.class);
        empExample.createCriteria().andEqualTo("deptId", sysUser.getDeptId()).andEqualTo("grade", "1");
        List<EmpInfo> empInfos = empInfoDao.selectByExample(empExample);
        List<SysUser> sysUsers = sysUserDao.selectAll();
        List<String> stringList = sysUsers.stream().map(e -> e.getEmpId()).collect(Collectors.toList());
        EmpInfo empInfo = empInfos.stream().filter(e -> stringList.contains(e.getId())).collect(Collectors.toList()).get(0);
        //在评价记录表中查询副主任当月的所有评价
        Example example = new Example(DeptAssRecord.class);
        example.createCriteria().andEqualTo("juryId", empInfo.getId()).andEqualTo("assessMonth", buildOuTTradeNo());
        List<DeptAssRecord> deptAssRecordList = deptAssRecordDao.selectByExample(example);
        List<DeptAssRecord> collect = removeImportedDepartment(deptAssRecordList);
        logger.info("在评价记录表中查询副主任当月的所有评价" + collect);
        if (deptInfoList.size() == collect.size()) {
            resultMap.put("subordinate", "1");
        } else {
            resultMap.put("subordinate", "0");
        }
        Example exampleTop = new Example(DeptAssRecord.class);
        exampleTop.createCriteria().andEqualTo("juryId", sysUser.getEmpId()).andEqualTo("assessMonth", buildOuTTradeNo());
        List<DeptAssRecord> topAssRecordList = deptAssRecordDao.selectByExample(exampleTop);
        List<DeptAssRecord> collectNew = removeImportedDepartment(topAssRecordList);
        if (deptInfoList.size() == collectNew.size()) {
            resultMap.put("selfAssessment", "1");
        } else {
            resultMap.put("selfAssessment", "0");
        }
        //  List<DeptInfo>  = deptInfoService.getAllSecondDep();
        logger.info("当月所有参与考核的部门:" + deptInfoList);
        Object[] resultArr = new Object[deptInfoList.size()];
        for (int i = 0; i < deptInfoList.size(); i++) {
            ArrayList<String> evaluationList = new ArrayList<>();
            Example exampleDept = new Example(DeptAssRecord.class);
            exampleDept.createCriteria().andEqualTo("deptId", deptInfoList.get(i).getId()).andEqualTo("assessMonth", buildOuTTradeNo());
            List<DeptAssRecord> deptRecordList = deptAssRecordDao.selectByExample(exampleDept);
            logger.info("查询本部门的所有考核记录" + deptRecordList);
            double cooperateScore = 0.0;
            for (DeptAssRecord deptAssRecord : deptRecordList) {
                //查询部门名称
                Example empInfoExample = new Example(EmpInfo.class);
                empInfoExample.createCriteria().andEqualTo("id", deptAssRecord.getJuryId());
                EmpInfo empInfoNew = empInfoDao.selectOneByExample(empInfoExample);
                if (!StringUtils.isEmpty(deptAssRecord.getCooperateScore()) && !StringUtils.isEmpty(deptAssRecord.getCooperateRemark())) {
                    cooperateScore += deptAssRecord.getCooperateScore().doubleValue();
                    //首先将当前部门的所有评价存到list中,然后将list放入数组中
                    evaluationList.add("(" + deptAssRecord.getCooperateRemark() + "——" + empInfoNew.getName() + ")");
                }
            }
            String evaluation = String.join(",", evaluationList);
            CollaborativePoint collaborativePoint = new CollaborativePoint();
            collaborativePoint.setDepartment(deptInfoList.get(i).getName());
            collaborativePoint.setInitialScore(cooperateScore + "");
            collaborativePoint.setCauseInitialValue(evaluation);
            resultArr[i] = collaborativePoint;
        }
        logger.info("最后组装的数据为:" + resultArr);
        resultMap.put("data", resultArr);
        logger.info("数据返回前端:::" + resultMap);
        return resultMap;

    }

    /**
     * 测算
     *
     * @Param:
     * @return:
     * @Date: 2019/11/26
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map topEvaluation(String loginUserId, String data) {
        logger.info("判断该部门副主任是否完成评价");
        // 判断该部门副主任是否完成评价
        logger.info(data);
        HashMap<String, Object> resultMap = new HashMap<>();
        SysUser sysUser = getSysUserById(loginUserId);
        //先查询所管辖范围内的这个月份参与考核的二级部门
        Example deptInfoExample = new Example(DeptInfo.class);
        deptInfoExample.createCriteria().andEqualTo("parent", sysUser.getDeptId()).andEqualTo("needAssessment", "1").andEqualTo("status", "0000");
        List<DeptInfo> deptInfoList = deptInfoDao.selectByExample(deptInfoExample);
        logger.info("先查询所管辖范围内的这个月份参与考核的二级部门:" + deptInfoList);
        logger.info("查询所管辖范围内的这个月份参与考核的二级部门" + deptInfoList);
        Object[] resultArr = new Object[deptInfoList.size()];
        if (!StringUtils.isEmpty(deptInfoList)) {
            for (int i = 0; i < deptInfoList.size(); i++) {
                DeptInfo deptInfo = deptInfoList.get(i);
                Example excellentExample = new Example(DeptAssRecord.class);
                excellentExample.createCriteria().andEqualTo("taskLevel", "优秀").andEqualTo("assessMonth", buildOuTTradeNo()).andEqualTo("deptId", deptInfo.getId());
                List<DeptAssRecord> excellentList = deptAssRecordDao.selectByExample(excellentExample);
                int excellentQuantity = excellentList.size();
                Example goodExample = new Example(DeptAssRecord.class);
                goodExample.createCriteria().andEqualTo("taskLevel", "良好").andEqualTo("assessMonth", buildOuTTradeNo()).andEqualTo("deptId", deptInfo.getId());
                List<DeptAssRecord> goodList = deptAssRecordDao.selectByExample(goodExample);
                int goodQuantity = goodList.size();
                Example qualifiedExample = new Example(DeptAssRecord.class);
                qualifiedExample.createCriteria().andEqualTo("taskLevel", "合格").andEqualTo("assessMonth", buildOuTTradeNo()).andEqualTo("deptId", deptInfo.getId());
                List<DeptAssRecord> qualifiedList = deptAssRecordDao.selectByExample(qualifiedExample);
                int qualifiedQuantity = qualifiedList.size();
                Example unqualifiedExample = new Example(DeptAssRecord.class);
                unqualifiedExample.createCriteria().andEqualTo("taskLevel", "不合格").andEqualTo("assessMonth", buildOuTTradeNo()).andEqualTo("deptId", deptInfo.getId());
                List<DeptAssRecord> unqualifiedList = deptAssRecordDao.selectByExample(unqualifiedExample);
                int unqualifiedQuantity = unqualifiedList.size();
                logger.info("该部门的优秀，良好，合格，不合格的数量分别为" + excellentQuantity + ";" + goodQuantity + ";" + qualifiedQuantity + ";" + unqualifiedQuantity);
                //当前得分
                Example currentScoreExample = new Example(DeptAssRecord.class);
                currentScoreExample.createCriteria().andEqualTo("assessMonth", buildOuTTradeNo()).andEqualTo("deptId", deptInfo.getId());
                List<DeptAssRecord> currentScoreList = deptAssRecordDao.selectByExample(currentScoreExample);
                double totalScore = 0.0;
                double deductionOfPoints = 0.0;
                //将顶级部门的评价结果添加到测算中
                List<DepartmentAndscore> beanList = ConvertUtils.getBeanList(data, DepartmentAndscore.class);
                for (DepartmentAndscore departmentAndscore : beanList) {
                    //根据部门名称查出部门id
                    Example deptExample = new Example(DeptInfo.class);
                    deptExample.createCriteria().andEqualTo("name", departmentAndscore.getDepartment());
                    DeptInfo deptInfoFind = deptInfoDao.selectOneByExample(deptExample);
                    if (deptInfo.getId().equals(deptInfoFind.getId())) {
                        String score = departmentAndscore.getInitialScore();
                        if ("106".equals(score)) {
                            excellentQuantity = excellentQuantity + 1;
                            totalScore = totalScore + 106.0;
                        } else if ("103".equals(score)) {
                            goodQuantity = goodQuantity + 1;
                            totalScore = totalScore + 103.0;
                        } else if ("100".equals(score)) {
                            qualifiedQuantity = qualifiedQuantity + 1;
                            totalScore = totalScore + 100.0;
                        } else {
                            unqualifiedQuantity = unqualifiedQuantity + 1;
                            totalScore = totalScore + 97.0;
                        }
                    }
                }
                for (DeptAssRecord deptAssRecord : currentScoreList) {
                    if (!StringUtils.isEmpty(deptAssRecord.getCooperateScore())) {
                        deductionOfPoints += deptAssRecord.getCooperateScore().doubleValue();
                    }
                    totalScore += Double.parseDouble(deptAssRecord.getTaskPerScore());
                }
                double result = totalScore + deductionOfPoints;
                logger.info("本部门的总得分为" + totalScore);
                logger.info("本部门的扣分为" + deductionOfPoints);
                logger.info("本部门的最终得分为" + result);
                //初级与二级评价过的数量+终极评委评价的数量
                double size = (double) currentScoreList.size() + 1;
                logger.info("评价的数量为" + size);
                double v = 0.0;
                if (size != 0) {
                    v = numericalTradeOff(result / size);
                }
                logger.info("当前得分为" + v);
                // double currentScore = div(size, totalScore, 1);
                //部门:department  当前得分:currentScore  优秀数量:excellentQuantity  良好数量:goodQuantity  合格数量:qualifiedQuantity  不合格数量:unqualifiedQuantity  扣分合计:deductionOfPoints
                SimulationCalculation calculation = new SimulationCalculation();
                calculation.setDepartment(deptInfo.getName());
                calculation.setCurrentScore(Double.toString(v));
                calculation.setExcellentQuantity(Integer.toString(excellentQuantity));
                calculation.setGoodQuantity(Integer.toString(goodQuantity));
                calculation.setQualifiedQuantity(Integer.toString(qualifiedQuantity));
                calculation.setUnqualifiedQuantity(Integer.toString(unqualifiedQuantity));
                calculation.setDeductionOfPoints(Double.toString(deductionOfPoints));
                resultArr[i] = calculation;
            }
            resultMap.put("calculate", resultArr);
        }
        logger.info("将测算结果返回前端");
        return resultMap;
    }

    /**
     * 最终提交
     *
     * @Param: [loginUserId, data]
     * @return: void
     * @Date: 2019/11/26
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void finalToSubmit(String loginUserId, String data) {
        SysUser sysUser = getSysUserById(loginUserId);
        List<DepartmentAndscore> beanList = ConvertUtils.getBeanList(data, DepartmentAndscore.class);
        for (DepartmentAndscore departAndSco : beanList) {
            //根据部门name查询id
            Example example = new Example(DeptInfo.class);
            example.createCriteria().andEqualTo("name", departAndSco.getDepartment());
            DeptInfo deptInfo = deptInfoDao.selectOneByExample(example);
            //先查有没有这条记录
            Example deptExample = new Example(DeptAssRecord.class);
            deptExample.createCriteria().andEqualTo("deptId", deptInfo.getId()).andEqualTo("juryId", sysUser.getEmpId()).andEqualTo("assessMonth", buildOuTTradeNo());
            DeptAssRecord deptAssRecordFind = deptAssRecordDao.selectOneByExample(deptExample);
            if (deptAssRecordFind != null) {
                deptAssRecordFind.setTaskLevel(NumChangeToChinese(departAndSco.getInitialScore()));
                deptAssRecordFind.setTaskPerScore(departAndSco.getInitialScore());
                deptAssRecordFind.setManageTime(DateUtil.getTime());
                int i = deptAssRecordDao.updateByPrimaryKey(deptAssRecordFind);
                if (i == 1) {
                    logger.info("修改考核记录表成功!");
                } else {
                    logger.info("修改考核记录表成功!");
                }
            } else {
                DeptAssRecord deptAssRecord = new DeptAssRecord();
                logger.info("开始组装考核记录表需要的数据");
                deptAssRecord.setId(createUuid());
                deptAssRecord.setDeptId(deptInfo.getId());
                deptAssRecord.setAssessMonth(buildOuTTradeNo());
                deptAssRecord.setTaskLevel(NumChangeToChinese(departAndSco.getInitialScore()));
                deptAssRecord.setTaskPerScore(departAndSco.getInitialScore());
                deptAssRecord.setCooperateScore(null);
                deptAssRecord.setCooperateRemark(null);
                deptAssRecord.setJuryId(sysUser.getEmpId());
                deptAssRecord.setSource("01");
                deptAssRecord.setManageTime(DateUtil.getTime());
                deptAssRecord.setManageUser(sysUser.getEmpId());
                int insert = deptAssRecordDao.insert(deptAssRecord);
                if (insert == 1) {
                    logger.info("存入考核记录表成功!");
                } else {
                    logger.info("存入考核记录表失败!");
                }
            }
            AssessReportInfo deptAssessReport = new AssessReportInfo();
            deptAssessReport.setId(createUuid());
            deptAssessReport.setDepId(deptInfo.getId());
            deptAssessReport.setAssessMonth(buildOuTTradeNo());
            //(根据部门id查询这月总记录条数)
            Example initExample = new Example(DeptAssRecord.class);
            initExample.createCriteria().andEqualTo("deptId", deptInfo.getId()).andEqualTo("assessMonth", buildOuTTradeNo());
            List<DeptAssRecord> deptAssRecordList = deptAssRecordDao.selectByExample(initExample);
            //最初得分总和
            double initRecordNum = 0.0;
            //扣分总和
            double assScoreNum = 0.0;
            List<HashMap<String, String>> arrayList = new ArrayList<>();
            JSONArray arr = new JSONArray();
            for (int i = 0; i < deptAssRecordList.size(); i++) {
                initRecordNum += Double.parseDouble(deptAssRecordList.get(i).getTaskPerScore());
                if (!StringUtils.isEmpty(deptAssRecordList.get(i).getCooperateScore())) {
                    assScoreNum += deptAssRecordList.get(i).getCooperateScore().doubleValue();
                }
                Example empInfoExample = new Example(EmpInfo.class);
                empInfoExample.createCriteria().andEqualTo("id", deptAssRecordList.get(i).getJuryId());
                EmpInfo empInfo = empInfoDao.selectOneByExample(empInfoExample);
                HashMap<String, String> insertMap = new HashMap<>();
                if (deptAssRecordList.get(i).getCooperateRemark() != null) {
                    insertMap.put("juryname", empInfo.getName());
                    insertMap.put("cooperate_remark", deptAssRecordList.get(i).getCooperateRemark());
                    arrayList.add(i, insertMap);
                } else {
                    arrayList.add(i, null);
                }
            }
            double initScore = initRecordNum / deptAssRecordList.size();
            double floor = Math.floor(initScore);
            if (floor + 0.5 < initScore) {
                initScore = floor + 1;
            } else if (floor + 0.5 == initScore + 0.5) {
                initScore = floor;
            } else {
                initScore = floor + 0.5;
            }
            //绩效最初评分
            deptAssessReport.setInitScore(BigDecimal.valueOf(initScore));
            logger.info("绩效最初评分" + BigDecimal.valueOf(initScore));
            //绩效最终得分(先查询出他这月份的考核条数)
            double aScore = initRecordNum + assScoreNum;
            double assessScore = aScore / deptAssRecordList.size();
            logger.info("绩效最终得分" + assessScore);
            double floorAss = Math.floor(assessScore);
            if (floorAss + 0.5 < assessScore) {
                assessScore = floorAss + 1;
            } else if (floorAss + 0.5 == assessScore + 0.5) {
                assessScore = floorAss;
            } else {
                assessScore = floorAss + 0.5;
            }
            deptAssessReport.setAssessScore(new BigDecimal(assessScore));
            //评分原因
            deptAssessReport.setCooperateRemark(JSONObject.toJSONString(arrayList));
            deptAssessReport.setManageTime(DateUtil.getTime());
            int insert = assessReportInfoDao.insert(deptAssessReport);
            if (insert == 1) {
                logger.info("参与考核的部门存入部门绩效月报表成功!");
            } else {
                logger.info("参与考核的部门存入部门绩效月报表失败!");
            }
        }
        logger.info("将不参与考核的部门导入部门绩效月报表,都默认为100分");
        Example example = new Example(DeptInfo.class);
        example.createCriteria().andEqualTo("needAssessment", "0");
        List<DeptInfo> deptInfoList = deptInfoDao.selectByExample(example);
        logger.info("查询所有不参与考核的部门" + deptInfoList);
        for (DeptInfo deptInfo : deptInfoList) {
            AssessReportInfo deptAssessReport = new AssessReportInfo();
            deptAssessReport.setId(createUuid());
            deptAssessReport.setDepId(deptInfo.getId());
            deptAssessReport.setAssessMonth(buildOuTTradeNo());
            deptAssessReport.setAssessScore(new BigDecimal(100));
            deptAssessReport.setInitScore(new BigDecimal(100));
            deptAssessReport.setCooperateRemark(null);
            deptAssessReport.setManageTime(DateUtil.getTime());
            int insert = assessReportInfoDao.insert(deptAssessReport);
            if (insert == 1) {
                logger.info("不参与考核的部门存入部门绩效月报表成功!");
            } else {
                logger.info("不参与考核的部门存入部门绩效月报表失败!");
            }
        }
    }

    /**
     * 获取当前年月yyyymm
     *
     * @Param:
     * @return:
     * @Date: 2019/11/8
     */
    private String buildOuTTradeNo() {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(1);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMM");
        return formatters.format(today);
    }


    private String getNowYear() {

        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String substring = time.substring(0, 4);
        return substring;

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

    private String NumChangeToChinese(String number) {
        //优秀106,良好103,合格100,不合格97
        if ("106".equals(number)) {
            return "优秀";
        } else if ("103".equals(number)) {
            return "良好";
        } else if ("100".equals(number)) {
            return "合格";
        } else if ("97".equals(number)) {
            return "不合格";
        } else {
            return null;
        }
    }

    private SysUser getSysUserById(String loginUserId) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", loginUserId);
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        return sysUser;
    }

    /**
     * 评价进度
     *
     * @Param: [loginUserId]
     * @return: java.util.List<com.bestvike.standplat.data.SysUser>
     * @Date: 2019/11/26
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<SysUser> evaluationProgress(String loginUserId) {
        SysUser sysUser = getSysUserById(loginUserId);
        PageInfo<SysUser> psu = MybatisUtils.page(sysUser, new ISelect() {
            @Override
            public void doSelect() {
                logger.info("查询本人是否为评委");
                Example example = new Example(SysUser.class);
                Example.Criteria and = example.and();
                and.andIsNotNull("deptId");
                and.andLike("roles", "%" + "0010" + "%").orLike("roles", "%" + "0002" + "%").orLike("roles", "%" + "0011" + "%").orLike("roles", "%" + "0012" + "%");
                List<SysUser> sysUsers = sysUserDao.selectByExample(example);
                for (SysUser user : sysUsers) {
                    logger.info("查询员工的角色,如果为系统外部门评委,部门评价则显示-,反之正常评价");
                    if ("0012".equals(user.getRoles())) {
                        user.setDepartmentEvaluate("-");
                    } else {
                        Example deptAssExample = new Example(DeptAssRecord.class);
                        deptAssExample.createCriteria().andEqualTo("juryId", user.getEmpId()).andEqualTo("assessMonth", lastMonth());
                        List<DeptAssRecord> assRecordList = deptAssRecordDao.selectByExample(deptAssExample);
                        if (assRecordList.size() > 0) {
                            //部门是否评价
                            user.setDepartmentEvaluate("已评价");
                        } else {
                            user.setDepartmentEvaluate("未评价");
                        }
                    }
                    //员工考核记录表
                    EmpAssessRecord empAssessRecord = new EmpAssessRecord();
                    empAssessRecord.setJuryId(user.getEmpId());
                    empAssessRecord.setAssessMonth(lastMonth());
                    List<EmpAssessRecord> empRecordList =  empAssessRecordDao.selectByCondition(empAssessRecord);
                    //判断员工几级评委
                    DeptInfo deptInfo = deptInfoDao.selectByPrimaryKey(user.getDeptId());
                    if (deptInfo != null) {
                        Integer grade = deptInfo.getGrade();
                        if (grade == 0) {
                            user.setStaffEvaluate("-");
                        } else {
                            if (empRecordList.size() > 0) {
                                //员工是否评价
                                user.setStaffEvaluate("已评价");
                            } else {
                                user.setStaffEvaluate("未评价");
                            }
                        }
                    } else {
                        user.setStaffEvaluate("-");
                    }
                }
            }
        });
        return psu.getList();
    }

    private String lastMonth() {
        Calendar c = Calendar.getInstance();
        return String.valueOf(c.get(Calendar.YEAR)) + String.valueOf(c.get(Calendar.MONTH));
    }

    /**
     * 小数点取舍(不满0.5或满0.5按0.5算,大于0.5按1算)
     *
     * @Param: []
     * @return:
     * @Date:
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

    /**
     * 部门评价时去除导入的部门
     *
     * @Param:
     * @return:
     * @Date: 2019/11/21
     */
    private List<DeptAssRecord> removeImportedDepartment(List<DeptAssRecord> deAssRecordList) {
        return deAssRecordList.stream().filter(e -> {
            String deptId = e.getDeptId();
            Example depExample = new Example(DeptInfo.class);
            depExample.createCriteria().andEqualTo("id", deptId).andEqualTo("needAssessment", "1");
            int i = deptInfoDao.selectCountByExample(depExample);
            if (i == 1) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }
}
