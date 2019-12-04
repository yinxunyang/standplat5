package com.bestvike.standplat.data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Arc_BuildInfo")
public class ArcBuildInfo implements Serializable {
	private static final long serialVersionUID = -6187201767513020089L;

	private String bldname;

	public String getBldname() {
		return bldname;
	}

	public void setBldname(String bldname) {
		this.bldname = bldname;
	}
}
