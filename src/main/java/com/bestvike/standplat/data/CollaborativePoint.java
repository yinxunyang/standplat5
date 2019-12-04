package com.bestvike.standplat.data;

import java.io.Serializable;

public class CollaborativePoint implements Serializable {
    private String department;
    private String initialScore;
    private String causeInitialValue;

    public CollaborativePoint() {
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

    public String getCauseInitialValue() {
        return causeInitialValue;
    }

    public void setCauseInitialValue(String causeInitialValue) {
        this.causeInitialValue = causeInitialValue;
    }
}
