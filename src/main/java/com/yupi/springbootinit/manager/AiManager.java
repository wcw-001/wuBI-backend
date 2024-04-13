package com.yupi.springbootinit.manager;

import com.mrli.openai.OpenAiService;
import com.mrli.openai.completion.chat.ChatCompletionChoice;
import com.mrli.openai.completion.chat.ChatCompletionRequest;
import com.mrli.openai.completion.chat.ChatCompletionResult;
import com.mrli.openai.completion.chat.ChatMessage;
import com.mrli.openai.config.ChatGptConfig;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.chart.RequestDto;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiManager {
    @Resource
    private YuCongMingClient client;
    public String doChat(Long biModelId,String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(biModelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        if(response == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI 响应错误");
        }
        return response.getData().getContent();
    }
    private  final ChatGptConfig config;

    public AiManager(ChatGptConfig config) {
        this.config=config;
    }

    public String doChatGPT(RequestDto requestDto){
        OpenAiService service = new OpenAiService(config);
        String message = requestDto.getMessage();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage e = new ChatMessage();
        e.setRole("user");
        e.setContent(message);
        messages.add(e);
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .build();
        ChatCompletionResult completion = service.createChatCompletion(completionRequest);
        List<ChatCompletionChoice> choices = completion.getChoices();
        StringBuilder content = new StringBuilder();
        for (ChatCompletionChoice choice : choices){
            content.append(choice.getMessage().getContent());
        }
        return content.toString();
    }
}
