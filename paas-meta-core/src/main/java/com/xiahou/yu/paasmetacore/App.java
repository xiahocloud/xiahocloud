package com.xiahou.yu.paasmetacore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/2/27 16:36
 * @version 1.0
 */

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App.class);
        application.run(args);
    }
}
