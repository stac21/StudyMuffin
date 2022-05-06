package com.example.studymuffin;

public class Goal {
    private String name;

    private boolean completed;

    public Goal(String name) {
        this.name = name;

        this.completed = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
