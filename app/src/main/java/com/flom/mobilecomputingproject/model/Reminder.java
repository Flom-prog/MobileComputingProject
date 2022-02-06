package com.flom.mobilecomputingproject.model;

public class Reminder {

    private int creator_id;
    private String message, reminder_time, creation_time;
    private double location_x, location_y;
    private boolean reminder_seen;

    public Reminder() {
    }

    public Reminder(String message, String reminder_time, String creation_time) {
        this.message = message;
        this.reminder_time = reminder_time;
        this.creation_time = creation_time;
    }

    public Reminder(int creator_id, String message, String reminder_time, String creation_time, double location_x, double location_y, boolean reminder_seen) {
        this.creator_id = creator_id;
        this.message = message;
        this.reminder_time = reminder_time;
        this.creation_time = creation_time;
        this.location_x = location_x;
        this.location_y = location_y;
        this.reminder_seen = reminder_seen;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReminder_time() {
        return reminder_time;
    }

    public void setReminder_time(String reminder_time) {
        this.reminder_time = reminder_time;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public double getLocation_x() {
        return location_x;
    }

    public void setLocation_x(double location_x) {
        this.location_x = location_x;
    }

    public double getLocation_y() {
        return location_y;
    }

    public void setLocation_y(double location_y) {
        this.location_y = location_y;
    }

    public boolean isReminder_seen() {
        return reminder_seen;
    }

    public void setReminder_seen(boolean reminder_seen) {
        this.reminder_seen = reminder_seen;
    }
}