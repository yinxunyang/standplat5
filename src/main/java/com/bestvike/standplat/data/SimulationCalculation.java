package com.bestvike.standplat.data;

import java.io.Serializable;

/**
* 终极评委测算 (给前端组报文)
* @Param:
* @return:
* @Date: 2019/11/7
*/
public class SimulationCalculation implements Serializable {
    //部门:department  当前得分:currentScore  优秀数量:excellentQuantity  良好数量:goodQuantity  合格数量:qualifiedQuantity  不合格数量:unqualifiedQuantity  扣分合计:deductionOfPoints
    private String department;
    private String currentScore ;
    private String excellentQuantity;
    private String goodQuantity;
    private String qualifiedQuantity;
    private String unqualifiedQuantity;
    private String deductionOfPoints;

    public SimulationCalculation() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }

    public String getExcellentQuantity() {
        return excellentQuantity;
    }

    public void setExcellentQuantity(String excellentQuantity) {
        this.excellentQuantity = excellentQuantity;
    }

    public String getGoodQuantity() {
        return goodQuantity;
    }

    public void setGoodQuantity(String goodQuantity) {
        this.goodQuantity = goodQuantity;
    }

    public String getQualifiedQuantity() {
        return qualifiedQuantity;
    }

    public void setQualifiedQuantity(String qualifiedQuantity) {
        this.qualifiedQuantity = qualifiedQuantity;
    }

    public String getUnqualifiedQuantity() {
        return unqualifiedQuantity;
    }

    public void setUnqualifiedQuantity(String unqualifiedQuantity) {
        this.unqualifiedQuantity = unqualifiedQuantity;
    }

    public String getDeductionOfPoints() {
        return deductionOfPoints;
    }

    public void setDeductionOfPoints(String deductionOfPoints) {
        this.deductionOfPoints = deductionOfPoints;
    }
}
