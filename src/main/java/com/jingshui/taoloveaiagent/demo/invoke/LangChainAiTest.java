package com.jingshui.taoloveaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class LangChainAiTest {


    public static void main(String[] args) {
        QwenChatModel youApiKeyHere = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-max")
                .build();
        String answer = youApiKeyHere.chat("你好");
        System.out.println("LangChainAi:"+answer);
    }
}

