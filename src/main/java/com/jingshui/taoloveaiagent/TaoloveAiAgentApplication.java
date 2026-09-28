package com.jingshui.taoloveaiagent;

import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
@EnableAsync
public class TaoloveAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaoloveAiAgentApplication.class, args);
    }

}
