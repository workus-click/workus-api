package com.workus.workus.attend.schedule.presentation.validation;

public class ValidationRules {
    private ValidationRules() {}

    public static boolean allOrNone(String a, String b) {
        boolean pa = isProvided(a);
        boolean pb = isProvided(b);
        return (pa && pb) || (!pa && !pb);
    }

    private static boolean isProvided(String s) {
        return s != null && !s.isBlank();
    }
}