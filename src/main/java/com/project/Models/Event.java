package com.project.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Event extends BaseEntitie{
    private String name;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department departmentObject;

    public Event(int id, String name, Department departmentObject, boolean deleted) {
        super(id, deleted);
        this.name = name;
        this.departmentObject = departmentObject;
    }

    public Event(String name, Department departmentObject, boolean deleted) {
        super(deleted);
        this.name = name;
        this.departmentObject = departmentObject;
    }

    public Event(String name, Department departmentObject) {
        this.name = name;
        this.departmentObject = departmentObject;
    }

    public Event() {}

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

    public Department getDepartmentObject() {
        return departmentObject;
    }

    public void setDepartmentObject(Department departmentObject) {
        this.departmentObject = departmentObject;
    }
}
