package com.bestvike.standplat.support;

public class AuthorityContext {
    private static ThreadLocal<String> scopeThread = new ThreadLocal<>();
    private static ThreadLocal<String> authoritiesThread = new ThreadLocal<>();

    public static String getScope() {
        return scopeThread.get();
    }
    public static void setScope(String scope) {
        scopeThread.set(scope);
    }
    public static String getAuthorities() {
        return authoritiesThread.get();
    }
    public static void setAuthorities(String authorities) {
        authoritiesThread.set(authorities);
    }
}
