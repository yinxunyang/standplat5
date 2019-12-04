package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.EmpAssessLog;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
* 员工评价修改日志表
* @Param:
* @return:
* @Date: 2019/11/22 
*/
@Repository
public interface EmpAssessLogDao extends Mapper<EmpAssessLog> {
}
