package com.project.Models;


public class Institute extends BaseEntitie {
    private String name;
    private String contactName;
    private String phoneNumber;
    private String city;
    private String street;
    private String number;

    public Institute(int id, String name, String contactName, String phoneNumber, String city, String street, String number, boolean deleted) {
        super(id, deleted);
        this.name = name;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.street = street;
        this.number = number;
    }

    public Institute(String name, String contactName, String phoneNumber, String city, String street, String number, boolean deleted) {
        super(deleted);
        this.name = name;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.street = street;
        this.number = number;
    }

    public Institute(String name, String contactName, String phoneNumber, String city, String street, String number) {
        this.name = name;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.street = street;
        this.number = number;
    }

    public Institute() {}

    public boolean objectIsEmpty() {
        if (
                isEmpty(this.name) ||
                        isEmpty(this.contactName) ||
                        isEmpty(this.phoneNumber) ||
                        isEmpty(this.city) ||
                        isEmpty(this.street) ||
                        isEmpty(this.number)
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Institute{" +
                "name='" + name + '\'' +
                ", contactName='" + contactName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
