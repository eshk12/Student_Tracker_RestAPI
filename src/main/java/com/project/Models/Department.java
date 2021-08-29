package com.project.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Department extends BaseEntitie{
    private String name;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Institute instituteObject;

    public Department(int id, String name, Institute instituteObject, boolean deleted) {
        super(id, deleted);
        this.name = name;
        this.instituteObject = instituteObject;
    }

    public Department(String name, Institute instituteObject, boolean deleted) {
        super(deleted);
        this.name = name;
        this.instituteObject = instituteObject;
    }

    public Department(String name, Institute instituteObject) {
        this.name = name;
        this.instituteObject = instituteObject;
    }
    public Department(){}

    public boolean objectIsEmpty() {
        if (
                isEmpty(this.name)
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

    public Institute getInstituteObject() {
        return instituteObject;
    }

    public void setInstituteObject(Institute instituteObject) {
        this.instituteObject = instituteObject;
    }

    @Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", instituteObject=" + instituteObject +
                '}';
    }
}
