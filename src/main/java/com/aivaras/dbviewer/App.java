package com.aivaras.dbviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args){
        String username = args[0];
        String password = args[1];
        System.out.println(username + ":" + password);
        SpringApplication.run(App.class);
    }
}
