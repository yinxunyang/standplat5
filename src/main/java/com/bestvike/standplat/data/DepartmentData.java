package com.bestvike.standplat.data;

import javax.persistence.Entity;
import java.io.Serializable;

/**
* 给前端组装报文用
* @Param:
* @return:
* @Date: 2019/11/4
*/
@Entity
public class DepartmentData implements Serializable {
    private String department;
    public DepartmentData() {
    }
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "departmentData{" +
                "department='" + department + '\'' +
                '}';
    }
}
