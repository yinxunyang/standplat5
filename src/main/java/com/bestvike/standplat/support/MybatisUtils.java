package com.bestvike.standplat.support;

import com.bestvike.standplat.data.BasePageData;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Set;

public final class MybatisUtils {
    public static <T> PageInfo<T> page(BasePageData baseData, Mapper<T> mapper, ExampleCriteria exampleCriteria) {
        return PageHelper.startPage(baseData.getPage(), baseData.getLimit()).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                Example example = new Example(baseData.getClass());
                Example.Criteria criteria = example.createCriteria();
                exampleCriteria.initCriteria(criteria);
                if (!StringUtils.isEmpty(baseData.getSort())) {
                    String[] sorts = baseData.getSort().split(",");
                    String sortProp = null;
                    boolean sortDesc = false;
                    if (sorts.length > 0) {
                        sortProp = sorts[0];
                        if (sorts.length > 1) {
                            sortDesc = sorts[1].equals("descending");
                        }
                        if (!sortDesc) {
                            example.orderBy(sortProp);
                        } else {
                            example.orderBy(sortProp).desc();
                        }
                    }
                }
                mapper.selectByExample(example);
            }
        });
    }
    public static <T> PageInfo<T> page(BasePageData baseData, ISelect iSelect) {
        String sort = null;
        if (!StringUtils.isEmpty(baseData.getSort())) {
            String[] sorts = baseData.getSort().split(",");
            String sortProp = null;
            boolean sortDesc = false;

            if (sorts.length > 0) {
                sortProp = sorts[0];
                if (sorts.length > 1) {
                    sortDesc = sorts[1].equals("descending");
                }
                NameStyle nameStyle = baseData.getClass().getAnnotation(NameStyle.class);
                if (nameStyle == null || nameStyle.value().equals(Style.camelhump)) {
                    sortProp = StringUtil.camelhumpToUnderline(sortProp);
                }
                if (sort == null) {
                    sort = sortProp;
                } else {
                    sort += ", " + sortProp;
                }
                if (sortDesc) {
                    sort += " desc";
                }
                Set<EntityColumn> entityColumnSet = EntityHelper.getPKColumns(baseData.getClass());
                if (entityColumnSet != null) {
                    for (EntityColumn entityColumn : entityColumnSet) {
                        if (sortProp.equalsIgnoreCase(entityColumn.getColumn())) {
                            continue;
                        }
                        sort += ", " + entityColumn.getColumn();
                    }
                }
            }
        }
        return PageHelper.startPage(baseData.getPage(), baseData.getLimit(), sort).doSelectPageInfo(iSelect);
    }
}
