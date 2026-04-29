package ru.practicum.stats.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StatsServiceIntegrationTest {

	@Autowired
	private StatsService statsService;

	@Test
	void saveAndQueryStats() {
		LocalDateTime now = LocalDateTime.now();
		statsService.saveHit(EndpointHitDto.builder()
				.app("ewm-main-service").uri("/events/1").ip("1.1.1.1").timestamp(now).build());
		statsService.saveHit(EndpointHitDto.builder()
				.app("ewm-main-service").uri("/events/1").ip("1.1.1.1").timestamp(now).build());
		statsService.saveHit(EndpointHitDto.builder()
				.app("ewm-main-service").uri("/events/1").ip("2.2.2.2").timestamp(now).build());

		List<ViewStatsDto> all = statsService.getStats(now.minusHours(1), now.plusHours(1), null, false);
		assertThat(all).hasSize(1);
		assertThat(all.get(0).getHits()).isEqualTo(3L);

		List<ViewStatsDto> unique = statsService.getStats(now.minusHours(1), now.plusHours(1), null, true);
		assertThat(unique).hasSize(1);
		assertThat(unique.get(0).getHits()).isEqualTo(2L);

		List<ViewStatsDto> filtered = statsService.getStats(
				now.minusHours(1), now.plusHours(1), List.of("/events/2"), false);
		assertThat(filtered).isEmpty();
	}
}
