package com.bestvike.standplat.data;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author ${author}
 * @since 2019-11-05
 */
@Entity
@Table(name = "emp_assess_record")
public class EmpAssessRecord extends BasePageData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String empId;
    private String assessMonth;
    private BigDecimal empScore;
    private String assessRemark;
    private String recordTime;
    private String juryId;
    private String source;
    private String status;
    private String manageTime;
    private String manageUser;


    private String deptId;
    private BigDecimal rewardScore;
    private BigDecimal score;

    public BigDecimal getRewardScore() {
        return rewardScore;
    }

    public void setRewardScore(BigDecimal rewardScore) {
        this.rewardScore = rewardScore;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    @Transient
    private String deptName;
    @Transient
    private String gradeName;
    @Transient
    private String empName;
    @Transient
    private String postName;
    @Transient
    private String jurName;
    @Transient
    private String tmpEmpScore;
    @Transient
    private String formattedSort;

    public String getTmpEmpScore() {
        return tmpEmpScore;
    }

    public void setTmpEmpScore(String tmpEmpScore) {
        this.tmpEmpScore = tmpEmpScore;
    }

    public String getJurName() {
        return jurName;
    }

    public void setJurName(String jurName) {
        this.jurName = jurName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getAssessMonth() {
        return assessMonth;
    }

    public void setAssessMonth(String assessMonth) {
        this.assessMonth = assessMonth;
    }

    public BigDecimal getEmpScore() {
        return empScore;
    }

    public void setEmpScore(BigDecimal empScore) {
        this.empScore = empScore;
    }

    public String getAssessRemark() {
        return assessRemark;
    }

    public void setAssessRemark(String assessRemark) {
        this.assessRemark = assessRemark;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManageTime() {
        return manageTime;
    }

    public void setManageTime(String manageTime) {
        this.manageTime = manageTime;
    }

    public String getManageUser() {
        return manageUser;
    }

    public void setManageUser(String manageUser) {
        this.manageUser = manageUser;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public void setFormattedSort(String formattedSort) {
        this.formattedSort = formattedSort;
    }


    @Override
    public String toString() {
        return "EmpAssessRecord{" +
                "id='" + id + '\'' +
                ", empId='" + empId + '\'' +
                ", assessMonth='" + assessMonth + '\'' +
                ", empScore=" + empScore +
                ", assessRemark='" + assessRemark + '\'' +
                ", recordTime='" + recordTime + '\'' +
                ", juryId='" + juryId + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", manageTime='" + manageTime + '\'' +
                ", manageUser='" + manageUser + '\'' +
                ", deptId='" + deptId + '\'' +
                ", rewardScore=" + rewardScore +
                ", score=" + score +
                ", deptName='" + deptName + '\'' +
                ", gradeName='" + gradeName + '\'' +
                ", empName='" + empName + '\'' +
                ", postName='" + postName + '\'' +
                ", jurName='" + jurName + '\'' +
                ", tmpEmpScore='" + tmpEmpScore + '\'' +
                '}';
    }
}
