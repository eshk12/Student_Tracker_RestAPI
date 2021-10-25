package com.project.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Candidate extends BaseEntitie{
    private String uid; //V
    private String eventDate;
    private String scheduleDate; //v
    private String candidateName; //V
    private String email;
    private String phoneNumber;
    private String comment; //V
    private String registerationState; //V
    private String candidateStatus; //V


    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Invitation invitationObject;

    public Candidate(int id, String uid, String eventDate, String scheduleDate, String candidateName, String email, String phoneNumber, String comment, String registerationState, String candidateStatus, Invitation invitationObject, boolean deleted) {
        super(id, deleted);
        this.uid = uid;
        this.eventDate = eventDate;
        this.scheduleDate = scheduleDate;
        this.candidateName = candidateName;
        this.comment = comment;
        this.registerationState = registerationState;
        this.candidateStatus = candidateStatus;
        this.invitationObject = invitationObject;
    }

    public Candidate(String uid, String eventDate, String scheduleDate, String candidateName, String email, String phoneNumber, String comment, String registerationState, String candidateStatus, Invitation invitationObject, boolean deleted) {
        super(deleted);
        this.uid = uid;
        this.eventDate = eventDate;
        this.scheduleDate = scheduleDate;
        this.candidateName = candidateName;
        this.comment = comment;
        this.registerationState = registerationState;
        this.candidateStatus = candidateStatus;
        this.invitationObject = invitationObject;
    }

    public Candidate(String uid, String eventDate, String scheduleDate, String candidateName, String email, String phoneNumber, String comment, String registerationState, String candidateStatus, Invitation invitationObject) {
        this.uid = uid;
        this.eventDate = eventDate;
        this.scheduleDate = scheduleDate;
        this.candidateName = candidateName;
        this.comment = comment;
        this.registerationState = registerationState;
        this.candidateStatus = candidateStatus;
        this.invitationObject = invitationObject;
    }

    public Candidate(){}

    public boolean isValidObject(){
        return (this.uid == ("") ||
                this.candidateName.equals("") ||
                this.email.equals("") ||
                this.phoneNumber.equals("")
        ) ? false : true ;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Invitation getInvitationObject() {
        return invitationObject;
    }

    public void setInvitationObject(Invitation invitationObject) {
        this.invitationObject = invitationObject;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "uid='" + uid + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", scheduleDate='" + scheduleDate + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", comment='" + comment + '\'' +
                ", registerationState='" + registerationState + '\'' +
                ", candidateStatus='" + candidateStatus + '\'' +
                ", eventObject=" + invitationObject +
                '}';
    }
}
