package com.bestvike.standplat.data;

import javax.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;

/**
* 员工评价修改日志表
* @Param:
* @return:
* @Date: 2019/11/22
*/
@Entity
public class EmpAssessLog implements Serializable {
    private String id;
    private String empAssessId;
    private BigDecimal preEmpScore;
    private BigDecimal empScore;
    private String manageTime;

    public EmpAssessLog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpAssessId() {
        return empAssessId;
    }

    public void setEmpAssessId(String empAssessId) {
        this.empAssessId = empAssessId;
    }

    public BigDecimal getPreEmpScore() {
        return preEmpScore;
    }

    public void setPreEmpScore(BigDecimal preEmpScore) {
        this.preEmpScore = preEmpScore;
    }

    public BigDecimal getEmpScore() {
        return empScore;
    }

    public void setEmpScore(BigDecimal empScore) {
        this.empScore = empScore;
    }

    public String getManageTime() {
        return manageTime;
    }

    public void setManageTime(String manageTime) {
        this.manageTime = manageTime;
    }
}
