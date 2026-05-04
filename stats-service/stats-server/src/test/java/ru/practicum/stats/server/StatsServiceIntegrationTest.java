package ru.practicum.stats.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты сервиса статистики")
class StatsServiceIntegrationTest {

    @Autowired
    private StatsService statsService;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        statsService.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service").uri("/events/1").ip("1.1.1.1").timestamp(now).build());
        statsService.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service").uri("/events/1").ip("1.1.1.1").timestamp(now).build());
        statsService.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service").uri("/events/1").ip("2.2.2.2").timestamp(now).build());
    }

    @Test
    @DisplayName("Получение общего количества обращений к эндпоинту")
    void shouldReturnTotalHitsCount() {
        List<ViewStatsDto> stats = statsService.getStats(now.minusHours(1), now.plusHours(1), null, false);

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getHits()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Подсчёт уникальных посетителей по IP")
    void shouldReturnUniqueHitsCount() {
        List<ViewStatsDto> stats = statsService.getStats(now.minusHours(1), now.plusHours(1), null, true);

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getHits()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Возврат пустого списка при фильтре по несуществующему URI")
    void shouldReturnEmptyListForUnknownUri() {
        List<ViewStatsDto> stats = statsService.getStats(
                now.minusHours(1), now.plusHours(1), List.of("/events/2"), false);

        assertThat(stats).isEmpty();
    }
}