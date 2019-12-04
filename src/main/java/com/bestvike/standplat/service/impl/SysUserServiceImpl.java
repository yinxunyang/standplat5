package com.bestvike.standplat.service.impl;

import com.bestvike.commons.crypto.bcrypt.BCryptPasswordEncoder;
import com.bestvike.commons.exception.ServiceException;
import com.bestvike.commons.utils.EncryptUtils;
import com.bestvike.commons.utils.StringUtils;
import com.bestvike.standplat.dao.SysUserDao;
import com.bestvike.standplat.data.SysUser;
import com.bestvike.standplat.service.BaseService;
import com.bestvike.standplat.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SysUserServiceImpl extends BaseService implements SysUserService {
    @Autowired
    private SysUserDao sysUserDao;

    @Value("${app.authority.default-password:666666}")
    private String defaultPassword;

    @Override
    public List<SysUser> fetch(SysUser sysUser) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        Example.Criteria criteria1 = example.createCriteria();
        if (!StringUtils.isEmpty(sysUser.getDeptId())) {
            criteria.andEqualTo("deptId", sysUser.getDeptId());
        }
        criteria.andEqualTo("status", "0000");


        return null;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int modifyAvatar(String id, String avatar) {
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setAvatar(avatar);
        return sysUserDao.updateByPrimaryKeySelective(sysUser);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int modifySettings(String id, Map<String, Object> settings) {
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setSettingsMap(settings);
        return sysUserDao.updateByPrimaryKeySelective(sysUser);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int create(SysUser sysUser) throws NoSuchAlgorithmException {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("empId", sysUser.getEmpId()).andEqualTo("status","0000");
        int cnt = sysUserDao.selectCountByExample(example);
        if (cnt > 0) {
            throw new ServiceException("该用户已存在");
        }
        if (StringUtils.isEmpty(sysUser.getId())) {
            sysUser.setId(StringUtils.guid());
        }
        if (StringUtils.isEmpty(sysUser.getPassword())) {
            sysUser.setPassword(new BCryptPasswordEncoder().encode(EncryptUtils.MD5Encode(defaultPassword)));
        }
        if (StringUtils.isEmpty(sysUser.getStatus())) {
            sysUser.setStatus("0000");
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sysUser.setRegisterTime(dateFormat.parse(dateFormat.format(date)));
        } catch (Exception e){
            e.printStackTrace();
        }
        return sysUserDao.insert(sysUser);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int modify(SysUser sysUser) {
        int result = sysUserDao.updateByPrimaryKeySelective(sysUser);
        if (result <= 0) {
            throw new ServiceException("内部异常");
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(String ids) {
        /*Example example = new Example(SysUser.class);
        example.createCriteria().andIn("id", Arrays.asList(ids.split(",")));
        return sysUserDao.deleteByExample(example);*/
        String []sysId = ids.split(",");
        for(String id:sysId){
            /*Example example = new Example(SysUser.class);
            example.createCriteria().andEqualTo("id",id);*/
            SysUser user = sysUserDao.selectByPrimaryKey(id);
            sysUserDao.deleteByPrimaryKey(user);
            /*if(!user.getRoles().equals("8888")){
                throw new ServiceException("该用户存在管理员或评委角色，请修改用户角色后再进行删除");
            } else {
                *//*user.setStatus("9999");
                sysUserDao.updateByPrimaryKey(user);*//*
                sysUserDao.deleteByPrimaryKey(user);
            }*/
        }
    }

    @Override
    public int resetPass(String id) {
        try {
            String password = new BCryptPasswordEncoder().encode(EncryptUtils.MD5Encode(defaultPassword));
            return sysUserDao.resetPassword(id, password);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("密码加密异常");
        }
    }

    @Override
    public int saveGrants(String id, String[] roles) {
        String roleIds = "";
        if (roles != null && roles.length > 0) {
            roleIds = String.join(",", roles);
        }
        return sysUserDao.saveGrants(id, roleIds);
    }
}
