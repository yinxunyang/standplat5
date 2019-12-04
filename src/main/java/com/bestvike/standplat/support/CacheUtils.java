package com.bestvike.standplat.support;


import com.bestvike.standplat.data.DeptInfo;
import com.bestvike.standplat.data.SysDict;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.List;

public class CacheUtils {
    private static ApplicationContext applicationContext;


    /////////////////////////////////////// 字典 ////////////////////////////////////////////////////////////
    public static void createDict(SysDict sysDict) {
        List<SysDict> children = sysDict.getChildren();
        if (children != null && children.size() > 0) {
            for (SysDict child : children) {

            }
        }
    }
    public static void modifyDict(SysDict sysDict) {

        createDict(sysDict);
        /*cache.delete("dicts:list:" + sysDict.getCode());
        cache.set("dicts:list:" + sysDict.getCode(), sysDict.getChildren());

        cache.delete("dicts:hash:" + sysDict.getCode());
        List<SysDict> children = sysDict.getChildren();
        if (children != null && children.size() > 0) {
            for (SysDict child : children) {
                cache.put("dicts:hash:" + sysDict.getCode(), child.getCode(), child.getName());
            }
        }*/
    }
    public static void removeDict(List<String> list) {
    }


    /////////////////////////////////////// 部门 ////////////////////////////////////////////////////////////
    public static void createDept(DeptInfo deptInfo){
    }
    public static void modifyDept(DeptInfo dept) {
        removeDept(dept.getId());
        createDept(dept);
    }
    public static void removeDept(List<String> list) {
        for (String id : list) {
            removeDept(id);
        }
    }
    public static void removeDept(String id) {

    }

    public static String transDept(String id) {

            return id;

    }

    public static void init(ApplicationContext applicationContext) {
        CacheUtils.applicationContext = applicationContext;

    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
