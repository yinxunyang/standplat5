package com.bestvike.standplat.data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
// @NameStyle(Style.normal)
public class ViewRole extends SysRole implements Serializable {
    private  static  final long serialVersionUID = 1L;

    @Transient
    private Integer grantUserCount;

    public Integer getGrantUserCount() {
        return grantUserCount;
    }

    public void setGrantUserCount(Integer grantUserCount) {
        this.grantUserCount = grantUserCount;
    }
}
