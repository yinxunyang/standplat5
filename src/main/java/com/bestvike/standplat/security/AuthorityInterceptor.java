package com.bestvike.standplat.security;

import com.bestvike.commons.exception.AuthorityException;

import com.bestvike.commons.utils.HttpUtils;
import com.bestvike.standplat.support.Annotation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * authentication aop class.
 * 鉴权拦截器，处理过程如下：
 * 读取resource/authority.json权限配置文件，拦截所有的RequestMapping，校验用户是否有接口请求权限
 *
 * @author Liu qingxiang
 * @since v1.0.0
 */
@Aspect
@Component
public class AuthorityInterceptor {
    public Log logger = LogFactory.getLog(this.getClass());



    @Value("${app.authority.check-url}")
    private Boolean checkUrl;

    @Pointcut("execution(* com.bestvike..*.*(..)) && "
            + "(  @annotation(org.springframework.web.bind.annotation.PostMapping)"
            + "|| @annotation(org.springframework.web.bind.annotation.GetMapping)"
            + "|| @annotation(org.springframework.web.bind.annotation.PutMapping)"
            + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    private void doRequestPointcut() {

    }

    @Before(value = "doRequestPointcut()")
    public void doAuth(JoinPoint joinPoint) throws NoSuchMethodException {

        Class[] requestTypeArray = { GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class };
        List<Class> requestTypeList = Arrays.asList(requestTypeArray);
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        java.lang.annotation.Annotation[] annotations = method.getAnnotations();
        Optional<java.lang.annotation.Annotation> find = Arrays.stream(annotations)
                .filter(annotation -> requestTypeList.contains(annotation.annotationType())).findFirst();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (!checkUrl || !find.isPresent())
            return;
        java.lang.annotation.Annotation typeAnnotation = find.get();
        Class cl = typeAnnotation.getClass();
        Method method1 = cl.getMethod("value");
        try {
            // 兼容类注解RequestMapping的情况
            RequestMapping requestMapping = joinPoint.getTarget().getClass().getAnnotation(RequestMapping.class);
            String value = "";
            if (requestMapping != null)
                value = requestMapping.value()[0];
            String[] methodValue = (String[]) method1.invoke(typeAnnotation);
            if (methodValue != null) {
                value += methodValue[0];
            }
            // 请求路径，避免双斜杠可能性

            value.replaceAll("//", "/");
            // 获取请求类型
            String requestType = typeAnnotation.annotationType().getTypeName();
            if (StringUtils.isEmpty(requestType)) {
                // 异常
                logger.error("没有请求类型");
                throw new AuthorityException("鉴权失败");
            }
            // 鉴权
            String methodName = Annotation.getMethod(requestType);


        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (InvocationTargetException e) {
            logger.error(e);
        }

    }

    public boolean isAuth(String userId, String method, String url) {
        if (!checkUrl) {
            return true;
        }
        HashSet<String> userPermissions = null;
        if (userPermissions == null) {
            return false;
        }
        return userPermissions.contains(method + ":" + url) || userPermissions.contains(url);
    }
}
