package com.bestvike.standplat.data;

import java.io.Serializable;

public class CooperateRemark implements Serializable {
    private  String juryname;
    private  String cooperate_remark;

    public CooperateRemark() {
    }

    public String getJuryname() {
        return juryname;
    }

    public void setJuryname(String juryname) {
        this.juryname = juryname;
    }

    public String getCooperate_remark() {
        return cooperate_remark;
    }

    public void setCooperate_remark(String cooperate_remark) {
        this.cooperate_remark = cooperate_remark;
    }
}
