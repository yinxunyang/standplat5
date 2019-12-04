package com.bestvike.standplat.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class DeptInfo extends BasePageData implements Serializable {
    private  static  final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private Integer grade;
    private String leader;
    private String status;
    private String needAssessment;
    private String parent;
    private Date manageTime;
    private String manageUser;
    private String remark;

    @Transient
    private List<DeptInfo> children;

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

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
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

    public List<DeptInfo> getChildren() {
        return children;
    }

    public void setChildren(List<DeptInfo> children) {
        this.children = children;
    }

    public String getNeedAssessment() {
        return needAssessment;
    }

    public void setNeedAssessment(String needAssessment) {
        this.needAssessment = needAssessment;
    }

    public DeptInfo() {
    }

    public DeptInfo(String id, String name, Integer grade, String leader, String status, String needAssessment, String parent, Date manageTime, String manageUser, String remark, List<DeptInfo> children) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.leader = leader;
        this.status = status;
        this.needAssessment = needAssessment;
        this.parent = parent;
        this.manageTime = manageTime;
        this.manageUser = manageUser;
        this.remark = remark;
        this.children = children;
    }
}
