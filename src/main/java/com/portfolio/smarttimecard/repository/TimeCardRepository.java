package com.portfolio.smarttimecard.repository;

import com.portfolio.smarttimecard.entity.TimeCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

public interface TimeCardRepository
        extends JpaRepository<TimeCard, Long>{
    Optional<TimeCard> findByWorkDate(LocalDate workDate);
    Optional<TimeCard> findTopByOrderByIdDesc();
    List<TimeCard> findByWorkDateBetweenOrderByWorkDateDesc(
            LocalDate startDate,
            LocalDate endDate
    );
    @Query(value = """
        SELECT DISTINCT EXTRACT(YEAR FROM work_date)
        FROM time_card
        ORDER BY 1 DESC
        """, nativeQuery = true)
    List<Integer> findDistinctYears();

    List<TimeCard>
    findByWorkDateBetweenOrderByWorkDateAsc(
            LocalDate startDate,
            LocalDate endDate
    );

}
