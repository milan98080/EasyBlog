package com.example.easyblog;

public class postdataholder {
    String full_name , email, title, date , description , pimage;

    public postdataholder(String full_name, String email, String title, String date, String description, String pimage) {
        this.full_name = full_name;
        this.email = email;
        if(title.length()==0){title = " ";}
        this.title = title;
        this.date = date;
        if(description.length()==0){description = " ";}
        this.description = description;
        this.pimage = pimage;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }
}
