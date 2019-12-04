package com.bestvike.standplat.controller;

import com.bestvike.standplat.data.SysUser;
import com.bestvike.standplat.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@RestController
public class SysUserController extends BaseController {
    @Autowired
    private SysUserService sysUserService;

//    @PutMapping("/api/authority/roles")
//    public SysUser modify(@RequestBody SysUser sysUser) {
//        return sysUserService.modify(sysUser);
//    }

    @GetMapping("/api/users")
    public List<SysUser> fetch(SysUser sysUser) {
        return sysUserService.fetch(sysUser);
    }
    @PutMapping("/api/users/avatar")
    public int modifyAvatar(@RequestBody String avatar) {
        return sysUserService.modifyAvatar(getLoginUserId(), avatar);
    }
    @PutMapping("/api/users/settings")
    public int modifySettings(@RequestBody Map<String, Object> settings) {
        return sysUserService.modifySettings(getLoginUserId(), settings);
    }

    @PostMapping("/api/users")
    public int create(@RequestBody SysUser sysUser) throws NoSuchAlgorithmException {
        return sysUserService.create(sysUser);
    }

    @PutMapping("/api/users")
    public int modify(@RequestBody SysUser sysUser) {
        return sysUserService.modify(sysUser);
    }

    @DeleteMapping("/api/users/{ids}")
    public void remove(@PathVariable String ids) {
        sysUserService.remove(ids);
    }

    /**
     * 用户管理重置密码为666666
     * @return
     */
    @PutMapping("/api/users/{id}/password/reset")
    public int resetPass(@PathVariable("id") String id) {
        return sysUserService.resetPass(id);
    }


    /**
     * 保存用户授权角色
     * @return
     */
    @PutMapping("/api/users/{id}/roles")
    public int saveGrants(@PathVariable String id, @RequestBody String[] roles) {
        return sysUserService.saveGrants(id, roles);
    }
}
