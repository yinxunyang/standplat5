package com.bestvike.standplat;

import com.bestvike.standplat.dao.ArcBuildInfoMapper;
import com.bestvike.standplat.dao.SysUserDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

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
			String ss = sysUserDao.selectId();
			System.out.println("ss:" + ss);
		}

	/*	@Autowired
		private ArcBuildInfoMapper arcBuildInfoMapper;


		@Test
		public void test10() {
			String ss = arcBuildInfoMapper.queryArcBuildInfoById();
			System.out.println("ss:" + ss);
		}*/
	/*@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void test2() {
		String sql = "select sysdate from dual";
		jdbcTemplate.execute(sql);
		System.out.println("执行完成");
	}*/

}
