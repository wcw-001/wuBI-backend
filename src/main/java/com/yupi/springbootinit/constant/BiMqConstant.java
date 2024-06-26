package com.yupi.springbootinit.constant;

public interface BiMqConstant {
    String BI_EXCHANGE_NAME = "bi_exchange";
    String BI_QUEUE_NAME = "bi_queue";
    String BI_ROUTING_KEY = "bi_routingKey";

    /**
     * 死信队列交换机
     */
    String BI_DLX_EXCHANGE_NAME = "bi-dlx-exchange";

    /**
     * 死信队列
     */
    String BI_DLX_QUEUE_NAME = "bi_dlx_queue";

    /**
     * 死信队列路由键
     */
    String BI_DLX_ROUTING_KEY = "bi_dlx_routingKey";

    /**
     * AI 问答
     */
    String AI_QUESTION_EXCHANGE_NAME = "ai_question_exchange";

    String AI_QUESTION_QUEUE = "ai_question_queue";
    String AI_QUESTION_ROUTING_KEY = "ai_question_routingKey";


    /**
     * AI对话死信队列交换机
     */
    String AI_DLX_EXCHANGE_NAME = "ai-dlx-exchange";

    /**
     * AI对话死信队列
     */
    String AI_DLX_QUEUE_NAME = "ai_dlx_queue";

    /**
     * AI对话死信队列路由键
     */
    String AI_DLX_ROUTING_KEY = "ai_dlx_routingKey";

    /**
     * MQ ip地址
     */
    String BI_MQ_HOST = "xxxxx.xxxxx.xxxx";
}
