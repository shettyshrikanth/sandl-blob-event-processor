package uk.gov.moj.cpp.sandl.entity;

import java.time.LocalDate;

public class Assignment {
    private String id;
    private String justiceAreaId;
    private String justiceAreaType;
    private String title;
    private String surname;
    private String forenames;
    private String ljaId;
    private String sittingLocationId;
    private LocalDate rotaScheduleStartDate;
    private LocalDate rotaScheduleEndDate;
    private LocalDate dobDate;
    private LocalDate startDate;


    public Assignment () {
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getJusticeAreaId() {
        return justiceAreaId;
    }

    public void setJusticeAreaId(final String justiceAreaId) {
        this.justiceAreaId = justiceAreaId;
    }

    public String getJusticeAreaType() {
        return justiceAreaType;
    }

    public void setJusticeAreaType(final String justiceAreaType) {
        this.justiceAreaType = justiceAreaType;
    }

    public LocalDate getRotaScheduleStartDate() {
        return rotaScheduleStartDate;
    }

    public void setRotaScheduleStartDate(final LocalDate rotaScheduleStartDate) {
        this.rotaScheduleStartDate = rotaScheduleStartDate;
    }

    public LocalDate getRotaScheduleEndDate() {
        return rotaScheduleEndDate;
    }

    public void setRotaScheduleEndDate(final LocalDate rotaScheduleEndDate) {
        this.rotaScheduleEndDate = rotaScheduleEndDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getForenames() {
        return forenames;
    }

    public void setForenames(final String forenames) {
        this.forenames = forenames;
    }

    public LocalDate getDobDate() {
        return dobDate;
    }

    public void setDobDate(final LocalDate dobDate) {
        this.dobDate = dobDate;
    }

    public String getLjaId() {
        return ljaId;
    }

    public void setLjaId(final String ljaId) {
        this.ljaId = ljaId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getSittingLocationId() {
        return sittingLocationId;
    }

    public void setSittingLocationId(final String sittingLocationId) {
        this.sittingLocationId = sittingLocationId;
    }
}
