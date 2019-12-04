package com.bestvike.standplat.controller;

import com.bestvike.commons.entity.User;
import com.bestvike.commons.exception.AuthorityException;
import com.bestvike.commons.exception.CredentialsException;
import com.bestvike.commons.exception.LoginException;
import com.bestvike.commons.exception.ServiceException;

import com.bestvike.commons.support.RestError;
import com.bestvike.commons.support.RestStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * basecontroller, need to be extented by all the controller class.
 * @author Li Hua
 * @since v1.0.0
 */
public class BaseController {
    protected Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    protected HttpServletRequest httpServletRequest;
/*
    @Autowired
*/
/*    @Qualifier("authorityCache")
    protected Cache cache;*/

    protected String getLoginUserId() {
        SecurityContext securityContext = (SecurityContext) httpServletRequest.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    protected User getLoginUser() {
        String userId = this.getLoginUserId();
       /* if (!StringUtils.isEmpty(userId)) {
            return cache.get("user_details:" + userId, User.class);
        }*/
        return null;
    }

    @Value("${app.error.prefix:}")
    protected String prefix;
    @Value("${app.instance.code:}")
    protected String appCode;

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleBusinessException(ServiceException e) {
        logger.error(e);
        logger.error(e.getCause());
        RestError restError = RestError.build(this.appCode, this.prefix, RestStatus.SERVICE_ERROR, e.getCode(), e.getMessage(), e.getDebug());
        return new ResponseEntity<>(restError, RestStatus.SERVICE_ERROR.getStatus());
    }

    // Spring 登录异常
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleAuthenticationException(AuthenticationException e) {
        logger.error(e);
        if (e.getCause() != null) {
            logger.error(e.getCause());
        }
        RestError restError = RestError.build(this.appCode, this.prefix, RestStatus.UNAUTHORIZED, RestStatus.UNAUTHORIZED.getCode(),
                !StringUtils.isEmpty(e.getMessage()) ? e.getMessage() : RestStatus.UNAUTHORIZED.getMessage(), null);
        return new ResponseEntity<>(restError, RestStatus.UNAUTHORIZED.getStatus());
    }

    // 登录失败401
    @ExceptionHandler(CredentialsException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleAuthorityException(CredentialsException e) {
        logger.error(e);
        if (e.getCause() != null) {
            logger.error(e.getCause());
        }
        RestError restError = RestError.build(this.appCode, this.prefix, RestStatus.UNAUTHORIZED, RestStatus.UNAUTHORIZED.getCode(), e.getMessage(), e.getDebug());
        return new ResponseEntity<>(restError, RestStatus.UNAUTHORIZED.getStatus());
    }

    // 登录失败403，需要校验码
    @ExceptionHandler(LoginException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleAuthorityException(LoginException e) {
        logger.error(e);
        if (e.getCause() != null) {
            logger.error(e.getCause());
        }
        RestError restError = RestError.build(this.appCode, this.prefix, RestStatus.FORBIDDEN, RestStatus.FORBIDDEN.getCode(), e.getMessage(), e.getDebug());
        return new ResponseEntity<>(restError, RestStatus.FORBIDDEN.getStatus());
    }

    // 内部鉴权失败
    @ExceptionHandler(AuthorityException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleAuthorityException(AuthorityException e) {
        logger.error(e);
        if (e.getCause() != null) {
            logger.error(e.getCause());
        }
        RestError restError = RestError.build(this.appCode, this.prefix, RestStatus.FORBIDDEN, e.getDebug());
        return new ResponseEntity<>(restError, RestStatus.FORBIDDEN.getStatus());
    }

    /**
     * 其他未处理的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<RestError> handleException(Exception e) {
        logger.error(e);
        logger.error(e.getCause());
        RestError restError = RestError.build(this.appCode, this.prefix, RestStatus.INTERNAL_SERVER_ERROR, RestStatus.INTERNAL_SERVER_ERROR.getCode(), RestStatus.INTERNAL_SERVER_ERROR.getMessage(), e.getMessage());
        return new ResponseEntity<>(restError, RestStatus.INTERNAL_SERVER_ERROR.getStatus());
    }
}
