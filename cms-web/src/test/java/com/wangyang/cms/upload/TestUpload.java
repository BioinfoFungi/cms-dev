package com.wangyang.cms.upload;

import com.wangyang.cms.handle.FileHandlers;
import com.wangyang.cms.pojo.enums.AttachmentType;
import com.wangyang.cms.pojo.support.UploadResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestUpload {
    @Autowired
    FileHandlers fileHandlers;
    @Test
    public  void  test(){
//        UploadResult uploadResult = fileHandlers.upload("https://video-subtitle.tedcdn.com/talk/podcast/2018S/None/AlexanderBelcredi_2018S-480p-zh-cn.mp4", "test/0005.mp4", AttachmentType.LOCAL);
    }
}
