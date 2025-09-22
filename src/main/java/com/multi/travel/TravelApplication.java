package com.multi.travel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.multi.travel.mapper")
public class TravelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelApplication.class, args);
    }

}