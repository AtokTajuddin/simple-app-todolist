package com.todolist.app.models;

import java.util.Date;

public class TaskItem {
    public enum TaskType {
        TODO, HABIT, PLANNING, DAILY_ACTIVITY
    }

    public enum TaskStatus {
        PENDING, COMPLETED, FAILED, SKIPPED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    private String id;
    private String title;
    private String description;
    private TaskType type;
    private TaskStatus status;
    private Priority priority;
    private Date createdDate;
    private Date completedDate;
    private Date dueDate;
    private Date reminderTime;
    private boolean hasReminder;
    private boolean isRecurring;
    private int recurringDays; // For habits - repeat every X days
    private int coinReward;
    private int coinPenalty;
    private int streakCount;
    private boolean isOverdue;

    public TaskItem(String title, TaskType type) {
        this.id = System.currentTimeMillis() + "_" + Math.random();
        this.title = title;
        this.type = type;
        this.status = TaskStatus.PENDING;
        this.priority = Priority.MEDIUM;
        this.createdDate = new Date();
        this.coinReward = getCoinRewardByType(type);
        this.coinPenalty = getCoinPenaltyByType(type);
        this.streakCount = 0;
        this.hasReminder = false;
        this.isRecurring = (type == TaskType.HABIT || type == TaskType.DAILY_ACTIVITY);
        this.recurringDays = (type == TaskType.DAILY_ACTIVITY) ? 1 : 7; // Daily or weekly
        this.isOverdue = false;
    }

    private int getCoinRewardByType(TaskType type) {
        switch (type) {
            case TODO: return 10;
            case HABIT: return 15;
            case PLANNING: return 20;
            case DAILY_ACTIVITY: return 5;
            default: return 10;
        }
    }

    private int getCoinPenaltyByType(TaskType type) {
        switch (type) {
            case TODO: return 5;
            case HABIT: return 10;
            case PLANNING: return 15;
            case DAILY_ACTIVITY: return 3;
            default: return 5;
        }
    }

    public boolean isUrgent() {
        if (dueDate == null) return false;

        long currentTime = System.currentTimeMillis();
        long dueTime = dueDate.getTime();
        long timeDiff = dueTime - currentTime;

        // Consider urgent if due within 2 hours or overdue
        return timeDiff <= 2 * 60 * 60 * 1000 || isOverdue;
    }

    public boolean isDueToday() {
        if (dueDate == null) return false;

        long currentTime = System.currentTimeMillis();
        long dueTime = dueDate.getTime();
        long timeDiff = Math.abs(dueTime - currentTime);

        // Due today if within 24 hours
        return timeDiff <= 24 * 60 * 60 * 1000;
    }

    public void updateOverdueStatus() {
        if (dueDate != null && status == TaskStatus.PENDING) {
            this.isOverdue = new Date().after(dueDate);
        }
    }

    public String getTimeUntilDue() {
        if (dueDate == null) return "";

        long currentTime = System.currentTimeMillis();
        long dueTime = dueDate.getTime();
        long timeDiff = dueTime - currentTime;

        if (timeDiff < 0) {
            return "Overdue";
        }

        long hours = timeDiff / (60 * 60 * 1000);
        long minutes = (timeDiff % (60 * 60 * 1000)) / (60 * 1000);

        if (hours > 24) {
            long days = hours / 24;
            return days + " days";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + " minutes";
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskType getType() { return type; }
    public void setType(TaskType type) { this.type = type; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Date getCreatedDate() { return createdDate; }
    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getReminderTime() { return reminderTime; }
    public void setReminderTime(Date reminderTime) { this.reminderTime = reminderTime; }
    public boolean hasReminder() { return hasReminder; }
    public void setHasReminder(boolean hasReminder) { this.hasReminder = hasReminder; }
    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }
    public int getRecurringDays() { return recurringDays; }
    public void setRecurringDays(int recurringDays) { this.recurringDays = recurringDays; }
    public int getCoinReward() { return coinReward; }
    public int getCoinPenalty() { return coinPenalty; }
    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }
    public boolean isOverdue() { return isOverdue; }
    public void setOverdue(boolean overdue) { isOverdue = overdue; }
}
