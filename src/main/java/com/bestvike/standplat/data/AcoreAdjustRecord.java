package com.bestvike.standplat.data;


import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
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
public class AcoreAdjustRecord extends BasePageData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String assessMonth;
    private String deptIds;
    private BigDecimal sysDeptAverage;
    private BigDecimal importDeptAverage;
    private BigDecimal adjustScore;
    private String manageTime;
    private String manageUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssessMonth() {
        return assessMonth;
    }

    public void setAssessMonth(String assessMonth) {
        this.assessMonth = assessMonth;
    }

    public BigDecimal getSysDeptAverage() {
        return sysDeptAverage;
    }

    public void setSysDeptAverage(BigDecimal sysDeptAverage) {
        this.sysDeptAverage = sysDeptAverage;
    }

    public BigDecimal getImportDeptAverage() {
        return importDeptAverage;
    }

    public void setImportDeptAverage(BigDecimal importDeptAverage) {
        this.importDeptAverage = importDeptAverage;
    }

    public BigDecimal getAdjustScore() {
        return adjustScore;
    }

    public void setAdjustScore(BigDecimal adjustScore) {
        this.adjustScore = adjustScore;
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

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    @Override
    public String toString() {
        return "AcoreAdjustRecord{" +
                "id='" + id + '\'' +
                ", assessMonth='" + assessMonth + '\'' +
                ", deptIds='" + deptIds + '\'' +
                ", sysDeptAverage=" + sysDeptAverage +
                ", importDeptAverage=" + importDeptAverage +
                ", adjustScore=" + adjustScore +
                ", manageTime=" + manageTime +
                ", manageUser='" + manageUser + '\'' +
                '}';
    }
}
