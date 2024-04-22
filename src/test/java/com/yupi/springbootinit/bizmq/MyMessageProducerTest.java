package com.yupi.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;


@SpringBootTest
class MyMessageProducerTest {


    @Resource
    private BiMessageProducer messageProducer;
    @Test
    void sendMessage() {
        //messageProducer.sendMessage("code_exchange","my_routingKey","欢迎来到wcw智能BI系统");
    }
}
