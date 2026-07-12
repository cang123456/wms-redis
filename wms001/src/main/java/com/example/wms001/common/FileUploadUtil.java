package com.example.wms001.common;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUploadUtil {

    /**
     * 上传文件到服务器本地
     * @param file 前端传来的文件
     * @param basePath 服务器存储根目录
     * @return 生成的文件名（带后缀）
     */
    public static String uploadToLocal(MultipartFile file, String basePath) throws IOException {
        // 1. 校验文件
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 2. 获取文件后缀（.jpg/.png）
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));

        // 3. 生成唯一文件名，避免重名覆盖
        // 格式：UUID + 时间戳 + 后缀
        String fileName = UUID.randomUUID().toString().replace("-", "")
                + System.currentTimeMillis()
                + suffix;

        // 4. 构建完整路径，自动创建目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs(); // 递归创建所有父目录
        }

        // 5. 写入服务器硬盘
        File destFile = new File(basePath + fileName);
        file.transferTo(destFile);

        return fileName;
    }
}