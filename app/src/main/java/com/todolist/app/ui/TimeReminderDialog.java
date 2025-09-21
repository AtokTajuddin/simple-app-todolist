package com.todolist.app.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.Toast;
import com.todolist.app.models.TaskItem;
import java.util.Calendar;
import java.util.Date;

public class TimeReminderDialog {

    public interface OnTimeSetListener {
        void onDueDateSet(Date dueDate);
        void onReminderSet(Date reminderTime);
        void onPrioritySet(TaskItem.Priority priority);
    }

    public static void showDueDatePicker(Context context, OnTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // After date is picked, show time picker
                showTimePicker(context, calendar, listener::onDueDateSet);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setTitle("📅 Set Due Date");
        datePickerDialog.show();
    }

    public static void showReminderPicker(Context context, OnTimeSetListener listener) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // After date is picked, show time picker
                showTimePicker(context, calendar, listener::onReminderSet);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setTitle("🔔 Set Reminder Time");
        datePickerDialog.show();
    }

    private static void showTimePicker(Context context, Calendar calendar, TimeSetCallback callback) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            context,
            (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                Date selectedTime = calendar.getTime();

                // Check if the selected time is in the future
                if (selectedTime.before(new Date())) {
                    Toast.makeText(context, "⚠️ Please select a future time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                callback.onTimeSet(selectedTime);
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Use 24-hour format
        );

        timePickerDialog.setTitle("⏰ Select Time");
        timePickerDialog.show();
    }

    public static void showPriorityPicker(Context context, OnTimeSetListener listener) {
        String[] priorities = {"🟢 Low Priority", "🟡 Medium Priority", "🟠 High Priority", "🔴 Urgent!"};
        TaskItem.Priority[] priorityValues = {
            TaskItem.Priority.LOW,
            TaskItem.Priority.MEDIUM,
            TaskItem.Priority.HIGH,
            TaskItem.Priority.URGENT
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("🎯 Set Priority Level");
        builder.setItems(priorities, (dialog, which) -> {
            listener.onPrioritySet(priorityValues[which]);
        });
        builder.show();
    }

    private interface TimeSetCallback {
        void onTimeSet(Date time);
    }
}
