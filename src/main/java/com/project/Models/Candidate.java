package com.project.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Candidate extends BaseEntitie{
    private String uid;
    private String eventDate;
    private String scheduleDate;
    private String candidateName;
    private String comment;
    private int registerationState;
    private int candidateStatus;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Event eventObject;

    public Candidate(int id, String uid, String eventDate, String scheduleDate, String candidateName, String comment, int registerationState, int candidateStatus, Event eventObject, boolean deleted) {
        super(id, deleted);
        this.uid = uid;
        this.eventDate = eventDate;
        this.scheduleDate = scheduleDate;
        this.candidateName = candidateName;
        this.comment = comment;
        this.registerationState = registerationState;
        this.candidateStatus = candidateStatus;
        this.eventObject = eventObject;
    }

    public Candidate(String uid, String eventDate, String scheduleDate, String candidateName, String comment, int registerationState, int candidateStatus, Event eventObject, boolean deleted) {
        super(deleted);
        this.uid = uid;
        this.eventDate = eventDate;
        this.scheduleDate = scheduleDate;
        this.candidateName = candidateName;
        this.comment = comment;
        this.registerationState = registerationState;
        this.candidateStatus = candidateStatus;
        this.eventObject = eventObject;
    }

    public Candidate(String uid, String eventDate, String scheduleDate, String candidateName, String comment, int registerationState, int candidateStatus, Event eventObject) {
        this.uid = uid;
        this.eventDate = eventDate;
        this.scheduleDate = scheduleDate;
        this.candidateName = candidateName;
        this.comment = comment;
        this.registerationState = registerationState;
        this.candidateStatus = candidateStatus;
        this.eventObject = eventObject;
    }

    public Candidate(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRegisterationState() {
        return registerationState;
    }

    public void setRegisterationState(int registerationState) {
        this.registerationState = registerationState;
    }

    public int getCandidateStatus() {
        return candidateStatus;
    }

    public void setCandidateStatus(int candidateStatus) {
        this.candidateStatus = candidateStatus;
    }

    public Event getEventObject() {
        return eventObject;
    }

    public void setEventObject(Event eventObject) {
        this.eventObject = eventObject;
    }
}
