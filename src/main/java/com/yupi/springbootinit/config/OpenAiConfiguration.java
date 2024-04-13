package com.yupi.springbootinit.config;

import com.mrli.openai.OpenAiService;
import com.mrli.openai.config.ChatGptEnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author LiDaShuai
 * @version 1.0
 * @project chatgpt-test
 * @date: 2023/3/4 12:53
 * @copyright 2009–2022xxxxx all rights reserved.
 */
@Configuration
@ConditionalOnMissingBean(OpenAiService.class)
public class OpenAiConfiguration {
    /**
     * 创建并返回一个OpenAiService实例。
     * @param config 提供OpenAI配置信息的对象。
     * @return 返回初始化好的OpenAiService实例。
     */
    @Bean
    public OpenAiService openAiService(ChatGptEnableAutoConfiguration config){
        return  new OpenAiService(config.getConfig());// 通过ChatGptEnableAutoConfiguration的配置信息，创建OpenAiService实例
    }

}
