package org.example.webbproj.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Form {
    private final Long id;

    private final Long userId;

    private String fullName;

    private String phone;

    private String email;

    private String taskDescription;

    public Form(Long id, Long userId,
                String fullName,
                String phone,
                String email,
                String taskDescription) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.taskDescription = taskDescription;
    }

    @JsonCreator
    public Form(@JsonProperty("fullName") String fullName,
                @JsonProperty("phone") String phone,
                @JsonProperty("email") String email,
                @JsonProperty("taskDescription") String taskDescription) {
        this.id = null;
        this.userId = null;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.taskDescription = taskDescription;
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
}