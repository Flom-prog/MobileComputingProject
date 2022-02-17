package com.flom.mobilecomputingproject.model;

public class Reminder {

    private int reminder_id, creator_id;
    private String message, image_reminder, reminder_time, creation_time;
    private double location_x, location_y;
    private String reminder_seen;

    public Reminder() {
    }

    public Reminder(int reminder_id, String message, String image_reminder, String reminder_time, String creation_time, String reminder_seen) {
        this.reminder_id = reminder_id;
        this.message = message;
        this.image_reminder = image_reminder;
        this.reminder_time = reminder_time;
        this.creation_time = creation_time;
        this.reminder_seen = reminder_seen;
    }

    public Reminder(int creator_id, String message, String reminder_time, String creation_time, double location_x, double location_y, String reminder_seen) {
        this.creator_id = creator_id;
        this.message = message;
        this.reminder_time = reminder_time;
        this.creation_time = creation_time;
        this.location_x = location_x;
        this.location_y = location_y;
        this.reminder_seen = reminder_seen;
    }

    public int getReminder_id() {
        return reminder_id;
    }

    public void setReminder_id(int reminder_id) {
        this.reminder_id = reminder_id;
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

    public String getImage_reminder() {
        return image_reminder;
    }

    public void setImage_reminder(String image_reminder) {
        this.image_reminder = image_reminder;
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

    public String getReminder_seen() {
        return reminder_seen;
    }

    public void setReminder_seen(String reminder_seen) {
        this.reminder_seen = reminder_seen;
    }
}
