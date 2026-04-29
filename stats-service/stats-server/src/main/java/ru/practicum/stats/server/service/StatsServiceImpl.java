package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.exception.BadRequestException;
import ru.practicum.stats.server.mapper.StatsMapper;
import ru.practicum.stats.server.model.ViewStats;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

	private final StatsRepository repository;

	@Override
	@Transactional
	public void saveHit(EndpointHitDto dto) {
		repository.save(StatsMapper.toEntity(dto));
	}

	@Override
	public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
		if (start == null || end == null || start.isAfter(end)) {
			throw new BadRequestException("Start must be before end and both must be provided");
		}
		List<ViewStats> stats;
		boolean hasUris = uris != null && !uris.isEmpty();
		if (unique) {
			stats = hasUris
					? repository.findUniqueStatsByUris(start, end, uris)
					: repository.findUniqueStats(start, end);
		} else {
			stats = hasUris
					? repository.findStatsByUris(start, end, uris)
					: repository.findStats(start, end);
		}
		return stats.stream().map(StatsMapper::toDto).toList();
	}
}
