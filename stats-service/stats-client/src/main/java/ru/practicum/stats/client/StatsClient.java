package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.Constants;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class StatsClient {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_PATTERN);

	private final RestTemplate rest;

	public StatsClient(@Value("${stats-server.url:http://localhost:9090}") String serverUrl,
					   RestTemplateBuilder builder) {
		this.rest = builder
				.uriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(serverUrl))
				.build();
	}

	public void hit(EndpointHitDto hit) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<EndpointHitDto> entity = new HttpEntity<>(hit, headers);
		rest.exchange("/hit", HttpMethod.POST, entity, Object.class);
	}

	public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/stats")
				.queryParam("start", start.format(FORMATTER))
				.queryParam("end", end.format(FORMATTER))
				.queryParam("unique", unique);
		if (uris != null && !uris.isEmpty()) {
			builder.queryParam("uris", uris.toArray());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<ViewStatsDto[]> response = rest.exchange(
				builder.build().toUriString(),
				HttpMethod.GET,
				entity,
				ViewStatsDto[].class);
		ViewStatsDto[] body = response.getBody();
		return body == null ? Collections.emptyList() : List.of(body);
	}
}
