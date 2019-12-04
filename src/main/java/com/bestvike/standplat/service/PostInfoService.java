package com.bestvike.standplat.service;

import com.bestvike.standplat.data.PostInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Classname PostInfoService
 * @Description
 * @Date 2019/11/6 13:50
 * @Created by yl
 */
@Service
public interface PostInfoService {
    List<PostInfo> fetchAll();
    List<PostInfo> fetch(PostInfo postInfo);
    int create(PostInfo postInfo, String user);
    int modify(PostInfo postInfo);
    Map remove(String codes);
}
