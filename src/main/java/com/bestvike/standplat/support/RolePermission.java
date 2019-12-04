package com.bestvike.standplat.support;

import java.io.Serializable;
import java.util.HashSet;

public class RolePermission implements Serializable {
    private static final long serialVersionUID = 1L;

    private String route;
    private HashSet<String> operates;
    private HashSet<String> urls;

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public HashSet<String> getOperates() {
        return operates;
    }

    public void setOperates(HashSet<String> operates) {
        this.operates = operates;
    }

    public HashSet<String> getUrls() {
        return urls;
    }

    public void setUrls(HashSet<String> urls) {
        this.urls = urls;
    }
}
