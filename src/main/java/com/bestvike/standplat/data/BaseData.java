package com.bestvike.standplat.data;

import com.bestvike.commons.utils.StringUtils;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.Transient;
import java.io.Serializable;

@NameStyle(Style.camelhump)
public abstract class BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Transient
    protected String fuzzy;
    @Transient
    protected String fuzzyLike;
    @Transient
    protected String sort;

    public String getFuzzy() {
        return fuzzy;
    }

    public void setFuzzy(String fuzzy) {
        this.fuzzy = fuzzy;
    }

    public String getFuzzyLike() {
        if (StringUtils.isEmpty(fuzzyLike) && !StringUtils.isEmpty(fuzzy)) {
            return "%" + fuzzy + "%";
        }
        return fuzzyLike;
    }

    public void setFuzzyLike(String fuzzyLike) {
        this.fuzzyLike = fuzzyLike;
    }

    public String getSort() {
        return this.sort;
        /*String sortProp = null;
        boolean sortDesc = false;
        if (!StringUtils.isEmpty(this.sort)) {
            String[] sorts = this.sort.split(",");
            if (sorts.length > 0) {
                sortProp = sorts[0];
                if (sorts.length > 1) {
                    sortDesc = sorts[1].equals("descending");
                }
                if (!sortDesc) {
                    sortProp = sortProp + " desc";
                } else {
                    sortProp = sortProp + " asc";
                }
            } else {
                sortProp = "";
            }
            return sortProp;
        }
        return sort;*/
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Sort getFormattedSort() {
        if (!StringUtils.isEmpty(this.sort)) {
            String[] sorts = this.sort.split(",");
            if (sorts.length > 0) {
                Sort sort = new Sort();
                sort.setProp(sorts[0]);
                if (sorts.length > 1) {
                    sort.setDescending(sorts[1].equals("descending"));
                }
                return sort;
            }
        }
        return null;
    }

    public static class Sort implements Serializable {
        private static final long serialVersionUID = 1L;

        private String prop;
        private Boolean isDescending;

        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }

        public Boolean isDescending() {
            return isDescending;
        }

        public void setDescending(Boolean descending) {
            isDescending = descending;
        }
    }
}
