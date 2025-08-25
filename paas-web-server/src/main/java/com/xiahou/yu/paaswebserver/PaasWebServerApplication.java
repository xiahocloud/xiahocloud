package com.xiahou.yu.paaswebserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@ComponentScan(basePackages = {
    "com.xiahou.yu.paaswebserver",
    "com.xiahou.yu.paasdomincore.runtime",
    "com.xiahou.yu.paasinfracommon"
})
public class PaasWebServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaasWebServerApplication.class, args);
    }

}
