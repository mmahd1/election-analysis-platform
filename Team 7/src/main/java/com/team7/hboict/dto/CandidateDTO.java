package com.team7.hboict.dto;

public class CandidateDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private int votes;
    private String shortCode;

    public CandidateDTO() {
    }

    public CandidateDTO(Long id, String firstName, String lastName, String gender, int votes, String shortCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.votes = votes;
        this.shortCode = shortCode;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public int getVotes() {
        return votes;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}
