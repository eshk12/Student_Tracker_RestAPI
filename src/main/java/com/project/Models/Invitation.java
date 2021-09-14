package com.project.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Invitation extends BaseEntitie{
    private String name;
    private String studyYear;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department departmentObject;

    public Invitation(int id, String name, String studyYear, Department departmentObject, boolean deleted) {
        super(id, deleted);
        this.name = name;
        this.studyYear = studyYear;
        this.departmentObject = departmentObject;
    }

    public Invitation(String name, String studyYear, Department departmentObject, boolean deleted) {
        super(deleted);
        this.name = name;
        this.studyYear = studyYear;
        this.departmentObject = departmentObject;
    }

    public Invitation(String name, String studyYear, Department departmentObject) {
        this.name = name;
        this.studyYear = studyYear;
        this.departmentObject = departmentObject;
    }

    public Invitation() {}

    public boolean objectIsEmpty() {
        if (
                isEmpty(this.name) || isEmpty(studyYear)
        ) {
            return true;
        }
        return false;
    }
    public boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(String studyYear) {
        this.studyYear = studyYear;
    }

    public Department getDepartmentObject() {
        return departmentObject;
    }

    public void setDepartmentObject(Department departmentObject) {
        this.departmentObject = departmentObject;
    }
}
