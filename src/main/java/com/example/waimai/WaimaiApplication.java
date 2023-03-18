package com.example.waimai;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ServletComponentScan
@SpringBootApplication(exclude=DataSourceAutoConfiguration.class)
@EnableTransactionManagement
public class WaimaiApplication {

    public static void main(String[] args) {

        SpringApplication.run(WaimaiApplication.class, args);
        log.info("启动项目");
    }

}
