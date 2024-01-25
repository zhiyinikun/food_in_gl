package com.gl.controller.admin;

import com.gl.constant.FilePathConstant;
import com.gl.constant.MessageConstant;
import com.gl.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j

public class CommonController {
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("上传的文件为:{}",file);

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();

        //利用uuid构造唯一的文件名
        int index = originalFilename.lastIndexOf("."); //截取最后的.    不用split是因为会占用空间

        String extename = originalFilename.substring(index);//获取文件扩展名
        String newFileName = UUID.randomUUID().toString() + extename;

        log.info("新文件名为: {}",newFileName);


        //将上传的存储存储在本地磁盘目录里 F:\waimai\food-in-gl\food-in-gl-server\src\main\resources\images
        String filePath = FilePathConstant.IMAGE_PATH +newFileName;
        try {
            file.transferTo(new File(filePath));//保存图片
            //利用通过uploadFile接口映射图片的本地路径。
            return Result.success(FilePathConstant.IMAGE_URL+newFileName);
        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
