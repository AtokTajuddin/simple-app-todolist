package com.todolist.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.todolist.app.activities.CalendarHistoryActivity;
import com.todolist.app.activities.CharacterShopActivity;
import com.todolist.app.managers.GameManager;
import com.todolist.app.managers.TaskManager;
import com.todolist.app.managers.ReminderManager;
import com.todolist.app.models.Character;
import com.todolist.app.models.TaskItem;
import com.todolist.app.ui.UIStyleHelper;
import com.todolist.app.ui.TimeReminderDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private GameManager gameManager;
    private TaskManager taskManager;
    private ReminderManager reminderManager;
    private final ArrayList<TaskItem> currentTasks = new ArrayList<>();

    // UI Components
    private LinearLayout todocontainer;
    private EditText todoedittext;
    private TextView coinsDisplay;
    private TextView characterStatus;
    private Spinner taskTypeSpinner;
    private ScrollView mainScrollView;
    private LinearLayout urgentTasksContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize managers
        gameManager = GameManager.getInstance();
        taskManager = TaskManager.getInstance();
        reminderManager = ReminderManager.getInstance();
        reminderManager.initialize(this);

        createHabiticaStyleUI();

        // Update displays
        updateGameStatus();
        loadCurrentTasks();
        showUrgentTasks();
    }

    private void createHabiticaStyleUI() {
        // Main container with background
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setBackgroundColor(UIStyleHelper.Colors.BACKGROUND);

        // Create game status bar
        createGameStatusBar(mainContainer);

        // Create urgent tasks section
        createUrgentTasksSection(mainContainer);

        // Create navigation section
        createNavigationSection(mainContainer);

        // Create task input section
        createTaskInputSection(mainContainer);

        // Create tasks display section
        createTasksDisplaySection(mainContainer);

        setContentView(mainContainer);
    }

    private void createGameStatusBar(LinearLayout parent) {
        LinearLayout statusBar = UIStyleHelper.createGameStatusBar(this);

        // App title with icon
        LinearLayout titleSection = new LinearLayout(this);
        titleSection.setOrientation(LinearLayout.HORIZONTAL);
        titleSection.setGravity(Gravity.CENTER_VERTICAL);
        titleSection.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView gameIcon = new TextView(this);
        gameIcon.setText("🎮");
        gameIcon.setTextSize(24);
        gameIcon.setPadding(0, 0, 12, 0);

        TextView appTitle = new TextView(this);
        appTitle.setText("Epic Quest List");
        appTitle.setTextSize(20);
        appTitle.setTextColor(UIStyleHelper.Colors.WHITE);
        appTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        titleSection.addView(gameIcon);
        titleSection.addView(appTitle);

        // Coins display
        coinsDisplay = new TextView(this);
        coinsDisplay.setTextSize(16);
        coinsDisplay.setTextColor(UIStyleHelper.Colors.WHITE);
        coinsDisplay.setTypeface(null, android.graphics.Typeface.BOLD);

        // Character status
        characterStatus = new TextView(this);
        characterStatus.setTextSize(12);
        characterStatus.setTextColor(UIStyleHelper.Colors.WHITE);

        LinearLayout statusSection = new LinearLayout(this);
        statusSection.setOrientation(LinearLayout.VERTICAL);
        statusSection.setGravity(Gravity.END);
        statusSection.addView(coinsDisplay);
        statusSection.addView(characterStatus);

        statusBar.addView(titleSection);
        statusBar.addView(statusSection);
        parent.addView(statusBar);
    }

    private void createUrgentTasksSection(LinearLayout parent) {
        urgentTasksContainer = new LinearLayout(this);
        urgentTasksContainer.setOrientation(LinearLayout.VERTICAL);
        urgentTasksContainer.setPadding(16, 8, 16, 8);
        parent.addView(urgentTasksContainer);
    }

    private void createNavigationSection(LinearLayout parent) {
        LinearLayout navContainer = new LinearLayout(this);
        navContainer.setOrientation(LinearLayout.HORIZONTAL);
        navContainer.setPadding(16, 16, 16, 8);
        navContainer.setBackgroundColor(UIStyleHelper.Colors.WHITE);

        Button characterShopButton = new Button(this);
        characterShopButton.setText("🏪 " + getString(R.string.character_shop));
        characterShopButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.stylePrimaryButton(characterShopButton);
        characterShopButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharacterShopActivity.class);
            startActivity(intent);
        });

        // Add spacing
        View spacing1 = new View(this);
        spacing1.setLayoutParams(new LinearLayout.LayoutParams(16, LinearLayout.LayoutParams.MATCH_PARENT));

        Button historyButton = new Button(this);
        historyButton.setText("📅 " + getString(R.string.calendar_history));
        historyButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleSecondaryButton(historyButton);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalendarHistoryActivity.class);
            startActivity(intent);
        });

        navContainer.addView(characterShopButton);
        navContainer.addView(spacing1);
        navContainer.addView(historyButton);

        // Add shadow separator
        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
        separator.setBackgroundColor(UIStyleHelper.Colors.DARK_GRAY);

        parent.addView(navContainer);
        parent.addView(separator);
    }

    private void createTaskInputSection(LinearLayout parent) {
        LinearLayout inputContainer = UIStyleHelper.createTaskTypeContainer(this, UIStyleHelper.Colors.PRIMARY_BLUE);
        inputContainer.setPadding(20, 20, 20, 20);

        // Section title
        TextView sectionTitle = new TextView(this);
        UIStyleHelper.styleHeaderText(sectionTitle, "✨ Create New Quest");

        // Task type selector with icon
        LinearLayout typeSection = new LinearLayout(this);
        typeSection.setOrientation(LinearLayout.HORIZONTAL);
        typeSection.setGravity(Gravity.CENTER_VERTICAL);
        typeSection.setPadding(0, 8, 0, 12);

        TextView typeIcon = new TextView(this);
        typeIcon.setText("🎯");
        typeIcon.setTextSize(18);
        typeIcon.setPadding(0, 0, 12, 0);

        TextView typeLabel = new TextView(this);
        UIStyleHelper.styleBodyText(typeLabel, "Quest Type:");
        typeLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        typeSection.addView(typeIcon);
        typeSection.addView(typeLabel);

        taskTypeSpinner = new Spinner(this);
        String[] taskTypes = {
            "📝 To-Do Quest",
            "🔄 Daily Habit",
            "📋 Master Plan",
            "⭐ Epic Activity"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, taskTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskTypeSpinner.setAdapter(adapter);
        taskTypeSpinner.setPadding(16, 12, 16, 12);

        // Task input with styling
        todoedittext = new EditText(this);
        todoedittext.setHint("Enter your epic quest...");
        todoedittext.setTextSize(16);
        todoedittext.setPadding(20, 16, 20, 16);
        todoedittext.setBackgroundDrawable(UIStyleHelper.createStrokedBackground(
            UIStyleHelper.Colors.WHITE, UIStyleHelper.Colors.PRIMARY_BLUE, 2, 12
        ));

        taskTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTaskHint();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Enhanced add button with reminder options
        LinearLayout buttonSection = new LinearLayout(this);
        buttonSection.setOrientation(LinearLayout.HORIZONTAL);

        Button addButton = new Button(this);
        addButton.setText("⚔️ Add Quest");
        addButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleSuccessButton(addButton);
        addButton.setOnClickListener(v -> {
            String taskText = todoedittext.getText().toString().trim();
            if (!taskText.isEmpty()) {
                showTaskCreationDialog(taskText);
                todoedittext.setText("");
            } else {
                Toast.makeText(this, "⚠️ Please enter a quest description!", Toast.LENGTH_SHORT).show();
            }
        });

        View buttonSpacing = new View(this);
        buttonSpacing.setLayoutParams(new LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT));

        Button quickAddButton = new Button(this);
        quickAddButton.setText("⚡ Quick Add");
        quickAddButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleSecondaryButton(quickAddButton);
        quickAddButton.setOnClickListener(v -> {
            String taskText = todoedittext.getText().toString().trim();
            if (!taskText.isEmpty()) {
                addNewTask(taskText);
                todoedittext.setText("");
                Toast.makeText(this, "🎉 Quest added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "⚠️ Please enter a quest description!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSection.addView(addButton);
        buttonSection.addView(buttonSpacing);
        buttonSection.addView(quickAddButton);

        inputContainer.addView(sectionTitle);
        inputContainer.addView(typeSection);
        inputContainer.addView(taskTypeSpinner);

        // Add spacing
        View spacing = new View(this);
        spacing.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16));
        inputContainer.addView(spacing);

        inputContainer.addView(todoedittext);

        // Add spacing
        View spacing2 = new View(this);
        spacing2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16));
        inputContainer.addView(spacing2);

        inputContainer.addView(buttonSection);

        // Add margin to container
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 8);
        inputContainer.setLayoutParams(params);

        parent.addView(inputContainer);
    }

    private void showTaskCreationDialog(String taskText) {
        TaskItem newTask = new TaskItem(taskText, getSelectedTaskType());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚔️ Configure Your Quest");
        builder.setMessage("Set up your quest with reminders and priorities!");

        // Create custom dialog layout
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(20, 20, 20, 20);

        // Priority selection
        Button priorityButton = new Button(this);
        priorityButton.setText("🎯 Set Priority: Medium");
        UIStyleHelper.styleSecondaryButton(priorityButton);
        priorityButton.setOnClickListener(v -> {
            TimeReminderDialog.showPriorityPicker(this, new TimeReminderDialog.OnTimeSetListener() {
                @Override
                public void onDueDateSet(Date dueDate) {}
                @Override
                public void onReminderSet(Date reminderTime) {}
                @Override
                public void onPrioritySet(TaskItem.Priority priority) {
                    newTask.setPriority(priority);
                    priorityButton.setText("🎯 Priority: " + priority.toString());
                }
            });
        });

        // Due date selection
        Button dueDateButton = new Button(this);
        dueDateButton.setText("📅 Set Due Date");
        UIStyleHelper.styleWarningButton(dueDateButton);
        dueDateButton.setOnClickListener(v -> {
            TimeReminderDialog.showDueDatePicker(this, new TimeReminderDialog.OnTimeSetListener() {
                @Override
                public void onDueDateSet(Date dueDate) {
                    newTask.setDueDate(dueDate);
                    SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                    dueDateButton.setText("📅 Due: " + df.format(dueDate));
                }
                @Override
                public void onReminderSet(Date reminderTime) {}
                @Override
                public void onPrioritySet(TaskItem.Priority priority) {}
            });
        });

        // Reminder selection
        Button reminderButton = new Button(this);
        reminderButton.setText("🔔 Set Reminder");
        UIStyleHelper.stylePrimaryButton(reminderButton);
        reminderButton.setOnClickListener(v -> {
            TimeReminderDialog.showReminderPicker(this, new TimeReminderDialog.OnTimeSetListener() {
                @Override
                public void onDueDateSet(Date dueDate) {}
                @Override
                public void onReminderSet(Date reminderTime) {
                    newTask.setReminderTime(reminderTime);
                    newTask.setHasReminder(true);
                    SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                    reminderButton.setText("🔔 Remind: " + df.format(reminderTime));
                }
                @Override
                public void onPrioritySet(TaskItem.Priority priority) {}
            });
        });

        dialogLayout.addView(priorityButton);
        dialogLayout.addView(dueDateButton);
        dialogLayout.addView(reminderButton);

        builder.setView(dialogLayout);
        builder.setPositiveButton("✅ Create Quest", (dialog, which) -> {
            addTaskWithReminder(newTask);
            Toast.makeText(this, "🎉 Quest created with reminders!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("❌ Cancel", null);
        builder.show();
    }

    private void createTasksDisplaySection(LinearLayout parent) {
        // Section header
        LinearLayout headerSection = new LinearLayout(this);
        headerSection.setOrientation(LinearLayout.HORIZONTAL);
        headerSection.setPadding(20, 16, 20, 8);
        headerSection.setBackgroundColor(UIStyleHelper.Colors.WHITE);

        TextView activeQuestsTitle = new TextView(this);
        UIStyleHelper.styleHeaderText(activeQuestsTitle, "⚔️ Active Quests");
        activeQuestsTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView questCount = new TextView(this);
        questCount.setText("0 quests");
        questCount.setTextSize(14);
        questCount.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        questCount.setGravity(Gravity.END);

        headerSection.addView(activeQuestsTitle);
        headerSection.addView(questCount);

        mainScrollView = new ScrollView(this);
        mainScrollView.setFillViewport(true);

        todocontainer = new LinearLayout(this);
        todocontainer.setOrientation(LinearLayout.VERTICAL);
        todocontainer.setPadding(8, 8, 8, 8);

        mainScrollView.addView(todocontainer);

        parent.addView(headerSection);
        parent.addView(mainScrollView);
    }

    private void updateTaskHint() {
        int selectedType = taskTypeSpinner.getSelectedItemPosition();
        String[] hints = {
            "Enter your to-do quest...",
            "Enter your daily habit...",
            "Enter your master plan...",
            "Enter your epic activity..."
        };
        todoedittext.setHint(hints[selectedType]);
    }

    private void addNewTask(String taskText) {
        TaskItem.TaskType type = getSelectedTaskType();
        TaskItem newTask = new TaskItem(taskText, type);

        currentTasks.add(newTask);
        taskManager.addTask(newTask);

        addTaskToUI(newTask);
        updateQuestCount();
    }

    private void addTaskWithReminder(TaskItem task) {
        currentTasks.add(task);
        taskManager.addTask(task);

        // Schedule reminder if set
        if (task.hasReminder()) {
            reminderManager.scheduleReminder(task);
        }

        addTaskToUI(task);
        updateQuestCount();
        showUrgentTasks(); // Refresh urgent tasks
    }

    private void showUrgentTasks() {
        urgentTasksContainer.removeAllViews();

        // Show overdue tasks
        for (TaskItem task : reminderManager.getOverdueTasks()) {
            createUrgentTaskCard(task, true);
        }

        // Show upcoming urgent tasks (next 4 hours)
        for (TaskItem task : reminderManager.getUpcomingTasks(4)) {
            if (task.isUrgent()) {
                createUrgentTaskCard(task, false);
            }
        }
    }

    private void createUrgentTaskCard(TaskItem task, boolean isOverdue) {
        LinearLayout urgentCard = new LinearLayout(this);
        urgentCard.setOrientation(LinearLayout.HORIZONTAL);
        urgentCard.setPadding(16, 12, 16, 12);

        if (isOverdue) {
            UIStyleHelper.styleOverdueCard(urgentCard);
        } else {
            UIStyleHelper.styleUrgentCard(urgentCard);
        }

        TextView urgentIcon = new TextView(this);
        urgentIcon.setText(isOverdue ? "🚨" : "⚠️");
        urgentIcon.setTextSize(20);
        urgentIcon.setPadding(0, 0, 12, 0);

        TextView urgentText = new TextView(this);
        String message = (isOverdue ? "OVERDUE: " : "URGENT: ") + task.getTitle();
        if (task.getDueDate() != null) {
            message += " (" + task.getTimeUntilDue() + ")";
        }
        urgentText.setText(message);
        urgentText.setTextSize(14);
        urgentText.setTextColor(isOverdue ? UIStyleHelper.Colors.DANGER_RED : UIStyleHelper.Colors.WARNING_ORANGE);
        urgentText.setTypeface(null, android.graphics.Typeface.BOLD);
        urgentText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button quickCompleteButton = new Button(this);
        quickCompleteButton.setText("✅");
        quickCompleteButton.setPadding(8, 8, 8, 8);
        UIStyleHelper.styleSuccessButton(quickCompleteButton);
        quickCompleteButton.setOnClickListener(v -> {
            completeTask(task, urgentCard);
            showUrgentTasks(); // Refresh urgent tasks
        });

        urgentCard.addView(urgentIcon);
        urgentCard.addView(urgentText);
        urgentCard.addView(quickCompleteButton);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 4, 0, 4);
        urgentCard.setLayoutParams(params);

        urgentTasksContainer.addView(urgentCard);
    }

    private TaskItem.TaskType getSelectedTaskType() {
        int selectedType = taskTypeSpinner.getSelectedItemPosition();
        switch (selectedType) {
            case 0: return TaskItem.TaskType.TODO;
            case 1: return TaskItem.TaskType.HABIT;
            case 2: return TaskItem.TaskType.PLANNING;
            case 3: return TaskItem.TaskType.DAILY_ACTIVITY;
            default: return TaskItem.TaskType.TODO;
        }
    }

    private void addTaskToUI(TaskItem task) {
        int typeColor = getTaskTypeColor(task.getType());

        LinearLayout taskCard = new LinearLayout(this);
        taskCard.setOrientation(LinearLayout.VERTICAL);
        UIStyleHelper.styleTaskCard(taskCard, typeColor);

        // Apply special styling for urgent/overdue tasks
        if (task.isOverdue()) {
            UIStyleHelper.styleOverdueCard(taskCard);
        } else if (task.isUrgent()) {
            UIStyleHelper.styleUrgentCard(taskCard);
        }

        // Task header with emoji, title and reward
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView taskIcon = new TextView(this);
        taskIcon.setText(getTaskTypeEmoji(task.getType()));
        taskIcon.setTextSize(20);
        taskIcon.setPadding(0, 0, 12, 0);

        TextView taskTitle = new TextView(this);
        taskTitle.setText(task.getTitle());
        taskTitle.setTextSize(18);
        taskTitle.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        taskTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        taskTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView rewardBadge = new TextView(this);
        rewardBadge.setText("💰 +" + task.getCoinReward());
        rewardBadge.setTextSize(14);
        rewardBadge.setTextColor(UIStyleHelper.Colors.GOLD);
        rewardBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        rewardBadge.setBackground(UIStyleHelper.createRoundedBackground(
            UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.GOLD, 0.2f), 12
        ));
        rewardBadge.setPadding(12, 6, 12, 6);

        headerLayout.addView(taskIcon);
        headerLayout.addView(taskTitle);
        headerLayout.addView(rewardBadge);

        // Task info section with time and priority
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);
        infoLayout.setPadding(32, 8, 0, 8);

        TextView taskInfo = new TextView(this);
        String typeText = task.getType().toString().replace("_", " ");
        taskInfo.setText("🎯 " + typeText + " • Penalty: -" + task.getCoinPenalty() + " coins");
        taskInfo.setTextSize(12);
        taskInfo.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        taskInfo.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Priority badge
        TextView priorityBadge = new TextView(this);
        UIStyleHelper.stylePriorityBadge(priorityBadge, task.getPriority());

        infoLayout.addView(taskInfo);
        infoLayout.addView(priorityBadge);

        // Time information
        if (task.getDueDate() != null || task.hasReminder()) {
            LinearLayout timeLayout = new LinearLayout(this);
            timeLayout.setOrientation(LinearLayout.HORIZONTAL);
            timeLayout.setPadding(32, 4, 0, 8);

            if (task.getDueDate() != null) {
                TextView dueTimeText = new TextView(this);
                String timeText = "📅 Due: " + task.getTimeUntilDue();
                UIStyleHelper.styleTimeDisplay(dueTimeText, timeText, task.isUrgent());
                timeLayout.addView(dueTimeText);

                if (task.hasReminder()) {
                    View spacing = new View(this);
                    spacing.setLayoutParams(new LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT));
                    timeLayout.addView(spacing);
                }
            }

            if (task.hasReminder()) {
                TextView reminderText = new TextView(this);
                SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                String reminderTimeText = "🔔 " + df.format(task.getReminderTime());
                UIStyleHelper.styleTimeDisplay(reminderText, reminderTimeText, false);
                timeLayout.addView(reminderText);
            }

            taskCard.addView(timeLayout);
        }

        // Action buttons with improved styling
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 8, 0, 0);

        Button completeButton = new Button(this);
        completeButton.setText("✅ Complete");
        completeButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleSuccessButton(completeButton);
        completeButton.setOnClickListener(v -> completeTask(task, taskCard));

        Button failButton = new Button(this);
        failButton.setText("❌ Fail");
        failButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleDangerButton(failButton);
        failButton.setOnClickListener(v -> failTask(task, taskCard));

        Button deleteButton = new Button(this);
        deleteButton.setText("🗑️ Delete");
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        UIStyleHelper.styleWarningButton(deleteButton);
        deleteButton.setOnClickListener(v -> deleteTask(task, taskCard));

        // Add spacing between buttons
        View spacing1 = new View(this);
        spacing1.setLayoutParams(new LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT));
        View spacing2 = new View(this);
        spacing2.setLayoutParams(new LinearLayout.LayoutParams(8, LinearLayout.LayoutParams.MATCH_PARENT));

        buttonLayout.addView(completeButton);
        buttonLayout.addView(spacing1);
        buttonLayout.addView(failButton);
        buttonLayout.addView(spacing2);
        buttonLayout.addView(deleteButton);

        taskCard.addView(headerLayout);
        taskCard.addView(infoLayout);
        taskCard.addView(buttonLayout);

        // Add ripple effect
        UIStyleHelper.addRippleEffect(taskCard);

        todocontainer.addView(taskCard);
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

    private String getTaskTypeEmoji(TaskItem.TaskType type) {
        switch (type) {
            case TODO: return "📝";
            case HABIT: return "🔄";
            case PLANNING: return "📋";
            case DAILY_ACTIVITY: return "⭐";
            default: return "📋";
        }
    }

    private void completeTask(TaskItem task, LinearLayout taskLayout) {
        if (gameManager.completeTask(task)) {
            // Cancel reminder
            reminderManager.cancelReminder(task.getId());

            // Update task in TaskManager as well
            taskManager.updateTask(task);

            showSuccessMessage("🎉 Quest Completed! +" + task.getCoinReward() + " coins earned!");

            // Check if character was revived
            Character activeChar = gameManager.getActiveCharacter();
            if (activeChar != null && activeChar.getStatus() == Character.CharacterStatus.ALIVE) {
                if (!gameManager.hasCompletedTaskToday()) {
                    showSuccessMessage("✨ " + getString(R.string.character_revived));
                }
            }

            removeTaskFromUI(task, taskLayout);
            updateGameStatus();
            updateQuestCount();
            showUrgentTasks(); // Refresh urgent tasks
        }
    }

    private void failTask(TaskItem task, LinearLayout taskLayout) {
        new AlertDialog.Builder(this)
            .setTitle("💀 Fail Quest")
            .setMessage("Are you sure you want to fail this quest? You will lose " + task.getCoinPenalty() + " coins and your character might be in danger!")
            .setPositiveButton("😈 Yes, Fail", (dialog, which) -> {
                // Cancel reminder
                reminderManager.cancelReminder(task.getId());

                gameManager.failTask(task);
                taskManager.updateTask(task);

                showErrorMessage("💀 Quest Failed! -" + task.getCoinPenalty() + " coins lost!");

                // Check if character died
                Character activeChar = gameManager.getActiveCharacter();
                if (activeChar != null && activeChar.getStatus() == Character.CharacterStatus.DEAD) {
                    showErrorMessage("☠️ " + getString(R.string.character_dead));
                }

                removeTaskFromUI(task, taskLayout);
                updateGameStatus();
                updateQuestCount();
                showUrgentTasks(); // Refresh urgent tasks
            })
            .setNegativeButton("🛡️ Keep Fighting", null)
            .show();
    }

    private void deleteTask(TaskItem task, LinearLayout taskLayout) {
        new AlertDialog.Builder(this)
            .setTitle("🗑️ Delete Quest")
            .setMessage("Are you sure you want to delete this quest? This action cannot be undone.")
            .setPositiveButton("🗑️ Delete", (dialog, which) -> {
                // Cancel reminder
                reminderManager.cancelReminder(task.getId());

                removeTaskFromUI(task, taskLayout);
                updateQuestCount();
                showUrgentTasks(); // Refresh urgent tasks
            })
            .setNegativeButton("📋 Keep", null)
            .show();
    }

    private void removeTaskFromUI(TaskItem task, LinearLayout taskLayout) {
        currentTasks.remove(task);
        taskManager.removeTask(task);
        todocontainer.removeView(taskLayout);
    }

    private void loadCurrentTasks() {
        todocontainer.removeAllViews();
        for (TaskItem task : currentTasks) {
            if (task.getStatus() == TaskItem.TaskStatus.PENDING) {
                addTaskToUI(task);
            }
        }
        updateQuestCount();
    }

    private void updateGameStatus() {
        // Update coins display with beautiful styling
        UIStyleHelper.styleCoinDisplay(coinsDisplay, gameManager.getCoins());

        // Update character status with styling
        Character activeChar = gameManager.getActiveCharacter();
        if (activeChar != null) {
            boolean isAlive = activeChar.getStatus() == Character.CharacterStatus.ALIVE;
            String status = (isAlive ? "😊 " : "💀 ") + activeChar.getName() + " (Lv." + activeChar.getLevel() + ")";
            UIStyleHelper.styleCharacterStatus(characterStatus, status, isAlive);
        } else {
            characterStatus.setText("🚫 No active character");
            characterStatus.setTextColor(UIStyleHelper.Colors.DANGER_RED);
        }
    }

    private void updateQuestCount() {
        // This method would need to find and update the quest count display
        int activeCount = 0;
        for (TaskItem task : currentTasks) {
            if (task.getStatus() == TaskItem.TaskStatus.PENDING) {
                activeCount++;
            }
        }
        // Quest count display logic would go here
    }

    private void showSuccessMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void showErrorMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGameStatus(); // Update when returning from other activities
        showUrgentTasks(); // Refresh urgent tasks when returning
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reminderManager != null) {
            reminderManager.shutdown();
        }
    }
}
