package com.todolist.app.managers;

import com.todolist.app.models.TaskItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskManager {
    private static TaskManager instance;
    private List<TaskItem> allTasks;
    private List<TaskItem> todayTasks;
    private List<TaskItem> habitTasks;
    private List<TaskItem> planningTasks;

    private TaskManager() {
        allTasks = new ArrayList<>();
        todayTasks = new ArrayList<>();
        habitTasks = new ArrayList<>();
        planningTasks = new ArrayList<>();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public void addTask(TaskItem task) {
        allTasks.add(task);
        categorizeTask(task);
    }

    private void categorizeTask(TaskItem task) {
        // Categorize tasks
        switch (task.getType()) {
            case TODO:
            case DAILY_ACTIVITY:
                if (isToday(task.getCreatedDate())) {
                    todayTasks.add(task);
                }
                break;
            case HABIT:
                habitTasks.add(task);
                break;
            case PLANNING:
                planningTasks.add(task);
                break;
        }
    }

    public void removeTask(TaskItem task) {
        allTasks.remove(task);
        todayTasks.remove(task);
        habitTasks.remove(task);
        planningTasks.remove(task);
    }

    public void updateTask(TaskItem task) {
        // This method ensures the task is properly updated in all lists
        if (!allTasks.contains(task)) {
            allTasks.add(task);
        }

        // Re-categorize after update
        todayTasks.remove(task);
        habitTasks.remove(task);
        planningTasks.remove(task);
        categorizeTask(task);
    }

    public List<TaskItem> getTasksByDate(Date date) {
        List<TaskItem> tasks = new ArrayList<>();

        // Include tasks from GameManager history as well
        GameManager gameManager = GameManager.getInstance();
        List<TaskItem> allHistoryTasks = new ArrayList<>(allTasks);
        allHistoryTasks.addAll(gameManager.getTaskHistory());

        for (TaskItem task : allHistoryTasks) {
            if (isSameDay(task.getCreatedDate(), date) ||
                (task.getDueDate() != null && isSameDay(task.getDueDate(), date))) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<TaskItem> getCompletedTasksByDate(Date date) {
        List<TaskItem> completedTasks = new ArrayList<>();

        // Include tasks from GameManager history
        GameManager gameManager = GameManager.getInstance();
        List<TaskItem> allHistoryTasks = new ArrayList<>(allTasks);
        allHistoryTasks.addAll(gameManager.getTaskHistory());

        for (TaskItem task : allHistoryTasks) {
            if (task.getStatus() == TaskItem.TaskStatus.COMPLETED &&
                task.getCompletedDate() != null &&
                isSameDay(task.getCompletedDate(), date)) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    public List<TaskItem> getHabitStreak(String habitTitle) {
        List<TaskItem> streak = new ArrayList<>();
        for (TaskItem task : habitTasks) {
            if (task.getTitle().equals(habitTitle) &&
                task.getStatus() == TaskItem.TaskStatus.COMPLETED) {
                streak.add(task);
            }
        }
        return streak;
    }

    private boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Getters
    public List<TaskItem> getAllTasks() { return allTasks; }
    public List<TaskItem> getTodayTasks() { return todayTasks; }
    public List<TaskItem> getHabitTasks() { return habitTasks; }
    public List<TaskItem> getPlanningTasks() { return planningTasks; }
}
