package com.example.waimai.controller;

import com.example.waimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

//文件上传下载
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    //file 跟请求的参数名一致也就是file
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        String originalFileName=file.getOriginalFilename();
       String suffix= originalFileName.substring(originalFileName.lastIndexOf("."));

       String fileName= UUID.randomUUID().toString()+suffix;

       //创建一个目录对象
        File dir=new File(basePath);
        //判读目录是否存在
        if(!dir.exists()){
            //创建
            dir.mkdirs();
        }



        try{
            file.transferTo(new File(basePath+fileName));
        }catch (Exception e){
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));

            ServletOutputStream outputStream=response.getOutputStream();
            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes =new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
