package com.project.Objects.Entities;

public class CandidatesState {
    private String candidateName;
    private boolean isFailed;

    public CandidatesState(String candidateName, boolean isFailed) {
        this.candidateName = candidateName;
        this.isFailed = isFailed;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }
}
