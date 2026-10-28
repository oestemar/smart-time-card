package com.portfolio.smarttimecard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class TimeCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String workType;
    private Integer breakTime;

    public TimeCard(){

    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public LocalDate getWorkDate(){
        return workDate;
    }

    public void setWorkDate(LocalDate workDate){
        this.workDate = workDate;
    }

    public LocalTime getStartTime(){
        return startTime;
    }

    public void setStartTime(LocalTime startTime){
        this.startTime = startTime;
    }

    public LocalTime getEndTime(){
        return endTime;
    }

    public void setEndTime(LocalTime endTime){
        this.endTime = endTime;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType){
        this.workType = workType;
    }

    public Integer getBreakTime(){
        return breakTime;
    }

    public void setBreakTime(Integer breakTime){
        this.breakTime = breakTime;
    }
}
