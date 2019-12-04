package com.bestvike.standplat;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.bestvike.standplat")
@ServletComponentScan(basePackages = {"com.bestvike.standplat"})
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

  /*  @Value("${app.redis.prefix}")
    private String redisPrefix;
    @Value("${app.redis.authority-prefix}")
    private String redisAuthorityPrefix;
    @Autowired
    private RedisTemplate redisTemplate;
    @Bean(name = "cache")
    public Cache cache() {
        return new Cache(redisPrefix, redisTemplate);
    }
    @Bean(name = "authorityCache")
    public Cache authorityCache() {
        return new Cache(redisAuthorityPrefix, redisTemplate);
    }*/
}
