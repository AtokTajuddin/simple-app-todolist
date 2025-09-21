package com.todolist.app.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.todolist.app.R;
import com.todolist.app.managers.TaskManager;
import com.todolist.app.models.TaskItem;
import com.todolist.app.ui.UIStyleHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarHistoryActivity extends AppCompatActivity {
    private TaskManager taskManager;
    private TextView selectedDateText;
    private LinearLayout tasksContainer;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;
    private TextView statsDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskManager = TaskManager.getInstance();
        selectedDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        createBeautifulCalendarUI();
        loadTasksForSelectedDate();
    }

    private void createBeautifulCalendarUI() {
        // Main container with background
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setBackgroundColor(UIStyleHelper.Colors.BACKGROUND);

        // Create header section
        createCalendarHeader(mainContainer);

        // Create date navigation section
        createDateNavigation(mainContainer);

        // Create stats section
        createStatsSection(mainContainer);

        // Create tasks display section
        createTasksSection(mainContainer);

        // Create back button
        createBackButton(mainContainer);

        setContentView(mainContainer);
    }

    private void createCalendarHeader(LinearLayout parent) {
        LinearLayout headerContainer = UIStyleHelper.createGameStatusBar(this);
        headerContainer.setPadding(20, 16, 20, 16);

        // Calendar title with icon
        LinearLayout titleSection = new LinearLayout(this);
        titleSection.setOrientation(LinearLayout.HORIZONTAL);
        titleSection.setGravity(Gravity.CENTER_VERTICAL);

        TextView calendarIcon = new TextView(this);
        calendarIcon.setText("📅");
        calendarIcon.setTextSize(28);
        calendarIcon.setPadding(0, 0, 16, 0);

        TextView calendarTitle = new TextView(this);
        calendarTitle.setText("Quest History");
        calendarTitle.setTextSize(24);
        calendarTitle.setTextColor(UIStyleHelper.Colors.WHITE);
        calendarTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        titleSection.addView(calendarIcon);
        titleSection.addView(calendarTitle);

        headerContainer.addView(titleSection);
        parent.addView(headerContainer);

        // Add subtitle
        LinearLayout subtitleContainer = new LinearLayout(this);
        subtitleContainer.setOrientation(LinearLayout.VERTICAL);
        subtitleContainer.setBackgroundColor(UIStyleHelper.Colors.WHITE);
        subtitleContainer.setPadding(20, 16, 20, 16);

        TextView subtitle = new TextView(this);
        UIStyleHelper.styleSubHeaderText(subtitle, "📊 Track your epic journey!");

        TextView description = new TextView(this);
        UIStyleHelper.styleBodyText(description, "View your completed quests, track progress, and see your achievement history.");

        subtitleContainer.addView(subtitle);
        subtitleContainer.addView(description);
        parent.addView(subtitleContainer);

        // Add separator
        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
        separator.setBackgroundColor(UIStyleHelper.Colors.DARK_GRAY);
        parent.addView(separator);
    }

    private void createDateNavigation(LinearLayout parent) {
        LinearLayout navContainer = UIStyleHelper.createTaskTypeContainer(this, UIStyleHelper.Colors.PRIMARY_BLUE);
        navContainer.setPadding(20, 16, 20, 16);

        // Today button
        Button todayButton = new Button(this);
        todayButton.setText("🏠 " + getString(R.string.today));
        UIStyleHelper.styleSuccessButton(todayButton);
        todayButton.setOnClickListener(v -> {
            selectedDate = Calendar.getInstance();
            updateDateDisplay();
            loadTasksForSelectedDate();
        });

        // Add spacing
        View spacing1 = new View(this);
        spacing1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16));

        // Date navigation row
        LinearLayout dateNavLayout = new LinearLayout(this);
        dateNavLayout.setOrientation(LinearLayout.HORIZONTAL);
        dateNavLayout.setGravity(Gravity.CENTER_VERTICAL);

        Button prevDayButton = new Button(this);
        prevDayButton.setText("⬅️ Previous");
        prevDayButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleSecondaryButton(prevDayButton);
        prevDayButton.setOnClickListener(v -> {
            selectedDate.add(Calendar.DAY_OF_MONTH, -1);
            updateDateDisplay();
            loadTasksForSelectedDate();
        });

        selectedDateText = new TextView(this);
        selectedDateText.setTextSize(18);
        selectedDateText.setTextColor(UIStyleHelper.Colors.PRIMARY_PURPLE);
        selectedDateText.setTypeface(null, android.graphics.Typeface.BOLD);
        selectedDateText.setGravity(Gravity.CENTER);
        selectedDateText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        selectedDateText.setBackground(UIStyleHelper.createRoundedBackground(
            UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.PRIMARY_PURPLE, 0.1f), 12
        ));
        selectedDateText.setPadding(16, 12, 16, 12);
        updateDateDisplay();

        Button nextDayButton = new Button(this);
        nextDayButton.setText("Next ➡️");
        nextDayButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleSecondaryButton(nextDayButton);
        nextDayButton.setOnClickListener(v -> {
            selectedDate.add(Calendar.DAY_OF_MONTH, 1);
            updateDateDisplay();
            loadTasksForSelectedDate();
        });

        // Add small spacing between buttons
        View smallSpacing1 = new View(this);
        smallSpacing1.setLayoutParams(new LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT));
        View smallSpacing2 = new View(this);
        smallSpacing2.setLayoutParams(new LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT));

        dateNavLayout.addView(prevDayButton);
        dateNavLayout.addView(smallSpacing1);
        dateNavLayout.addView(selectedDateText);
        dateNavLayout.addView(smallSpacing2);
        dateNavLayout.addView(nextDayButton);

        navContainer.addView(todayButton);
        navContainer.addView(spacing1);
        navContainer.addView(dateNavLayout);

        // Add margin to container
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 8);
        navContainer.setLayoutParams(params);

        parent.addView(navContainer);
    }

    private void createStatsSection(LinearLayout parent) {
        LinearLayout statsContainer = new LinearLayout(this);
        statsContainer.setOrientation(LinearLayout.HORIZONTAL);
        statsContainer.setBackgroundColor(UIStyleHelper.Colors.WHITE);
        statsContainer.setPadding(20, 16, 20, 16);

        statsDisplay = new TextView(this);
        statsDisplay.setTextSize(16);
        statsDisplay.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        statsDisplay.setTypeface(null, android.graphics.Typeface.BOLD);
        statsDisplay.setGravity(Gravity.CENTER);
        statsDisplay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        statsContainer.addView(statsDisplay);
        parent.addView(statsContainer);
    }

    private void createTasksSection(LinearLayout parent) {
        // Section header
        LinearLayout headerSection = new LinearLayout(this);
        headerSection.setOrientation(LinearLayout.HORIZONTAL);
        headerSection.setPadding(20, 16, 20, 8);
        headerSection.setBackgroundColor(UIStyleHelper.Colors.WHITE);

        TextView questHistoryTitle = new TextView(this);
        UIStyleHelper.styleHeaderText(questHistoryTitle, "⚔️ Quest History");

        headerSection.addView(questHistoryTitle);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        tasksContainer = new LinearLayout(this);
        tasksContainer.setOrientation(LinearLayout.VERTICAL);
        tasksContainer.setPadding(16, 8, 16, 16);

        scrollView.addView(tasksContainer);

        parent.addView(headerSection);
        parent.addView(scrollView);
    }

    private void createBackButton(LinearLayout parent) {
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setBackgroundColor(UIStyleHelper.Colors.WHITE);
        buttonContainer.setPadding(20, 16, 20, 16);

        Button backButton = new Button(this);
        backButton.setText("🏠 " + getString(R.string.back));
        backButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        UIStyleHelper.styleSecondaryButton(backButton);
        backButton.setOnClickListener(v -> finish());

        buttonContainer.addView(backButton);
        parent.addView(buttonContainer);
    }

    private void updateDateDisplay() {
        selectedDateText.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void loadTasksForSelectedDate() {
        tasksContainer.removeAllViews();

        Date date = selectedDate.getTime();
        List<TaskItem> tasksForDate = taskManager.getTasksByDate(date);
        List<TaskItem> completedTasks = taskManager.getCompletedTasksByDate(date);

        // Update stats display
        updateStatsDisplay(tasksForDate, completedTasks);

        if (tasksForDate.isEmpty() && completedTasks.isEmpty()) {
            createEmptyStateView();
            return;
        }

        // Show all tasks for this date
        for (TaskItem task : tasksForDate) {
            createTaskHistoryCard(task);
        }
    }

    private void updateStatsDisplay(List<TaskItem> allTasks, List<TaskItem> completedTasks) {
        int totalTasks = allTasks.size();
        int completedCount = completedTasks.size();

        if (totalTasks == 0) {
            statsDisplay.setText("📊 No quests recorded for this date");
            statsDisplay.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        } else {
            double successRate = (completedCount * 100.0 / totalTasks);
            String stats = String.format(Locale.getDefault(),
                "📊 %d Total | ✅ %d Completed | 📈 %.1f%% Success Rate",
                totalTasks, completedCount, successRate);
            statsDisplay.setText(stats);

            // Color code based on success rate
            if (successRate >= 80) {
                statsDisplay.setTextColor(UIStyleHelper.Colors.SUCCESS_GREEN);
            } else if (successRate >= 60) {
                statsDisplay.setTextColor(UIStyleHelper.Colors.WARNING_ORANGE);
            } else {
                statsDisplay.setTextColor(UIStyleHelper.Colors.DANGER_RED);
            }
        }
    }

    private void createEmptyStateView() {
        LinearLayout emptyStateCard = new LinearLayout(this);
        emptyStateCard.setOrientation(LinearLayout.VERTICAL);
        emptyStateCard.setGravity(Gravity.CENTER);
        UIStyleHelper.styleTaskCard(emptyStateCard, UIStyleHelper.Colors.LIGHT_GRAY);
        emptyStateCard.setPadding(40, 40, 40, 40);

        TextView emptyIcon = new TextView(this);
        emptyIcon.setText("🌙");
        emptyIcon.setTextSize(48);
        emptyIcon.setGravity(Gravity.CENTER);

        TextView emptyTitle = new TextView(this);
        emptyTitle.setText("Peaceful Day");
        emptyTitle.setTextSize(20);
        emptyTitle.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        emptyTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        emptyTitle.setGravity(Gravity.CENTER);
        emptyTitle.setPadding(0, 16, 0, 8);

        TextView emptyMessage = new TextView(this);
        emptyMessage.setText(getString(R.string.no_tasks_for_date) + "\nMaybe it was a day of rest!");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        emptyMessage.setGravity(Gravity.CENTER);

        emptyStateCard.addView(emptyIcon);
        emptyStateCard.addView(emptyTitle);
        emptyStateCard.addView(emptyMessage);

        tasksContainer.addView(emptyStateCard);
    }

    private void createTaskHistoryCard(TaskItem task) {
        int typeColor = getTaskTypeColor(task.getType());

        LinearLayout taskCard = new LinearLayout(this);
        taskCard.setOrientation(LinearLayout.VERTICAL);
        UIStyleHelper.styleTaskCard(taskCard, typeColor);

        // Task header with status, emoji, title and reward
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView statusEmoji = new TextView(this);
        statusEmoji.setText(getStatusEmoji(task.getStatus()));
        statusEmoji.setTextSize(24);
        statusEmoji.setPadding(0, 0, 12, 0);

        TextView taskIcon = new TextView(this);
        taskIcon.setText(getTypeEmoji(task.getType()));
        taskIcon.setTextSize(18);
        taskIcon.setPadding(0, 0, 12, 0);

        TextView taskTitle = new TextView(this);
        taskTitle.setText(task.getTitle());
        taskTitle.setTextSize(16);
        taskTitle.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        taskTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        taskTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView rewardBadge = new TextView(this);
        if (task.getStatus() == TaskItem.TaskStatus.COMPLETED) {
            rewardBadge.setText("💰 +" + task.getCoinReward());
            rewardBadge.setTextColor(UIStyleHelper.Colors.SUCCESS_GREEN);
            rewardBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.SUCCESS_GREEN, 0.2f), 12
            ));
        } else if (task.getStatus() == TaskItem.TaskStatus.FAILED) {
            rewardBadge.setText("💸 -" + task.getCoinPenalty());
            rewardBadge.setTextColor(UIStyleHelper.Colors.DANGER_RED);
            rewardBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.DANGER_RED, 0.2f), 12
            ));
        } else {
            rewardBadge.setText("⏳ Pending");
            rewardBadge.setTextColor(UIStyleHelper.Colors.WARNING_ORANGE);
            rewardBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.WARNING_ORANGE, 0.2f), 12
            ));
        }
        rewardBadge.setTextSize(12);
        rewardBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        rewardBadge.setPadding(12, 6, 12, 6);

        headerLayout.addView(statusEmoji);
        headerLayout.addView(taskIcon);
        headerLayout.addView(taskTitle);
        headerLayout.addView(rewardBadge);

        // Task details
        LinearLayout detailsLayout = new LinearLayout(this);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        detailsLayout.setPadding(36, 8, 0, 0);

        TextView taskType = new TextView(this);
        String typeText = task.getType().toString().replace("_", " ");
        taskType.setText("🎯 " + typeText + " Quest");
        taskType.setTextSize(14);
        taskType.setTextColor(UIStyleHelper.Colors.DARK_GRAY);

        detailsLayout.addView(taskType);

        if (task.getCompletedDate() != null) {
            TextView completedTime = new TextView(this);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            completedTime.setText("🕐 Completed at: " + timeFormat.format(task.getCompletedDate()));
            completedTime.setTextSize(12);
            completedTime.setTextColor(UIStyleHelper.Colors.SUCCESS_GREEN);
            completedTime.setTypeface(null, android.graphics.Typeface.BOLD);
            detailsLayout.addView(completedTime);
        }

        taskCard.addView(headerLayout);
        taskCard.addView(detailsLayout);

        // Add ripple effect
        UIStyleHelper.addRippleEffect(taskCard);

        tasksContainer.addView(taskCard);
    }

    private int getTaskTypeColor(TaskItem.TaskType type) {
        switch (type) {
            case TODO: return UIStyleHelper.Colors.TODO_COLOR;
            case HABIT: return UIStyleHelper.Colors.HABIT_COLOR;
            case PLANNING: return UIStyleHelper.Colors.PLANNING_COLOR;
            case DAILY_ACTIVITY: return UIStyleHelper.Colors.DAILY_COLOR;
            default: return UIStyleHelper.Colors.PRIMARY_BLUE;
        }
    }

    private String getStatusEmoji(TaskItem.TaskStatus status) {
        switch (status) {
            case COMPLETED: return "✅";
            case FAILED: return "❌";
            case SKIPPED: return "⏭️";
            default: return "⏳";
        }
    }

    private String getTypeEmoji(TaskItem.TaskType type) {
        switch (type) {
            case TODO: return "📝";
            case HABIT: return "🔄";
            case PLANNING: return "📋";
            case DAILY_ACTIVITY: return "⭐";
            default: return "📋";
        }
    }
}
