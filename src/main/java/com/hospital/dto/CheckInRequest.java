package com.hospital.dto;

// what the kiosk sends when a patient checks in
public class CheckInRequest {

    private String fullName;
    private String idNumber;
    private String nurseCode;
    private String category;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getNurseCode() {
        return nurseCode;
    }

    public void setNurseCode(String nurseCode) {
        this.nurseCode = nurseCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
