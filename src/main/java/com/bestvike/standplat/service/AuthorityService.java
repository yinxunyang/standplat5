package com.bestvike.standplat.service;

import com.bestvike.standplat.data.SysUser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Service
public interface AuthorityService {
    SysUser login(SysUser sysUser, HttpServletRequest httpServletRequest) throws UnsupportedEncodingException;
    void logout(HttpServletRequest httpServletRequest);
    void modifyPassword(SysUser sysUser) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    SysUser getUserInfo(String userId);
}
