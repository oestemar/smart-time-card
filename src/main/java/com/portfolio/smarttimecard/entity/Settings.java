package com.portfolio.smarttimecard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long defaultBreakMinutes;
    private Boolean startNoticeEnabled;
    private Boolean endNoticeEnabled;
    private LocalTime startScheduledTime;
    private LocalTime endScheduledTime;

    public Settings(){

    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getDefaultBreakMinutes(){
        return defaultBreakMinutes;
    }

    public void setDefaultBreakMinutes(Long defaultBreakMinutes){
        this.defaultBreakMinutes = defaultBreakMinutes;
    }

    public Boolean getStartNoticeEnabled(){
        return startNoticeEnabled;
    }

    public void setStartNoticeEnabled(Boolean startNoticeEnabled){
        this.startNoticeEnabled = startNoticeEnabled;
    }

    public Boolean getEndNoticeEnabled(){
        return endNoticeEnabled;
    }

    public void setEndNoticeEnabled(Boolean endNoticeEnabled){
        this.endNoticeEnabled = endNoticeEnabled;
    }

    public LocalTime getStartScheduledTime(){
        return startScheduledTime;
    }

    public void setStartScheduledTime(LocalTime startScheduledTime){
        this.startScheduledTime = startScheduledTime;
    }

    public LocalTime getEndScheduledTime(){
        return endScheduledTime;
    }

    public void setEndScheduledTime(LocalTime endScheduledTime){
        this.endScheduledTime = endScheduledTime;
    }
}
