package com.bestvike.standplat.dao;

import com.bestvike.standplat.data.ArcBuildInfo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface ArcBuildInfoMapper extends Mapper<ArcBuildInfo> {

	String queryArcBuildInfoById();
}
