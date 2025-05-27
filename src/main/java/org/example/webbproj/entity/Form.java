package org.example.webbproj.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Form {
    private final Long id;

    private final Long userId;

    private Date birthday;

    private String gender;

    private String fullName;

    private String phone;

    private String email;

    private String taskDescription;

    private List<Long> languagesId;

    public Form(Long id, Long userId,
                String fullName,
                String phone,
                String email,
                String taskDescription, Date birthday, String gender) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.taskDescription = taskDescription;
        this.birthday = birthday;
        this.gender = gender;
        this.languagesId = null;
    }

    @JsonCreator
    public Form(@JsonProperty("fullName") String fullName,
                @JsonProperty("phone") String phone,
                @JsonProperty("email") String email,
                @JsonProperty("taskDescription") String taskDescription,
                @JsonProperty("birthday") Date birthday,
                @JsonProperty("gender") String gender,
                @JsonProperty("languagesId") List<Long> languagesId) {
        this.id = null;
        this.userId = null;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.taskDescription = taskDescription;
        this.birthday = birthday;
        this.gender = gender;
        this.languagesId = languagesId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {return userId; }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Long> getLanguagesId() {
        return this.languagesId;
    }

    public void setLanguagesId(List<Long> languagesId) {
        this.languagesId = languagesId;
    }
}