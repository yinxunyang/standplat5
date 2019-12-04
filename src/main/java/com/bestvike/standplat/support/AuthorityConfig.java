package com.bestvike.standplat.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AuthorityConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> whiteList;
    private Map<String, List<String>> routes;

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public Map<String, List<String>> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, List<String>> routes) {
        this.routes = routes;
    }
}

