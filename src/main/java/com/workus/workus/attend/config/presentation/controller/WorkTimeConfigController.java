package com.workus.workus.attend.config.presentation.controller;

import com.workus.workus.attend.config.domain.model.WorkTimeConfig;
import com.workus.workus.attend.config.domain.repository.WorkTimeConfigRepository;
import com.workus.workus.attend.config.domain.service.WorkTimeConfigService;
import com.workus.workus.attend.config.presentation.dto.CreateWorkTimeConfigRequest;
import com.workus.workus.attend.config.presentation.dto.UpdateWorkTimeConfigRequest;
import com.workus.workus.attend.config.presentation.dto.WorkTimeConfigResponse;
import com.workus.workus.common.presentation.dto.Response;
import com.workus.workus.schedule.domain.model.TimeRange;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attend/work-time-config")
@RequiredArgsConstructor
public class WorkTimeConfigController {
    private final WorkTimeConfigService workTimeConfigService;
    private final WorkTimeConfigRepository workTimeConfigRepository;

    /**
     * 근무시간 설정 목록 조회
     */
    @GetMapping
    public Response<List<WorkTimeConfigResponse>> getWorkTimeConfigs(
            @RequestParam Long storeId
    ) {
        List<WorkTimeConfig> configs = workTimeConfigRepository.findByStoreId(storeId);
        List<WorkTimeConfigResponse> responses = configs.stream()
                .map(WorkTimeConfigResponse::from)
                .toList();
        return Response.of(0L, "성공", responses);
    }

    /**
     * 근무시간 설정 단건 조회
     */
    @GetMapping("/{id}")
    public Response<WorkTimeConfigResponse> getWorkTimeConfig(
            @PathVariable Long id
    ) {
        return workTimeConfigRepository.findById(id)
                .map(config -> Response.of(0L, "성공", WorkTimeConfigResponse.from(config)))
                .orElse(Response.of(-1L, "근무시간 설정을 찾을 수 없습니다.", null));
    }

    /**
     * 근무시간 설정 생성
     */
    @PostMapping
    public Response<WorkTimeConfigResponse> createWorkTimeConfig(
            @Valid @RequestBody CreateWorkTimeConfigRequest request
    ) {
        TimeRange workTime = new TimeRange(request.workStartTime(), request.workEndTime());
        TimeRange breakTime = (request.breakStartTime() != null && request.breakEndTime() != null)
                ? new TimeRange(request.breakStartTime(), request.breakEndTime())
                : null;

        WorkTimeConfig created = workTimeConfigService.createWorkTimeConfig(
                request.storeId(),
                request.workerType(),
                request.title(),
                workTime,
                breakTime
        );

        return Response.of(0L, "성공", WorkTimeConfigResponse.from(created));
    }

    /**
     * 근무시간 설정 수정
     */
    @PutMapping("/{id}")
    public Response<WorkTimeConfigResponse> updateWorkTimeConfig(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkTimeConfigRequest request
    ) {
        TimeRange workTime = (request.workStartTime() != null && request.workEndTime() != null)
                ? new TimeRange(request.workStartTime(), request.workEndTime())
                : null;

        TimeRange breakTime = (request.breakStartTime() != null && request.breakEndTime() != null)
                ? new TimeRange(request.breakStartTime(), request.breakEndTime())
                : null;

        WorkTimeConfig updated = workTimeConfigService.updateWorkTimeConfig(
                id,
                request.title(),
                workTime,
                breakTime
        );

        return Response.of(0L, "성공", WorkTimeConfigResponse.from(updated));
    }

    /**
     * 근무시간 설정 삭제
     */
    @DeleteMapping("/{id}")
    public Response<Void> deleteWorkTimeConfig(
            @PathVariable Long id
    ) {
        workTimeConfigService.deleteWorkTimeConfig(id);
        return Response.of(0L, "성공", null);
    }
}
