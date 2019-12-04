package com.bestvike.standplat.data;

import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.Transient;
import java.io.Serializable;

@NameStyle(Style.camelhump)
public abstract class BasePageData extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Transient
    protected int page;
    @Transient
    protected int limit;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
