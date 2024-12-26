package com.example.navigation;

public class Appointment {
    private final int id;
    private final int doctorid;
    private final String date;
    private final String time;
    private final String doctorName;
    private final String specialization;

    public Appointment(int id, int doctorid, String date, String time, String doctorName, String specialization) {
        this.id = id;
        this.doctorid = doctorid;
        this.date = date;
        this.time = time;
        this.doctorName = doctorName;
        this.specialization = specialization;
    }

    public int getId() {
        return id;
    }

    public int getdoctorId() {
        return doctorid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }
}

