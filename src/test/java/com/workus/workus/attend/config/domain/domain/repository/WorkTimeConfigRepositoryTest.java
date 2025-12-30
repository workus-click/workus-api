package com.workus.workus.attend.config.domain.domain.repository;

import com.workus.workus.attend.config.domain.model.EmployeeType;
import com.workus.workus.attend.config.domain.model.WorkTimeConfig;
import com.workus.workus.attend.config.domain.repository.WorkTimeConfigRepository;
import com.workus.workus.attend.config.repository.JpaWorkTimeConfigRepository;
import com.workus.workus.schedule.domain.model.TimeRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WorkTimeConfigRepositoryTest {

    @Autowired
    private WorkTimeConfigRepository workTimeConfigRepository;

    @Autowired
    private JpaWorkTimeConfigRepository jpaWorkTimeConfigRepository;

    @Nested
    @DisplayName("근무시간 설정 저장 및 조회")
    class SaveAndFind {
        @Test
        @DisplayName("근무시간 설정을 저장하고 조회할 수 있다")
        void save_and_find() {
            // given
            Long storeId = 1L;
            WorkTimeConfig config = WorkTimeConfig.of(
                    storeId,
                    EmployeeType.PART_TIME,
                    "알바 근무시간",
                    new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                    new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
            );

            // when: 저장
            jpaWorkTimeConfigRepository.save(config);

            // then: ID로 조회
            WorkTimeConfig found = jpaWorkTimeConfigRepository.findById(config.getId())
                    .orElseThrow();

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(config.getId());
            assertThat(found.getStoreId()).isEqualTo(storeId);
            assertThat(found.getWorkerType()).isEqualTo(EmployeeType.PART_TIME);
        }
    }

    @Nested
    @DisplayName("storeId로 근무시간 설정 조회")
    class FindByStoreId {
        @Nested
        @DisplayName("다른 storeId의 설정이 존재하는 경우")
        class GivenMultipleStoreIds {
            @Test
            @DisplayName("특정 storeId로 조회하면 해당 storeId의 설정만 반환한다")
            void shouldReturnOnlyConfigsForGivenStoreId() {
                // given
                Long storeId1 = 1L;
                Long storeId2 = 2L;

                WorkTimeConfig config1 = WorkTimeConfig.of(
                        storeId1,
                        EmployeeType.PART_TIME,
                        "알바 근무시간",
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                        new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
                );

                WorkTimeConfig config2 = WorkTimeConfig.of(
                        storeId1,
                        EmployeeType.FULL_TIME,
                        "정규직 근무시간",
                        new TimeRange(LocalTime.of(10, 0), LocalTime.of(18, 0)),
                        new TimeRange(LocalTime.of(13, 0), LocalTime.of(14, 0))
                );

                WorkTimeConfig config3 = WorkTimeConfig.of(
                        storeId2,
                        EmployeeType.PART_TIME,
                        "알바 근무시간",
                        new TimeRange(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                        new TimeRange(LocalTime.of(11, 0), LocalTime.of(12, 0))
                );

                jpaWorkTimeConfigRepository.save(config1);
                jpaWorkTimeConfigRepository.save(config2);
                jpaWorkTimeConfigRepository.save(config3);

                // when
                List<WorkTimeConfig> foundConfigs = workTimeConfigRepository.findByStoreId(storeId1);

                // then
                assertThat(foundConfigs).hasSize(2);
                assertThat(foundConfigs).extracting(WorkTimeConfig::getStoreId)
                        .containsOnly(storeId1);
                assertThat(foundConfigs).extracting(WorkTimeConfig::getId)
                        .containsExactlyInAnyOrder(config1.getId(), config2.getId());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 storeId로 조회하는 경우")
        class GivenNonExistentStoreId {
            @Test
            @DisplayName("빈 리스트를 반환한다")
            void shouldReturnEmptyList() {
                // given
                Long nonExistentStoreId = 999L;

                // when
                List<WorkTimeConfig> foundConfigs = workTimeConfigRepository.findByStoreId(nonExistentStoreId);

                // then
                assertThat(foundConfigs).isEmpty();
            }
        }

        @Nested
        @DisplayName("같은 storeId에 여러 설정이 존재하는 경우")
        class GivenMultipleConfigsForSameStoreId {
            @Test
            @DisplayName("해당 storeId의 모든 설정을 반환한다")
            void shouldReturnAllConfigsForStoreId() {
                // given
                Long storeId = 1L;

                WorkTimeConfig partTimeConfig = WorkTimeConfig.of(
                        storeId,
                        EmployeeType.PART_TIME,
                        "알바 근무시간",
                        new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0)),
                        new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0))
                );

                WorkTimeConfig fullTimeConfig = WorkTimeConfig.of(
                        storeId,
                        EmployeeType.FULL_TIME,
                        "정규직 근무시간",
                        new TimeRange(LocalTime.of(10, 0), LocalTime.of(18, 0)),
                        new TimeRange(LocalTime.of(13, 0), LocalTime.of(14, 0))
                );

                WorkTimeConfig nightShiftConfig = WorkTimeConfig.of(
                        storeId,
                        EmployeeType.PART_TIME,
                        "야간 근무시간",
                        new TimeRange(LocalTime.of(22, 0), LocalTime.of(6, 0)),
                        new TimeRange(LocalTime.of(0, 0), LocalTime.of(1, 0))
                );

                jpaWorkTimeConfigRepository.save(partTimeConfig);
                jpaWorkTimeConfigRepository.save(fullTimeConfig);
                jpaWorkTimeConfigRepository.save(nightShiftConfig);

                // when
                List<WorkTimeConfig> foundConfigs = workTimeConfigRepository.findByStoreId(storeId);

                // then
                assertThat(foundConfigs).hasSize(3);
                assertThat(foundConfigs).extracting(WorkTimeConfig::getStoreId)
                        .containsOnly(storeId);
                assertThat(foundConfigs).extracting(WorkTimeConfig::getWorkerType)
                        .containsExactlyInAnyOrder(EmployeeType.PART_TIME, EmployeeType.FULL_TIME, EmployeeType.PART_TIME);
            }
        }
    }
}

