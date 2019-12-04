package com.bestvike.standplat.data;

import java.io.Serializable;

/**
 * 二级评委存表
 * @Param:
 * @return:
 * @Date: 2019/11/4
 */
public class DepartmentAndscore implements Serializable {
    //部门名称
    private String department;
  //任务完成情况得分
    private String initialScore;

    public DepartmentAndscore() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInitialScore() {
        return initialScore;
    }

    public void setInitialScore(String initialScore) {
        this.initialScore = initialScore;
    }
}
