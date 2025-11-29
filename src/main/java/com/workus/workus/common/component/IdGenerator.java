package com.workus.workus.common.component;

import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {
    private static TsidFactory tsidFactory;

    public IdGenerator(TsidFactory tsidFactory) {
        IdGenerator.tsidFactory = tsidFactory;
    }
    /** Long 타입 PK */
    public static long nextId() {
        return tsidFactory.create().toLong();
    }

    /** Base-32 문자열 ID */
    public static String nextStringId() {
        return tsidFactory.create().toString();
    }

    /** prefix 포함된 ID (예: DOC_xxx) */
    public static String nextPrefixedId(String prefix) {
        return prefix + "_" + nextStringId();
    }
}
