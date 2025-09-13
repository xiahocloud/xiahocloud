package com.xiahou.yu.paaswebserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.xiahou.yu"
})
public class PaasWebServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaasWebServerApplication.class, args);
    }

}
