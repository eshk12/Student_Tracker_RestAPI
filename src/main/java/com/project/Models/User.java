package com.project.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.Utils.Definitions;
import com.project.Utils.Permissions;
import org.springframework.beans.factory.annotation.Autowired;

public class User extends BaseEntitie {

    @Autowired private Definitions definitions;
    @Autowired private Permissions permissions;

    private String uid;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private String password;
    private String email;
    private String phone;
    private String token;
    private int permission;



    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department departmentObject;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Institute instituteObject;

    public User(String uid, String firstName, String lastName, String password, String email, String phone, String token, int permission, Department departmentObject, Institute instituteObject, boolean deleted) {
        super(deleted);
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.token = token;
        this.permission = permission;
        this.departmentObject = departmentObject;
        this.instituteObject = instituteObject;
    }

    public User() {
        super();
    }

    public User(int id, String uid, String firstName, String lastName, String password, String email, String phone, String token, int permission, Department departmentObject, Institute instituteObject, boolean deleted) {
        super(id, deleted);
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.token = token;
        this.permission = permission;
        this.departmentObject = departmentObject;
        this.instituteObject = instituteObject;
    }

    public User(int id, String uid, String firstName, String lastName, String phone) {
        super(id);
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
    public boolean objectIsEmpty() {
        if (
            isEmpty(this.uid) ||
            isEmpty(this.firstName) ||
            isEmpty(this.lastName) ||
            isEmpty(this.password) ||
            isEmpty(this.phone)
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public Department getDepartmentObject() {
        return departmentObject;
    }

    public void setDepartmentObject(Department departmentObject) {
        this.departmentObject = departmentObject;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Institute getInstituteObject() {
        return instituteObject;
    }

    public void setInstituteObject(Institute instituteObject) {
        this.instituteObject = instituteObject;
    }

    @Override
    public String toString() {
        return "User{" +
                ", uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", permission=" + permission +
                ", departmentObject=" + departmentObject +
                ", instituteObject=" + instituteObject +
                '}';
    }
}
