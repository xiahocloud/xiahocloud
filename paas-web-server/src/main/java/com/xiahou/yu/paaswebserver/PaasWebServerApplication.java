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
        System.out.println("JVM http.proxyHost: " + System.getProperty("http.proxyHost"));
        System.out.println("JVM http.proxyPort: " + System.getProperty("http.proxyPort"));
        SpringApplication.run(PaasWebServerApplication.class, args);
    }

}
