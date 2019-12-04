package com.bestvike.standplat.data;

import com.bestvike.commons.utils.ConvertUtils;
import com.bestvike.commons.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class SysDict implements Serializable {
    private  static  final long serialVersionUID = 1L;

    @Id
    private String id;
    private String code;
    private String name;
    private Integer level;
    @JsonIgnore
    private String detail;
    private Integer sn;
    @JsonIgnore
    private Date manageTime;
    @JsonIgnore
    private String manageUser;
    private String remark;

    @Transient
    private List<SysDict> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
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

    public List<SysDict> getChildren() {
        if (children == null && !StringUtils.isEmpty(detail)) {
            children = ConvertUtils.getBeanList(detail, SysDict.class);
        }
        return children;
    }

    public void setChildren(List<SysDict> children) {
        if (children != null && StringUtils.isEmpty(detail)) {
            this.detail = ConvertUtils.getString(children);
        }
        this.children = children;
    }
}
