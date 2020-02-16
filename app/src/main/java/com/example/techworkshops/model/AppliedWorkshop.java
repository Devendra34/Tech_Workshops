package com.example.techworkshops.model;

public class AppliedWorkshop {
    private String student_email;
    private String workshop_id;

    public AppliedWorkshop() {
    }

    public AppliedWorkshop(String student_email, String workshop_id) {
        this.student_email = student_email;
        this.workshop_id = workshop_id;
    }

    public String getWorkshop_id() {
        return workshop_id;
    }

}
