package com.todolist.app.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.todolist.app.models.TaskItem;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReminderManager {
    private static ReminderManager instance;
    private Context context;
    private Handler handler;
    private Map<String, Runnable> activeReminders;
    private TaskManager taskManager;
    private GameManager gameManager;
    private boolean isActive;

    private ReminderManager() {
        handler = new Handler(Looper.getMainLooper());
        activeReminders = new ConcurrentHashMap<>();
        isActive = true;
    }

    public static ReminderManager getInstance() {
        if (instance == null) {
            instance = new ReminderManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        this.taskManager = TaskManager.getInstance();
        this.gameManager = GameManager.getInstance();
        startReminderSystem();
    }

    private void startReminderSystem() {
        // Check for reminders every minute
        Runnable reminderChecker = new Runnable() {
            @Override
            public void run() {
                if (isActive) {
                    checkAllReminders();
                    updateOverdueStatus();
                    handler.postDelayed(this, 60000); // Check every minute
                }
            }
        };
        handler.post(reminderChecker);
    }

    public void scheduleReminder(TaskItem task) {
        if (task.getReminderTime() == null || !task.hasReminder()) return;

        cancelReminder(task.getId());

        long currentTime = System.currentTimeMillis();
        long reminderTime = task.getReminderTime().getTime();
        long delay = reminderTime - currentTime;

        if (delay > 0) {
            Runnable reminderRunnable = () -> showReminder(task);
            handler.postDelayed(reminderRunnable, delay);
            activeReminders.put(task.getId(), reminderRunnable);
        }
    }

    public void cancelReminder(String taskId) {
        Runnable reminder = activeReminders.remove(taskId);
        if (reminder != null) {
            handler.removeCallbacks(reminder);
        }
    }

    private void checkAllReminders() {
        if (taskManager == null) return;

        List<TaskItem> allTasks = taskManager.getAllTasks();
        Date currentTime = new Date();

        for (TaskItem task : allTasks) {
            if (task.getStatus() != TaskItem.TaskStatus.PENDING) continue;

            // Check for due date reminders
            if (task.getDueDate() != null) {
                checkDueDateReminders(task, currentTime);
            }

            // Check for custom reminders
            if (task.hasReminder() && task.getReminderTime() != null) {
                checkCustomReminder(task, currentTime);
            }

            // Check for recurring reminders
            if (task.isRecurring()) {
                checkRecurringReminder(task, currentTime);
            }
        }
    }

    private void checkDueDateReminders(TaskItem task, Date currentTime) {
        if (task.getDueDate() == null) return;

        long timeUntilDue = task.getDueDate().getTime() - currentTime.getTime();
        long hours = timeUntilDue / (1000 * 60 * 60);

        // Remind at different intervals based on urgency
        if (hours == 24) {
            showUrgencyReminder(task, "📅 Due Tomorrow!", "Your quest '" + task.getTitle() + "' is due tomorrow!");
        } else if (hours == 2) {
            showUrgencyReminder(task, "⚠️ Due Soon!", "Your quest '" + task.getTitle() + "' is due in 2 hours!");
        } else if (hours == 0 && timeUntilDue > 0 && timeUntilDue <= 60 * 60 * 1000) {
            showUrgencyReminder(task, "🔥 Due Now!", "Your quest '" + task.getTitle() + "' is due within an hour!");
        } else if (timeUntilDue < 0) {
            showUrgencyReminder(task, "💀 Overdue!", "Quest '" + task.getTitle() + "' is overdue! Your character is in danger!");
        }
    }

    private void checkCustomReminder(TaskItem task, Date currentTime) {
        long timeDiff = Math.abs(task.getReminderTime().getTime() - currentTime.getTime());

        // If within 1 minute of reminder time
        if (timeDiff <= 60000) {
            showReminder(task);
            task.setHasReminder(false); // Mark as shown
        }
    }

    private void checkRecurringReminder(TaskItem task, Date currentTime) {
        if (task.getType() == TaskItem.TaskType.DAILY_ACTIVITY) {
            // Daily reminder at 9 AM
            Calendar cal = Calendar.getInstance();
            if (cal.get(Calendar.HOUR_OF_DAY) == 9 && cal.get(Calendar.MINUTE) == 0) {
                showRecurringReminder(task, "🌅 Daily Quest Available!",
                    "Time for your daily quest: " + task.getTitle());
            }
        } else if (task.getType() == TaskItem.TaskType.HABIT) {
            // Habit reminder every few hours
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if ((hour == 10 || hour == 14 || hour == 18) && cal.get(Calendar.MINUTE) == 0) {
                showRecurringReminder(task, "🔄 Habit Check!",
                    "Don't forget your habit: " + task.getTitle());
            }
        }
    }

    private void updateOverdueStatus() {
        if (taskManager == null) return;

        List<TaskItem> allTasks = taskManager.getAllTasks();
        for (TaskItem task : allTasks) {
            task.updateOverdueStatus();

            if (task.isOverdue() && task.getStatus() == TaskItem.TaskStatus.PENDING) {
                // Apply overdue penalty
                applyOverduePenalty(task);
            }
        }
    }

    private void applyOverduePenalty(TaskItem task) {
        if (gameManager != null) {
            // Increase penalty for overdue tasks
            int overduePenalty = task.getCoinPenalty() / 2;
            // Apply gradual penalty but don't complete the task
            showUrgencyReminder(task, "⚡ Overdue Penalty!",
                "Lost " + overduePenalty + " coins for overdue quest: " + task.getTitle());
        }
    }

    private void showReminder(TaskItem task) {
        if (context == null) return;

        String title = "🔔 Quest Reminder!";
        String message = "Time to work on: " + task.getTitle();

        if (task.getDueDate() != null) {
            message += "\nDue: " + task.getTimeUntilDue();
        }

        showNotification(title, message, task);
    }

    private void showUrgencyReminder(TaskItem task, String title, String message) {
        if (context == null) return;
        showNotification(title, message, task);
    }

    private void showRecurringReminder(TaskItem task, String title, String message) {
        if (context == null) return;
        showNotification(title, message, task);
    }

    private void showNotification(String title, String message, TaskItem task) {
        // Show toast notification (in a real app, you'd use proper notifications)
        handler.post(() -> {
            if (context != null) {
                String fullMessage = title + "\n" + message;
                Toast.makeText(context, fullMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Log reminder for debugging
        System.out.println("REMINDER: " + title + " - " + message);
    }

    public List<TaskItem> getUpcomingTasks(int hoursAhead) {
        List<TaskItem> upcomingTasks = new ArrayList<>();
        if (taskManager == null) return upcomingTasks;

        long currentTime = System.currentTimeMillis();
        long timeAhead = hoursAhead * 60 * 60 * 1000;

        for (TaskItem task : taskManager.getAllTasks()) {
            if (task.getStatus() != TaskItem.TaskStatus.PENDING) continue;

            if (task.getDueDate() != null) {
                long timeToDue = task.getDueDate().getTime() - currentTime;
                if (timeToDue > 0 && timeToDue <= timeAhead) {
                    upcomingTasks.add(task);
                }
            }
        }

        // Sort by due date
        upcomingTasks.sort((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()));
        return upcomingTasks;
    }

    public List<TaskItem> getOverdueTasks() {
        List<TaskItem> overdueTasks = new ArrayList<>();
        if (taskManager == null) return overdueTasks;

        for (TaskItem task : taskManager.getAllTasks()) {
            if (task.getStatus() == TaskItem.TaskStatus.PENDING && task.isOverdue()) {
                overdueTasks.add(task);
            }
        }

        return overdueTasks;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void shutdown() {
        isActive = false;
        handler.removeCallbacksAndMessages(null);
        activeReminders.clear();
    }
}
