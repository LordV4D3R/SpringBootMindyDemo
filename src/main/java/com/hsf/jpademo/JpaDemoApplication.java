package com.hsf.jpademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =========================================================================================
 * ĐIỂM KHỞI CHẠY ỨNG DỤNG (ENTRY POINT) - TƯƠNG ĐƯƠNG Program.cs TRONG .NET
 * =========================================================================================
 * - @SpringBootApplication: Tương đương 3 annotation gộp lại:
 *   1. @Configuration: Đánh dấu class cấu hình.
 *   2. @EnableAutoConfiguration: Kích hoạt cơ chế tự cấu hình (DataSource, Hibernate, Tomcat).
 *   3. @ComponentScan: Quét toàn bộ package 'com.hsf.jpademo' để tự động nạp các bean
 *      (@Entity, @Repository, @Service, @Component).
 * =========================================================================================
 */
@SpringBootApplication
public class JpaDemoApplication {

    public static void main(String[] args) {
        // Khởi động Spring Boot container và máy chủ nhúng Tomcat trên cổng 8080
        SpringApplication.run(JpaDemoApplication.class, args);
    }
}
