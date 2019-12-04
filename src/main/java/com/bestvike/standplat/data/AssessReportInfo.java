package com.bestvike.standplat.data;

import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 部门考核月报表
 *
 * @Param:
 * @return:
 * @Date: 2019/11/4
 */
@Entity
@Component
@Table(name = "dept_assess_report")
public class AssessReportInfo extends BasePageData implements Comparable<AssessReportInfo>,Serializable {
    @Id
    private String id;
    private String depId;
    private String assessMonth;
    private BigDecimal assessScore;
    private BigDecimal initScore;
    private String cooperateRemark;
    private String manageTime;

    @Transient
    private String deptName;


    @Transient
    private String otherScore;

    @Transient
    private String userId;

    public String getOtherScore() {
        return otherScore;
    }

    public void setOtherScore(String otherScores) {
        this.otherScore = otherScores;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getAssessMonth() {
        return assessMonth;
    }

    public void setAssessMonth(String assessMonth) {
        this.assessMonth = assessMonth;
    }


    public void setCooperateRemark(String cooperateRemark) {
        this.cooperateRemark = cooperateRemark;
    }

    public BigDecimal getAssessScore() {
        return assessScore;
    }

    public void setAssessScore(BigDecimal assessScore) {
        this.assessScore = assessScore;
    }

    public BigDecimal getInitScore() {
        return initScore;
    }

    public void setInitScore(BigDecimal initScore) {
        this.initScore = initScore;
    }

    public String getCooperateRemark() {
        return cooperateRemark;
    }

    public String getManageTime() {
        return manageTime;
    }

    public void setManageTime(String manageTime) {
        this.manageTime = manageTime;
    }

    @Override
    //重写Comparable接口的compareTo方法
    public int compareTo(AssessReportInfo ass) {
        // 根据月份降序排列，升序修改相减顺序即可
        return Integer.valueOf(ass.getAssessMonth() )- Integer.valueOf(this.assessMonth);
    }

    @Override
    public String toString() {
        return "AssessReportInfo{" +
                "id='" + id + '\'' +
                ", depId='" + depId + '\'' +
                ", assessMonth='" + assessMonth + '\'' +
                ", assessScore=" + assessScore +
                ", initScore=" + initScore +
                ", cooperateRemark='" + cooperateRemark + '\'' +
                ", manageTime=" + manageTime +
                ", deptName='" + deptName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
