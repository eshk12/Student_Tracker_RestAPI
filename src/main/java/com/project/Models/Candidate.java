package com.project.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Candidate extends BaseEntitie{
    private String uid; //V
    private String eventDate;
    private String scheduleDate;
    private String candidateName; //V
    private String comment; //V
    private String registerationState;
    private String candidateStatus;


    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Event eventObject;

    public Candidate(int id, String uid, String eventDate, String scheduleDate, String candidateName, String comment, String registerationState, String candidateStatus, Event eventObject, boolean deleted) {
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

    public Candidate(String uid, String eventDate, String scheduleDate, String candidateName, String comment, String registerationState, String candidateStatus, Event eventObject, boolean deleted) {
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

    public Candidate(String uid, String eventDate, String scheduleDate, String candidateName, String comment, String registerationState, String candidateStatus, Event eventObject) {
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

    public String getRegisterationState() {
        return registerationState;
    }

    public void setRegisterationState(String registerationState) {
        this.registerationState = registerationState;
    }

    public String getCandidateStatus() {
        return candidateStatus;
    }

    public void setCandidateStatus(String candidateStatus) {
        this.candidateStatus = candidateStatus;
    }

    public Event getEventObject() {
        return eventObject;
    }

    public void setEventObject(Event eventObject) {
        this.eventObject = eventObject;
    }
}
