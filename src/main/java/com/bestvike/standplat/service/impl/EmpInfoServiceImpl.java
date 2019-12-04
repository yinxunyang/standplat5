package com.bestvike.standplat.service.impl;

import com.bestvike.commons.utils.ExcelUtils;
import com.bestvike.commons.utils.FileUtils;
import com.bestvike.commons.utils.StringUtils;
import com.bestvike.standplat.dao.*;
import com.bestvike.standplat.data.*;
import com.bestvike.standplat.service.BaseService;
import com.bestvike.standplat.service.EmpInfoService;

import com.bestvike.standplat.support.ExcelUtil;
import com.bestvike.standplat.support.MybatisUtils;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class EmpInfoServiceImpl extends BaseService implements EmpInfoService {
    @Autowired
    private EmpInfoDao empInfoDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private DeptInfoDao deptInfoDao;
    @Autowired
    private EmpAssessRecordDao empAssessRecordDao;
    @Autowired
    private PostInfoDao postInfoDao;
    @Autowired
    private AssessReportInfoDao assessReportInfoDao;

    private static int total;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    @Value("${app.file.importPath:}")
    private String uploadPath;
    @Value("${app.file.template-path:}")
    private String templatePath;

    /**
     * 查询所有在职员工信息
     *
     * @param
     * @return
     */
    @Override
    public List<EmpInfo> fetchAllEmp() {
        Example example = new Example(EmpInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", "0000");
        return empInfoDao.selectByExample(example);
    }

    /**
     * 新增用户时按照部门筛选员工,去除已开户员工和离职员工
     *
     * @param deptId
     * @return
     */
    @Override
    public List<EmpInfo> fetchEmpNotInUser(String deptId) {
        /*Example example = new Example(EmpInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", "0000");
        if(deptId!=null && !"".equals(deptId)){
            criteria.andEqualTo("deptId",deptId);
        }
        return empInfoDao.selectByExample(example);*/
        return empInfoDao.fetchEmpNotInUser(deptId);
    }

    @Override
    public List<EmpInfo> fetchDeptEmp(EmpInfo empInfo, String userId) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("empId", userId);
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        empInfo.setDeptId(sysUser.getDeptId());
        Example example2 = new Example(DeptInfo.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("id", sysUser.getDeptId());
        DeptInfo deptInfo = deptInfoDao.selectOneByExample(example2);
        if (null == deptInfo) {
            logger.info("部门信息为空");
        }
        if ("0012".equals(sysUser.getRoles())) {
            Example examples = new Example(EmpInfo.class);
            examples.createCriteria().andEqualTo("deptId", sysUser.getDeptId()).andEqualTo("needScore", "1").andNotEqualTo("status", "9999");
            List<EmpInfo> empInfos = empInfoDao.selectByExample(examples);
            List<EmpInfo> collect = empInfos.stream().filter(item -> {
                        Example exampleAss = new Example(EmpAssessRecord.class);
                        exampleAss.createCriteria().andEqualTo("empId", item.getId()).andEqualTo("assessMonth", lastMonth());
                        EmpAssessRecord empAssessRecord = empAssessRecordDao.selectOneByExample(exampleAss);
                        if (!StringUtils.isEmpty(empAssessRecord)) {
                            if ("00".equals(empAssessRecord.getStatus())) {

                                return false;
                            } else {
                                item.setEmpScore(empAssessRecord.getEmpScore());
                                item.setAssessRemark(empAssessRecord.getAssessRemark());
                            }
                        }

                        PostInfo postInfo = postInfoDao.selectByPrimaryKey(item.getPostCode());
                        if (null != postInfo) {
                            List<String> list = Arrays.asList("处长", "三级职员", "协理员");
                            if (!list.contains(postInfo.getName())) {
                                item.setpostName(postInfo.getName());
                                return true;
                            }
                        }
                        return false;
                    }
            ).collect(Collectors.toList());
            return collect;
        }
        List<EmpInfo> empInfos = empInfoDao.fetchDeptEmp(empInfo);
        String state = "99";
        Example example3 = new Example(EmpAssessRecord.class);
        Example.Criteria criteria3 = example3.createCriteria();
        criteria3.andEqualTo("deptId", deptInfo.getId()).andEqualTo("status", "00").andEqualTo("assessMonth", buildOuTTradeNo());
        int count00 = empAssessRecordDao.selectCountByExample(example3);
        if (count00 > 0) {
            state = "66";
        }
        Example example4 = new Example(EmpAssessRecord.class);
        Example.Criteria criteria4 = example4.createCriteria();
        criteria4.andEqualTo("deptId", deptInfo.getId()).andEqualTo("status", "01").andEqualTo("assessMonth", buildOuTTradeNo());
        int count01 = empAssessRecordDao.selectCountByExample(example4);
        if (count01 > 0) {
            state = "22";
        }
        Example example6 = new Example(AssessReportInfo.class);
        example6.createCriteria().andEqualTo("assessMonth", buildOuTTradeNo());
        int i = assessReportInfoDao.selectCountByExample(example6);
        if (i == 0) {
            state = "11";
        }
        String finalState = state;
        empInfos = empInfos.stream().filter((EmpInfo e) -> {
            e.setState(finalState);
            Example examplePost = new Example(PostInfo.class);
            Example.Criteria criteriaPost = examplePost.createCriteria();
            criteriaPost.andEqualTo("code", e.getPostCode());
            PostInfo postInfo = postInfoDao.selectOneByExample(examplePost);
            e.setpostName(postInfo.getName());
            List<String> list = Arrays.asList("处长", "三级职员", "协理员");
            if (list.contains(e.getpostName())) {
                return false;
            }
            /**
             * 是否需要员工评价的状态
             * 99 可以参与评价
             * 66 已完成上月评价
             * 11 部门评价尚未完成 不能进行员工评价
             * 22 参与评价 且有暂存数据
             */
            //根据判断部门等级为1的部门---------当前数据中仅存在2级部门分别是0 和 1  下级部门为1时进行员工评价
            if (null != deptInfo && null != deptInfo.getGrade() && deptInfo.getGrade() == 1) {
                if (finalState.equals("66")) {
                    return false;
                }
                if (finalState.equals("22")) {
                    Example example5 = new Example(EmpAssessRecord.class);
                    Example.Criteria criteria5 = example5.createCriteria();
                    criteria5.andEqualTo("empId", e.getId()).andEqualTo("status", "01").andEqualTo("assessMonth", buildOuTTradeNo());
                    EmpAssessRecord empAssessRecord = empAssessRecordDao.selectOneByExample(example5);
                    e.setEmpScore(empAssessRecord.getEmpScore());
                    e.setAssessRemark(empAssessRecord.getAssessRemark() == null ? "" : empAssessRecord.getAssessRemark());
                }
            } else {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return empInfos;
    }

    //获取上个年月yyyymm
    private String buildOuTTradeNo() {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(1);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMM");
        return formatters.format(today);
    }
    /*@Override
    public PageInfo<EmpInfo> fetch(EmpInfo empInfo) {
        return MybatisUtils.page(empInfo, empInfoDao, new ExampleCriteria() {
            @Override
            public void initCriteria(Example.Criteria criteria) {
                if (!StringUtils.isEmpty(empInfo.getDeptId())) {
                    criteria.andEqualTo("deptId", empInfo.getDeptId());
                }
              *//*  if (!StringUtils.isEmpty(empInfo.getFuzzy())) {
                    criteria.andLike("id", "%" + empInfo.getFuzzy() + "%");
                    criteria.orLike("name", "%" + empInfo.getFuzzy() + "%");
                } else {*//*
                if (!StringUtils.isEmpty(empInfo.getId())) {
                    criteria.andEqualTo("id", empInfo.getId());
                }
                if (!StringUtils.isEmpty(empInfo.getName())) {
                    criteria.andLike("name", "%" + empInfo.getName() + "%");
                }
//                }
            }
        });
    }*/

    @Override
    public List<EmpInfo> fetch(EmpInfo empInfo) {
        PageInfo<EmpInfo> pageInfo = MybatisUtils.page(empInfo, new ISelect() {
            @Override
            public void doSelect() {
                empInfoDao.fetch(empInfo);
            }
        });
        return pageInfo.getList();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int create(EmpInfo empInfo, String user) {
        /*EmpInfo temp = empInfoDao.selectByPrimaryKey(empInfo.getId());
        if (temp != null) {
            throw new ServiceException("该员工编号已存在");
        }*/

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            empInfo.setManageTime(dateFormat.parse(dateFormat.format(date)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        empInfo.setManageUser(user);
        return empInfoDao.insert(empInfo);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int modify(EmpInfo empInfo) {
        String empStatus = empInfo.getStatus();
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("empId", empInfo.getId()).andEqualTo("status", "0000");
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        if (!StringUtils.isEmpty(sysUser)) {
            if (empStatus.equals("9999")) {
                sysUser.setStatus("9999");
            }
            sysUser.setName(empInfo.getName());
            sysUser.setDeptId(empInfo.getDeptId());
            sysUserDao.updateByPrimaryKey(sysUser);
        }

        return empInfoDao.updateByPrimaryKey(empInfo);
    }

    /**
     * 删除员工
     *
     * @param ids
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(String ids) {
       /* Example example = new Example(EmpInfo.class);
        example.createCriteria().andIn("id", Arrays.asList(ids.split(",")));
        return empInfoDao.deleteByExample(example);*/
        String[] empId = ids.split(",");
        for (String id : empId) {
            Example example = new Example(EmpInfo.class);
            example.createCriteria().andEqualTo("id", id);
            EmpInfo empInfo = empInfoDao.selectOneByExample(example);
            empInfo.setStatus("9999");
            empInfoDao.updateByPrimaryKey(empInfo);

            Example sysExample = new Example(SysUser.class);
            sysExample.createCriteria().andEqualTo("empId", id).andEqualTo("status", "0000");
            SysUser sysUser = sysUserDao.selectOneByExample(sysExample);
            if (!StringUtils.isEmpty(sysUser)) {
                sysUser.setStatus("9999");
                sysUserDao.updateByPrimaryKey(sysUser);
            }
        }
    }

    /**
     * 员工编号生成辅助方法
     *
     * @param empInfo
     * @return
     */
    public String getEmpId(EmpInfo empInfo) {
        String empString = empInfoDao.fetchEmpIdMax(empInfo);
        Integer empId;
        if (empString != null && !"".equals(empString)) {
            String empStringTemp = empString.substring(1);
            empId = Integer.parseInt(empStringTemp);
        } else {
            empId = 0;
        }
        empId++;
        String zeroEmpId = String.format("%03d", empId);
        String resultEmpId = empInfo.getDeptId() + zeroEmpId;
        return resultEmpId;
    }

    /**
     * 员工导入功能
     *
     * @param file
     * @param fileSource
     * @param path
     * @param manageUser
     * @return
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, String> importEmpInfo(MultipartFile file, String fileSource, String path, String manageUser) {
        logger.info("上传员工信息文件开始");
        Map<String, String> resultMap = new HashMap<>();

        //上传文件  读取文件内容
        InputStream inputStream = null;
        MultipartFile multipartFile = file;
        String tempName = fileSource;
        String fileName = multipartFile.getOriginalFilename();
        logger.info("upload filename: " + tempName + "-" + fileName);
        if (fileName == null || fileName.trim().equals("")) {
            logger.info("员工信息文件上传文件名为空");
            resultMap.put("retCode", "U1");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "文件上传文件名为空");
            logger.info("importTravel end 员工信息文件上传文件名为空");
            return resultMap;
        }
        try {
            inputStream = multipartFile.getInputStream();
            logger.info("获取上传文件流");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("importTravel end 获取文件流失败");
            resultMap.put("retCode", "U1");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "获取输入流失败");
            return resultMap;
        }
        logger.info("开始进行文件上传");
        byte[] bytes = null;
        try {
            bytes = FileUtils.read(inputStream);
        } catch (Exception e) {
            logger.error("上传文件读取文件流异常", e);
            resultMap.put("retCode", "U1");
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
            Object bytes1 = null;
            resultMap.put("retCode", "U1");
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
            resultMap.put("retCode", "U1");
            resultMap.put("level", "error");
            resultMap.put("retMsg", "上传文件异常");
            logger.info("员工信息文件上传异常", e);
            return resultMap;
        }

        logger.info("员工信息文件上传完成");

        String msg = "";
        Map<String, Object> varMap = new HashMap<String, Object>();
        List<EmpInfo> empInfoList = new ArrayList<>();
        varMap.put("empInfoList", empInfoList);
        try {
            FileInputStream is = new FileInputStream(filePath + upFileName);
            ExcelUtils.importFile(templatePath, "empInfoImport.xml", is, varMap);
        } catch (Exception e) {
            logger.info("员工信息转换失败：" + e);
            resultMap.put("level", "warning");
            resultMap.put("retMsg", "请使用模板导入");
            ExcelUtil.deleteFileDirectory(filePath + upFileName);
            return resultMap;
        }
        if (empInfoList.size() == 0) {
            resultMap.put("retCode", "9999");
            resultMap.put("retMsg", "导入数据为空");
            resultMap.put("level", "warning");
            ExcelUtil.deleteFileDirectory(filePath + upFileName);
            return resultMap;
        }

        try {
            //调用导入验证
            Map<String, Object> checkMap = checkEmpInfoList(empInfoList, manageUser);
            if (checkMap.get("msg").toString().length() != 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                msg = checkMap.get("msg").toString() + ",并重新导入";
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.info("网银流水信息导入失败:" + e);
            resultMap.put("level", "warning");
            resultMap.put("retMsg", "导入失败！");
            return resultMap;
        } finally {
            ExcelUtil.deleteFileDirectory(filePath + upFileName);
        }
        if (msg.length() > 0) {
            resultMap.put("retCode", "9999");
            resultMap.put("level", "warning");
            resultMap.put("retMsg", msg);
        } else {
            resultMap.put("retCode", "0000");
            resultMap.put("level", "success");
            resultMap.put("retMsg", "上传成功");
        }
        return resultMap;
    }

    /**
     * 员工信息导入验证
     *
     * @param list
     * @param user
     * @return
     */
    private Map<String, Object> checkEmpInfoList(List<EmpInfo> list, String user) {
        Map<String, Object> resultMap = new HashMap<>();
        int index = 2;
        String msg = "";

        for (EmpInfo empInfo : list) {
            String deptId = deptInfoDao.getDeptId(empInfo.getDeptName());
            StringBuffer msgbuf = new StringBuffer();
            // 验证部门名称
            if (deptId != null && !"".equals(deptId)) {
                empInfo.setDeptId(deptId);
                String id = getEmpId(empInfo);
                empInfo.setId(id);
            } else {
                msgbuf = getErrorMsg(msgbuf, index, "部门名称不存在 ");
            }
            // 验证员工级别
            boolean grade = false;

            if (grade == false) {
                msgbuf = getErrorMsg(msgbuf, index, "员工级别名称不存在 ");
            }
            /*if(empInfo.getGrade().equals("主任")){
                empInfo.setGrade("0");
            } else if(empInfo.getGrade().equals("副主任") ){
                empInfo.setGrade("1");
            } else if(empInfo.getGrade().equals("处长")){
                empInfo.setGrade("2");
            } else if(empInfo.getGrade().equals("副处长")){
                empInfo.setGrade("3");
            } else if(empInfo.getGrade().equals("员工") ){
                empInfo.setGrade("4");
            } else {
                msgbuf = getErrorMsg(msgbuf,index,"员工级别名称不存在 ");
            }*/
            // 验证岗级名称
            Example example = new Example(PostInfo.class);
            example.createCriteria().andEqualTo("name", empInfo.getpostName());
            PostInfo postInfo = postInfoDao.selectOneByExample(example);
            if (!StringUtils.isEmpty(postInfo)) {
                empInfo.setPostCode(postInfo.getCode());
            } else {
                msgbuf = getErrorMsg(msgbuf, index, "岗级名称不存在 ");
            }
            // 验证员工状态
            boolean status = false;

            if (status == false) {
                msgbuf = getErrorMsg(msgbuf, index, "员工状态不存在 ");
            }
            /*if(empInfo.getStatus().equals("正常") ){
                empInfo.setStatus("0000");
            } else if(empInfo.getStatus().equals("调走") ){
                empInfo.setStatus("1000");
            } else if(empInfo.getStatus().equals("休假") ){
                empInfo.setStatus("9000");
            } else if(empInfo.getStatus().equals("离职") ){
                empInfo.setStatus("9999");
            } else{
                msgbuf = getErrorMsg(msgbuf,index,"员工状态错误，请选择 正常、调走、休假、离职 状态 ");
            }*/
            // 验证是否参与评分
            if (empInfo.getNeedScore().equals("是")) {
                empInfo.setNeedScore("1");
            } else if (empInfo.getNeedScore().equals("否")) {
                empInfo.setNeedScore("0");
            } else {
                msgbuf = getErrorMsg(msgbuf, index, "请选择是否参与员工评分 ");
            }
            // 设置导入时间
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                empInfo.setManageTime(dateFormat.parse(dateFormat.format(date)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 设置导入人
            empInfo.setManageUser(user);
            if (msgbuf.length() == 0) {
                empInfoDao.insert(empInfo);
            } else {
                msg = msg + msgbuf.toString();
            }
            index++;
        }
        resultMap.put("msg", msg);
        return resultMap;
    }

    /**
     * 返回错误行信息
     *
     * @param str
     * @param index
     * @param msgStr
     * @return
     */
    private StringBuffer getErrorMsg(StringBuffer str, int index, String msgStr) {
        if (str == null || str.length() == 0) {
            str.append("第" + index + "行数据错误," + msgStr + "请修改第" + index + "行数据 ");
        } else {
            str.append("," + msgStr);
        }
        return str;
    }

    private String lastMonth() {
        Calendar c = Calendar.getInstance();
        return String.valueOf(c.get(Calendar.YEAR)) + String.valueOf(c.get(Calendar.MONTH));
    }
}
