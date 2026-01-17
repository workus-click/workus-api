package com.workus.workus.attend.schedule.presentation.validation;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({Default.class, AfterDefault.class})
public interface ValidationSequence {
}
