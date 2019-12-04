package com.bestvike.standplat.support;

public enum Annotation {
    GET("org.springframework.web.bind.annotation.GetMapping", "GET"),
    POST("org.springframework.web.bind.annotation.PostMapping", "POST"),
    PUT("org.springframework.web.bind.annotation.PutMapping", "PUT"),
    DELETE("org.springframework.web.bind.annotation.DeleteMapping", "DELETE");

    Annotation(String name, String method) {
        this.name = name;
        this.method = method;
    }

    private String name;
    private String method;

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public static String getMethod(String name) {
        for (Annotation annotation : Annotation.values()) {
            if (annotation.getName().equals(name)) {
                return annotation.getMethod();
            }
        }
        return null;
    }
}
