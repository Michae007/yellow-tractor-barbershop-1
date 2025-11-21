package com.yellowtractor.barbershop;

import java.util.Date;

public class Booking {
    private String date;
    private String time;
    private String clientName;
    private String clientAge;
    private String parentName;
    private String phone;
    private String service;
    private String notes;
    private String status;
    private Date createdAt;

    // Конструктор
    public Booking() {}

    // Геттеры и сеттеры
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientAge() { return clientAge; }
    public void setClientAge(String clientAge) { this.clientAge = clientAge; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // Вспомогательные методы
    public String getDisplayDate() {
        if (date == null) return "";
        try {
            // Преобразование из формата YYYY-MM-DD в DD.MM.YYYY
            String[] parts = date.split("-");
            if (parts.length == 3) {
                return parts[2] + "." + parts[1] + "." + parts[0];
            }
            return date;
        } catch (Exception e) {
            return date;
        }
    }
    
    public String getDisplayInfo() {
        return clientName + " (" + clientAge + " лет) - " + service;
    }
}
