package com.bestvike.standplat.data;

import com.bestvike.commons.entity.Route;
import com.bestvike.commons.utils.ConvertUtils;
import com.bestvike.commons.utils.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
public class SysUser extends BasePageData implements Serializable {
    private  static  final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private String deptId;
    private String empId;
    private String password;
    private String avatar;
    private String email;
    private String mobile;
    private String status;
    private String roles;
    private String settings;
    private Date registerTime;
    private String remark;

    @Transient
    private String deptName;
    @Transient
    private String passtoken;
    @Transient
    private String newPassword;
    @Transient
    private String validateCode;
    // password - 用户名、密码 passtoken - 用户名、令牌密码
    @Transient
    private String loginType;
    @Transient
    private String token;
    @Transient
    private String refreshToken;
    @Transient
    private String departmentEvaluate;
    @Transient
    private String staffEvaluate;
    @Transient
    private List<String> grants;
    @Transient
    private List<Route> routes;
    @Transient
    private Map<String, Object> settingsMap;
    @Transient
    private List<String> ids;

    public String getDepartmentEvaluate() {
        return departmentEvaluate;
    }

    public void setDepartmentEvaluate(String departmentEvaluate) {
        this.departmentEvaluate = departmentEvaluate;
    }

    public String getStaffEvaluate() {
        return staffEvaluate;
    }

    public void setStaffEvaluate(String staffEvaluate) {
        this.staffEvaluate = staffEvaluate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getSettings() {
        if (StringUtils.isEmpty(settings) && settingsMap != null) {
            settings = ConvertUtils.getString(settingsMap);
        }
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeptName() {

        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPasstoken() {
        return passtoken;
    }

    public void setPasstoken(String passtoken) {
        this.passtoken = passtoken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<String> getGrants() {
        if (grants == null && !StringUtils.isEmpty(roles)) {
            return Arrays.asList(roles.split(","));
        }
        return grants;
    }

    public void setGrants(List<String> grants) {
        if (grants != null) {
            this.roles = String.join(",", grants);
        }
        this.grants = grants;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public Map<String, Object> getSettingsMap() {
        if (settingsMap == null && !StringUtils.isEmpty(settings)) {
            return ConvertUtils.getBean(settings, Map.class);
        }
        return settingsMap;
    }

    public void setSettingsMap(Map<String, Object> settingsMap) {
        this.settingsMap = settingsMap;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
