package com.project.Objects.Entities;

public class FileResponse {
    private String fileLocation;
    private String errorMessage;
    private Boolean isFailed;
    private Object results;

    public FileResponse(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public FileResponse(Boolean isFailed, String errorMessage) {
        this.isFailed = isFailed;
        this.errorMessage = errorMessage;
    }

    public FileResponse(String fileLocation, Object results) {
        this.fileLocation = fileLocation;
        this.results = results;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getFailed() {
        return isFailed;
    }

    public void setFailed(Boolean failed) {
        isFailed = failed;
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "FileResponse{" +
                "fileLocation='" + fileLocation + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", isFailed=" + isFailed +
                ", results=" + results +
                '}';
    }
}
