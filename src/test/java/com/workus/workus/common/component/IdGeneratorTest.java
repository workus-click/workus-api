package com.workus.workus.common.component;

import com.github.f4b6a3.tsid.TsidFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class IdGeneratorTest {
    @Autowired
    private TsidFactory factory;

    @Test
    @DisplayName("Long 타입 ID가 단조 증가하는지 테스트")
    void testLongIdMonotonicIncreasing() {
        // given
        long prevId = IdGenerator.nextId();
        long nextId;
        for (int i = 0; i < 1000; i++) {
            nextId = IdGenerator.nextId();
            assertTrue(prevId < nextId, "IDs are not monotonically increasing");
            prevId = nextId;
        }
    }
    @Test
    @DisplayName("String 타입 ID가 단조 증가하는지 테스트")
    void testStringIdMonotonicIncreasing() {
        String prevId = IdGenerator.nextStringId();
        String nextId;
        for (int i = 0; i < 1000; i++) {
            nextId = IdGenerator.nextStringId();
            assertTrue(prevId.compareTo(nextId) < 0, "IDs are not monotonically increasing");
            prevId = nextId;
        }
    }
}