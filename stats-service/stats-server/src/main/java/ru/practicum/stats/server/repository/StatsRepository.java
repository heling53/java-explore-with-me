package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
            SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(h.ip))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h.ip) DESC
            """)
    List<ViewStats> findStats(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(h.ip))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h.ip) DESC
            """)
    List<ViewStats> findStatsByUris(LocalDateTime start, LocalDateTime end, Collection<String> uris);

    @Query("""
            SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStats> findUniqueStats(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStats> findUniqueStatsByUris(LocalDateTime start, LocalDateTime end, Collection<String> uris);
}
