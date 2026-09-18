package com.campus.property.controller;

import com.campus.property.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${file.upload-dir:d:/graduation_project/campus-property-server/uploads}")
    private String uploadDir;

    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File dest = new File(dir.getAbsolutePath(), newFilename);
            file.transferTo(dest);
            String url = "/api/upload/image/" + newFilename;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/image/{filename}")
    public org.springframework.core.io.Resource getImage(@PathVariable String filename) {
        try {
            File file = new File(new File(uploadDir).getAbsolutePath(), filename);
            if (file.exists()) {
                return new org.springframework.core.io.FileSystemResource(file);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
