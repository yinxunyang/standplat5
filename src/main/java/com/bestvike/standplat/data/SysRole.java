package com.bestvike.standplat.data;

import com.bestvike.commons.utils.ConvertUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
// @NameStyle(Style.normal)
public class SysRole extends BasePageData implements Serializable {
    private  static  final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private String permissions;
    private Integer sn;
    private String status;
    private Date manageTime;
    private String manageUser;
    private String remark;

    @Transient
    private Map<String, List<String>> routes;
    @Transient
    private String statusName;
    @Transient
    private Integer grantUserCount;

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

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getManageTime() {
        return manageTime;
    }

    public void setManageTime(Date manageTime) {
        this.manageTime = manageTime;
    }

    public String getManageUser() {
        return manageUser;
    }

    public void setManageUser(String manageUser) {
        this.manageUser = manageUser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Map<String, List<String>> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, List<String>> routes) {
        if (routes != null && routes.size() > 0) {
            this.permissions = ConvertUtils.getString(routes);
        }
        this.routes = routes;
    }

    public String getStatusName() {
        if (StringUtils.isEmpty(this.statusName) && !StringUtils.isEmpty(this.status)) {

        }
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
