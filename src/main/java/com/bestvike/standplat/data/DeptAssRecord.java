package com.bestvike.standplat.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
* 部门考核记录
* @Param:
* @return:
* @Date: 2019/11/4
*/
@Entity
@Table(name="dept_assess_record")
public class DeptAssRecord extends BasePageData implements Serializable{
    @Id
    private String id;
    private String deptId;
    private String assessMonth;
    private String taskLevel;
    private String taskPerScore;
    private BigDecimal cooperateScore;
    private String cooperateRemark;
    private String juryId;
    private String source;
    private String manageTime;
    private String manageUser;

    @Transient
    private BigDecimal assessScore;
    @Transient
    private String depName;
    @Transient
    private String jurName;
    @Transient
    private String tmpCooperateScore;

    public String getTmpCooperateScore() {
        return tmpCooperateScore;
    }

    public void setTmpCooperateScore(String tmpCooperateScore) {
        this.tmpCooperateScore = tmpCooperateScore;
    }

    public String getManageTime() {
        return manageTime;
    }

    public void setManageTime(String manageTime) {
        this.manageTime = manageTime;
    }
    public String getJurName() {
        return jurName;
    }

    public void setJurName(String jurName) {
        this.jurName = jurName;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public BigDecimal getAssessScore() {
        return assessScore;
    }

    public void setAssessScore(BigDecimal assessScore) {
        this.assessScore = assessScore;
    }

    public DeptAssRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getAssessMonth() {
        return assessMonth;
    }

    public void setAssessMonth(String assessMonth) {
        this.assessMonth = assessMonth;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }

    public String getTaskPerScore() {
        return taskPerScore;
    }

    public void setTaskPerScore(String taskPerScore) {
        this.taskPerScore = taskPerScore;
    }

    public BigDecimal getCooperateScore() {
        return cooperateScore;
    }

    public void setCooperateScore(BigDecimal cooperateScore) {
        this.cooperateScore = cooperateScore;
    }

    public String getCooperateRemark() {
        return cooperateRemark;
    }

    public void setCooperateRemark(String cooperateRemark) {
        this.cooperateRemark = cooperateRemark;
    }

    public String getJuryId() {
        return juryId;
    }

    public void setJuryId(String juryId) {
        this.juryId = juryId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getManageUser() {
        return manageUser;
    }

    public void setManageUser(String manageUser) {
        this.manageUser = manageUser;
    }

    @Override
    public String toString() {
        return "DeptAssessRecord{" +
                "id='" + id + '\'' +
                ", deptId='" + deptId + '\'' +
                ", assessMonth='" + assessMonth + '\'' +
                ", taskLevel='" + taskLevel + '\'' +
                ", taskPerScore='" + taskPerScore + '\'' +
                ", cooperateScore=" + cooperateScore +
                ", cooperateRemark='" + cooperateRemark + '\'' +
                ", juryId='" + juryId + '\'' +
                ", source='" + source + '\'' +
                ", manageTime=" + manageTime +
                ", manageUser='" + manageUser + '\'' +
                '}';
    }
}
