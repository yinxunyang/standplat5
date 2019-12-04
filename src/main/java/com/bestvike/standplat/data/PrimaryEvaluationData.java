package com.bestvike.standplat.data;
/**
*初级评委评价记录(接收前端数据)
* @Param:
* @return:
* @Date: 2019/11/7
*/
public class PrimaryEvaluationData {
    //部门
    private String department;
    //工作完成情况
    private String jobWork;
    //协同评价得分
    private String initialScore;
    //原因
    private String causeInitialValue;

    public PrimaryEvaluationData() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobWork() {
        return jobWork;
    }

    public void setJobWork(String jobWork) {
        this.jobWork = jobWork;
    }

    public String getInitialScore() {
        return initialScore;
    }

    public void setInitialScore(String initialScore) {
        this.initialScore = initialScore;
    }

    public String getCauseInitialValue() {
        return causeInitialValue;
    }

    public void setCauseInitialValue(String causeInitialValue) {
        this.causeInitialValue = causeInitialValue;
    }
}
