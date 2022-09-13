package com.bitc.eatnow.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsManager;

    public User() {
    }

    // Init User(일반 사용자로 지정)
    public User(String name, String password) {
        Name = name;
        Password = password;
        IsManager = "false";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsManager() {
        return IsManager;
    }

    public void setIsManager(String isManager) {
        IsManager = isManager;
    }
}
