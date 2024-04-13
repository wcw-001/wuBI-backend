package com.yupi.springbootinit.manager;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiManagerTest {
    @Resource
    private AiManager aiManager;
    @Test
    void doChat(){
//        String answer = aiManager.doChat("邓紫棋");
//        System.out.println(answer);
    }
}
