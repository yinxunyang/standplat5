package com.bestvike.standplat;

import com.bestvike.standplat.dao.SysUserDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: yinxunyang
 * @Description: 中间表单元测试类
 * @Date: 2019/9/9 17:06
 * @param:
 * @return:
 */
public class MidTableTest extends BaseTest {

	@Autowired
	private SysUserDao sysUserDao;


	@Test
	public void test10() {
		int ss = sysUserDao.selectId();
		System.out.println("ss:" + ss);
	}

}
