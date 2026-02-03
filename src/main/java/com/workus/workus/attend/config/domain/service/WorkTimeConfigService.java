package com.workus.workus.attend.config.domain.service;

import com.workus.workus.attend.config.domain.exception.BreakTimeOutOfWorkTimeRangeException;
import com.workus.workus.attend.config.domain.model.EmployeeType;
import com.workus.workus.attend.config.domain.model.WorkTimeConfig;
import com.workus.workus.attend.config.domain.repository.WorkTimeConfigRepository;
import com.workus.workus.schedule.domain.model.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 근무시간 설정 생성 및 수정 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WorkTimeConfigService {
    private final WorkTimeConfigRepository workTimeConfigRepository;

    /**
     * 새로운 근무시간 설정을 생성합니다.
     * 
     * @param storeId 매장 ID
     * @param workerType 직원 구분
     * @param title 근무시간 설정명
     * @param workTime 근무시간
     * @param breakTime 휴게시간 (null 가능)
     * @return 생성된 WorkTimeConfig
     * @throws IllegalArgumentException 기본값이 유효하지 않거나 휴게시간이 근무시간 범위를 벗어나는 경우
     */
    public WorkTimeConfig createWorkTimeConfig(
            Long storeId,
            EmployeeType workerType,
            String title,
            TimeRange workTime,
            TimeRange breakTime
    ) {
        // 기본값 체크
        validateDefaultValues(storeId, workerType, title, workTime);

        // WorkTimeConfig 생성 및 저장
        WorkTimeConfig workTimeConfig = WorkTimeConfig.of(storeId, workerType, title, workTime, breakTime);
        return workTimeConfigRepository.save(workTimeConfig);
    }

    /**
     * 기존 근무시간 설정을 수정합니다.
     * 
     * @param workTimeConfigId 근무시간 설정 ID
     * @param title 근무시간 설정명 (null이면 변경하지 않음)
     * @param workTime 근무시간 (null이면 변경하지 않음)
     * @param breakTime 휴게시간 (null이면 변경하지 않음)
     * @return 수정된 WorkTimeConfig
     * @throws IllegalArgumentException 근무시간 설정이 존재하지 않거나 기본값이 유효하지 않거나 휴게시간이 근무시간 범위를 벗어나는 경우
     */
    public WorkTimeConfig updateWorkTimeConfig(
            Long workTimeConfigId,
            String title,
            TimeRange workTime,
            TimeRange breakTime
    ) {
        // 기존 설정 조회
        WorkTimeConfig existingConfig = workTimeConfigRepository.findById(workTimeConfigId)
                .orElseThrow(() -> new IllegalArgumentException("근무시간 설정을 찾을 수 없습니다. workTimeConfigId: " + workTimeConfigId));

        // 제목 변경
        if (title != null && !title.isBlank()) {
            changeTitle(existingConfig, title);
        }

        // 근무시간 변경
        if (workTime != null) {
            existingConfig.changeWorkTime(workTime);
        }

        // 휴게시간 변경
        if (breakTime != null) {
            existingConfig.changeBreakTime(breakTime);
        } else if (workTime != null) {
            // 근무시간만 변경하고 휴게시간이 null이면 기존 휴게시간 유지
            // 하지만 근무시간 변경 시 휴게시간이 새로운 근무시간 범위를 벗어날 수 있으므로 검증됨
        }

        return workTimeConfigRepository.save(existingConfig);
    }

    /**
     * 근무시간 설정을 삭제합니다 (소프트 삭제).
     * 
     * @param workTimeConfigId 근무시간 설정 ID
     * @throws IllegalArgumentException 근무시간 설정이 존재하지 않는 경우
     */
    public void deleteWorkTimeConfig(Long workTimeConfigId) {
        WorkTimeConfig workTimeConfig = workTimeConfigRepository.findById(workTimeConfigId)
                .orElseThrow(() -> new IllegalArgumentException("근무시간 설정을 찾을 수 없습니다. workTimeConfigId: " + workTimeConfigId));

        workTimeConfigRepository.delete(workTimeConfig);
    }

    /**
     * 기본값 유효성 검증
     * 
     * @param storeId 매장 ID
     * @param workerType 직원 구분
     * @param title 근무시간 설정명
     * @param workTime 근무시간
     * @throws IllegalArgumentException 기본값이 유효하지 않은 경우
     */
    private void validateDefaultValues(Long storeId, EmployeeType workerType, String title, TimeRange workTime) {
        if (storeId == null) {
            throw new IllegalArgumentException("매장 ID는 필수입니다.");
        }
        if (workerType == null) {
            throw new IllegalArgumentException("직원 구분은 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("근무시간 설정명은 필수입니다.");
        }
        if (workTime == null) {
            throw new IllegalArgumentException("근무시간은 필수입니다.");
        }
    }

    /**
     * 제목 변경
     */
    private void changeTitle(WorkTimeConfig config, String newTitle) {
        config.changeTitle(newTitle);
    }
}
