package com.portfolio.smarttimecard.repository;

import com.portfolio.smarttimecard.entity.TimeCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface TimeCardRepository
        extends JpaRepository<TimeCard, Long>{
    Optional<TimeCard> findByWorkDate(LocalDate workDate);
    Optional<TimeCard> findTopByOrderByIdDesc();
}
