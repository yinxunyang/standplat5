/*
package com.bestvike.standplat.config;

import com.bestvike.commons.authority.AuthorityConfig;
import com.bestvike.commons.entity.Route;

import com.bestvike.commons.utils.ConvertUtils;
import com.bestvike.commons.utils.DateUtils;
import com.bestvike.standplat.data.DeptInfo;
import com.bestvike.standplat.data.SysDict;
import com.bestvike.standplat.data.SysRole;
import com.bestvike.standplat.service.DeptInfoService;
import com.bestvike.standplat.service.SysDictService;
import com.bestvike.standplat.service.SysRoleService;
import com.bestvike.standplat.support.CacheUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebListener
public class InitListener implements ServletContextListener, ApplicationContextAware {
    protected Log logger = LogFactory.getLog(this.getClass());

*/
/*    @Autowired
    private Cache cache;*//*
*/
/**//*

    */
/*@Autowired*//*

*/
/*    @Qualifier("authorityCache")
    private Cache authorityCache;*//*

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private DeptInfoService deptInfoService;

    @Value("${app.authority.clear-config:true}")
    private Boolean clearConfig;
    @Value("${app.authority.clear-role-permissions:false}")
    private Boolean clearRolePermissions;
    @Value("classpath:authority.json")
    private Resource resource;
    @Value("${app.instance.code}")
    private String appCode;
    @Value("classpath:routes.json")
    private Resource routesResource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext()).getAutowireCapableBeanFactory().autowireBean(this);

        if (clearConfig || !authorityCache.exists("authority_config:" + appCode + ":__timestamp__")) {
            // 读取鉴权配置 authority.json
            try {
                authorityCache.set("authority_config:" + appCode + ":__timestamp__", DateUtils.getDateTime());
                AuthorityConfig authorityConfig = ConvertUtils.getBean(resource.getInputStream(), AuthorityConfig.class);
                if (authorityConfig != null) {
                    List<String> whites = authorityConfig.getWhites();
                    if (whites != null && whites.size() > 0) {
                        authorityCache.add("authority_config:" + appCode + ":whites", whites);
                    }
                    */
/*Map<String, List<String>> routes = authorityConfig.getRoutes();
                    routes.forEach((key, value) -> {
                        authorityCache.add("authority_config:" + appCode + ":routes:" + key, value);
                    });*//*

                }
            } catch (IOException e) {
                logger.error(e);
            }
        }

        if (clearConfig || !authorityCache.exists("routes:" + appCode + ":__timestamp__")) {
            // 读取鉴权配置 authority.json
            try {
                authorityCache.set("routes:" + appCode + ":__timestamp__", DateUtils.getDateTime());
                List<Route> routes = ConvertUtils.getBeanList(routesResource.getInputStream(), Route.class);
                authorityCache.delete("routes:" + appCode + ":async_routes");
                if (routes != null && routes.size() > 0) {
                    // 路由增加id
                    authorityCache.set("routes:" + appCode + ":async_routes", initRoutes(routes, null));
                    // authorityCache.add("routes:" + appCode + ":async_routes", initRoutes(routes, null));
                }
            } catch (IOException e) {
                logger.error(e);
            }
        }

        if (clearRolePermissions || !authorityCache.exists("role_permissions:__timestamp__")) {
            authorityCache.set("role_permissions:__timestamp__", DateUtils.getDateTime());
            List<SysRole> sysRoleList = sysRoleService.fetchAllPermissions();
            if (sysRoleList != null && sysRoleList.size() > 0) {
                for (SysRole sysRole : sysRoleList) {
                    String permissions = sysRole.getPermissions();
                    authorityCache.delete("role_permissions:" + sysRole.getId());
                    if (!StringUtils.isEmpty(permissions)) {
                        authorityCache.putAll("role_permissions:" + sysRole.getId(), ConvertUtils.getBean(permissions, Map.class));
                    }
                }
            }
        }

        // 缓存部门
        if (cache.get("depts:__timestamp__") == null) {
            // TODO: 完善缓存写入
            cache.set("depts:__timestamp__", DateUtils.getDateTime());
            List<DeptInfo> deptInfoList = deptInfoService.fetchAll();
            if (deptInfoList != null && deptInfoList.size() > 0) {
                for (DeptInfo deptInfo : deptInfoList) {
                    CacheUtils.createDept(deptInfo);
                    // cache.put("depts", deptInfo.getId(), deptInfo.getName());
                }
            }
        }

        // 读取字典配置
        if (cache.get("dicts:__timestamp__") == null) {
            cache.set("dicts:__timestamp__", DateUtils.getDateTime());
            List<SysDict> sysDictList = sysDictService.fetchAll();
            if (sysDictList != null && sysDictList.size() > 0) {
                for (SysDict sysDict : sysDictList) {
                    CacheUtils.createDict(sysDict);
                    */
/*cache.set("dicts:list:" + sysDict.getCode(), sysDict.getChildren());

                    List<SysDict> children = sysDict.getChildren();
                    if (children != null && children.size() > 0) {
                        for (SysDict child : children) {
                            cache.put("dicts:hash:" + sysDict.getCode(), child.getCode(), child.getName());
                        }
                    }*//*

                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CacheUtils.init(applicationContext);
    }

    private List<Route> initRoutes(List<Route> routes, String parentPath) {
        if (routes == null || routes.size() == 0) {
            return routes;
        }
        List<Route> results = new ArrayList<>();
        for (Route route : routes) {
            route.setId(!StringUtils.isEmpty(parentPath) ? parentPath + "/" + route.getPath() : route.getPath());
            if (route.getAuthority() != null && route.getAuthority().getUrls() != null) {
                route.getAuthority().setUrls(null);
            }
            if (route.getAuthority() != null && route.getAuthority().getOperates() != null && route.getAuthority().getOperates().size() > 0) {
                if (route.getMeta() == null) {
                    route.setMeta(new Route.Meta());
                }
                route.getMeta().setOperates(new ArrayList<>());
            }
            if (route.getChildren() != null && route.getChildren().size() > 0) {
                route.setChildren(initRoutes(route.getChildren(), !StringUtils.isEmpty(parentPath) ? parentPath + "/" + route.getPath() : route.getPath()));
            }
            results.add(route);
        }
        return results;
    }
}
*/
