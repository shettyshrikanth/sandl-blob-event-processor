package uk.gov.moj.cpp.sandl.persistence.entity;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CourtSchedule")
public class CourtSchedule {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "oucode")
    private String oucode;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "startTime")
    private ZonedDateTime startTime;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "endTime")
    private ZonedDateTime endTime;

    @Column(name = "max_slots")
    private Integer maxSlots;

    @Column(name = "canOverList")
    private Boolean canOverList;

    @Column(name = "isWelsh")
    private Boolean isWelsh;

    public CourtSchedule() {
    }

    public CourtSchedule(final String id, final String oucode, final LocalDate startDate, final ZonedDateTime startTime, final LocalDate endDate, final ZonedDateTime endTime, final Integer maxSlots, final Boolean canOverList, final Boolean isWelsh) {
        this.id = id;
        this.oucode = oucode;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.maxSlots = maxSlots;
        this.canOverList = canOverList;
        this.isWelsh = isWelsh;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOucode() {
        return oucode;
    }

    public void setOucode(final String oucode) {
        this.oucode = oucode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(final ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(final Integer maxSlots) {
        this.maxSlots = maxSlots;
    }

    public Boolean getCanOverList() {
        return canOverList;
    }

    public void setCanOverList(final Boolean canOverList) {
        this.canOverList = canOverList;
    }

    public Boolean getWelsh() {
        return isWelsh;
    }

    public void setWelsh(final Boolean welsh) {
        isWelsh = welsh;
    }
}
