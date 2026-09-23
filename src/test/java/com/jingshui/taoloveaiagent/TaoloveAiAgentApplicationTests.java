package com.jingshui.taoloveaiagent;

import com.jingshui.taoloveaiagent.rag.LoveAppDocumentLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaoloveAiAgentApplicationTests {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Test
    void contextLoads() {
        loveAppDocumentLoader.loadMarkdowns();
    }

}
