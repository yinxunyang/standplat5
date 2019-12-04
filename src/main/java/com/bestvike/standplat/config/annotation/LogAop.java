package com.bestvike.standplat.config.annotation;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.javassist.ClassClassPath;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.Modifier;
import org.apache.ibatis.javassist.NotFoundException;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname LogAop
 * @Description 日志打印AOP
 * @Date 2019/11/6 11:42
 * @Created by yl
 */

@Aspect
@Component
public class LogAop {
    protected Log log = LogFactory.getLog(this.getClass());
    @Pointcut("@annotation(com.bestvike.standplat.config.annotation.LogPub)")
    public void logPointcut(){}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> clazz = joinPoint.getTarget ().getClass ();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature ();
        Method method = clazz.getMethod (signature.getName (), signature.getParameterTypes ());
        LogPub annotation = method.getAnnotation(LogPub.class);
        String name = annotation.value();
        boolean timeLog = annotation.timeLog();
        boolean paramsLog = annotation.paramsLog();

        Object[] args = joinPoint.getArgs ();

        if (signature.getParameterTypes ().length > 0 && paramsLog) {
            StringBuffer nameAndArgs = getNameAndArgs(this.getClass(), clazz.getName(), method.getName(), args);
            log.info("***" + name + "请求参数:" + nameAndArgs);
        }

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed ();
        long stop = System.currentTimeMillis();

        if (proceed != null && paramsLog) {
            log.info("***" + name + "返回参数:" + JSON.toJSONString(proceed));
        }

        if (timeLog) {
            log.info("@@@" + name + "用时" + (stop - start) + "ms");
        }

        return proceed;
    }

    private StringBuffer getNameAndArgs(Class<?> cls, String clazzName, String methodName, Object[] args) throws NotFoundException {

        Map<String, Object> nameAndArgs = new HashMap<>();

        ClassPool pool = ClassPool.getDefault();

        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            throw new NotFoundException("解析请求参数异常");
        }

        CtClass[] parameterTypes = cm.getParameterTypes();
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].getSimpleName().equals("BindingResult")) {
                nameAndArgs.put(attr.variableName(i + pos), args[i]);// paramNames即参数名
            }
        }

        boolean flag = false;
        if (nameAndArgs != null && nameAndArgs.size() > 0) {
            for (Map.Entry<String, Object> entry : nameAndArgs.entrySet()) {
                if (entry.getValue() instanceof String) {
                    flag = true;
                    break;
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        if (flag) {
            // 从Map中获取
            sb.append(JSON.toJSONString(nameAndArgs));
        } else {
            if (args != null) {
                for (Object object : args) {
                    if (object != null) {
                        if (object instanceof MultipartFile || object instanceof ServletRequest
                                || object instanceof ServletResponse || object instanceof BeanPropertyBindingResult) {
                            continue;
                        }
                        sb.append(JSON.toJSONString(object));
                    }
                }
            }
        }
        return sb;
    }

}
