package com.todolist.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.todolist.app.models.TaskItem;

public class UIStyleHelper {

    // Color scheme inspired by Habitica but more modern
    public static class Colors {
        public static final int PRIMARY_BLUE = Color.parseColor("#4A90E2");
        public static final int PRIMARY_PURPLE = Color.parseColor("#8B5FBF");
        public static final int SUCCESS_GREEN = Color.parseColor("#5CB85C");
        public static final int WARNING_ORANGE = Color.parseColor("#F0AD4E");
        public static final int DANGER_RED = Color.parseColor("#D9534F");
        public static final int GOLD = Color.parseColor("#FFD700");
        public static final int LIGHT_GRAY = Color.parseColor("#F8F9FA");
        public static final int DARK_GRAY = Color.parseColor("#6C757D");
        public static final int WHITE = Color.parseColor("#FFFFFF");
        public static final int BACKGROUND = Color.parseColor("#F5F7FA");

        // Task type colors
        public static final int TODO_COLOR = Color.parseColor("#4A90E2");
        public static final int HABIT_COLOR = Color.parseColor("#8B5FBF");
        public static final int PLANNING_COLOR = Color.parseColor("#17A2B8");
        public static final int DAILY_COLOR = Color.parseColor("#28A745");
    }

    public static GradientDrawable createRoundedBackground(int color, int cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }

    public static GradientDrawable createGradientBackground(int startColor, int endColor, int cornerRadius) {
        GradientDrawable drawable = new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{startColor, endColor}
        );
        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }

    public static GradientDrawable createStrokedBackground(int fillColor, int strokeColor, int strokeWidth, int cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }

    public static void styleTaskCard(LinearLayout layout, int typeColor) {
        // Create gradient background
        GradientDrawable background = createGradientBackground(
            Colors.WHITE,
            adjustColorAlpha(typeColor, 0.05f),
            24
        );

        // Add shadow effect with stroke
        background.setStroke(2, adjustColorAlpha(typeColor, 0.2f));

        layout.setBackground(background);
        layout.setPadding(20, 16, 20, 16);

        // Add margin effect
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
        }
        params.setMargins(8, 8, 8, 8);
        layout.setLayoutParams(params);

        // Add elevation effect
        layout.setElevation(4);
    }

    public static void styleButton(Button button, int backgroundColor, int textColor) {
        GradientDrawable background = createGradientBackground(
            backgroundColor,
            adjustColorAlpha(backgroundColor, 0.8f),
            20
        );

        button.setBackground(background);
        button.setTextColor(textColor);
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(24, 12, 24, 12);
        button.setElevation(2);

        // Add press effect
        button.setStateListAnimator(null);
    }

    public static void stylePrimaryButton(Button button) {
        styleButton(button, Colors.PRIMARY_BLUE, Colors.WHITE);
    }

    public static void styleSuccessButton(Button button) {
        styleButton(button, Colors.SUCCESS_GREEN, Colors.WHITE);
    }

    public static void styleDangerButton(Button button) {
        styleButton(button, Colors.DANGER_RED, Colors.WHITE);
    }

    public static void styleWarningButton(Button button) {
        styleButton(button, Colors.WARNING_ORANGE, Colors.WHITE);
    }

    public static void styleSecondaryButton(Button button) {
        GradientDrawable background = createStrokedBackground(
            Colors.WHITE, Colors.PRIMARY_BLUE, 3, 20
        );
        button.setBackground(background);
        button.setTextColor(Colors.PRIMARY_BLUE);
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(24, 12, 24, 12);
    }

    public static void styleHeaderText(TextView textView, String text) {
        textView.setText(text);
        textView.setTextSize(24);
        textView.setTextColor(Colors.PRIMARY_PURPLE);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 8, 0, 16);
    }

    public static void styleSubHeaderText(TextView textView, String text) {
        textView.setText(text);
        textView.setTextSize(18);
        textView.setTextColor(Colors.DARK_GRAY);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 4, 0, 8);
    }

    public static void styleBodyText(TextView textView, String text) {
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(Colors.DARK_GRAY);
        textView.setPadding(0, 2, 0, 2);
    }

    public static void styleCoinDisplay(TextView textView, int coins) {
        textView.setText("🪙 " + coins);
        textView.setTextSize(18);
        textView.setTextColor(Colors.GOLD);
        textView.setTypeface(null, Typeface.BOLD);

        GradientDrawable background = createRoundedBackground(
            adjustColorAlpha(Colors.GOLD, 0.2f), 16
        );
        textView.setBackground(background);
        textView.setPadding(16, 8, 16, 8);
    }

    public static void styleCharacterStatus(TextView textView, String status, boolean isAlive) {
        textView.setText(status);
        textView.setTextSize(14);
        textView.setTextColor(isAlive ? Colors.SUCCESS_GREEN : Colors.DANGER_RED);
        textView.setTypeface(null, Typeface.BOLD);

        GradientDrawable background = createRoundedBackground(
            adjustColorAlpha(isAlive ? Colors.SUCCESS_GREEN : Colors.DANGER_RED, 0.2f), 12
        );
        textView.setBackground(background);
        textView.setPadding(12, 6, 12, 6);
    }

    public static LinearLayout createGameStatusBar(Context context) {
        LinearLayout statusBar = new LinearLayout(context);
        statusBar.setOrientation(LinearLayout.HORIZONTAL);
        statusBar.setPadding(16, 12, 16, 12);

        GradientDrawable background = createGradientBackground(
            Colors.PRIMARY_PURPLE,
            Colors.PRIMARY_BLUE,
            0
        );
        statusBar.setBackground(background);
        statusBar.setElevation(8);

        return statusBar;
    }

    public static LinearLayout createTaskTypeContainer(Context context, int typeColor) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable background = createRoundedBackground(Colors.WHITE, 16);
        container.setBackground(background);
        container.setElevation(6);
        container.setPadding(20, 16, 20, 16);

        // Add colored top border
        View topBorder = new View(context);
        GradientDrawable borderBg = createRoundedBackground(typeColor, 8);
        topBorder.setBackground(borderBg);
        LinearLayout.LayoutParams borderParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 6
        );
        borderParams.setMargins(0, 0, 0, 12);
        topBorder.setLayoutParams(borderParams);
        container.addView(topBorder, 0);

        return container;
    }

    public static int getTaskTypeColor(String taskType) {
        switch (taskType.toLowerCase()) {
            case "todo": return Colors.TODO_COLOR;
            case "habit": return Colors.HABIT_COLOR;
            case "planning": return Colors.PLANNING_COLOR;
            case "daily_activity": return Colors.DAILY_COLOR;
            default: return Colors.PRIMARY_BLUE;
        }
    }

    public static String getTaskTypeEmoji(String taskType) {
        switch (taskType.toLowerCase()) {
            case "todo": return "📝";
            case "habit": return "🔄";
            case "planning": return "📋";
            case "daily_activity": return "⭐";
            default: return "📋";
        }
    }

    public static int adjustColorAlpha(int color, float alpha) {
        int alphaInt = Math.round(Color.alpha(color) * alpha);
        return Color.argb(alphaInt, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static void addRippleEffect(View view) {
        // Simple press state change for API compatibility
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.7f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        });
    }

    public static void styleTimeDisplay(TextView textView, String timeText, boolean isUrgent) {
        textView.setText(timeText);
        textView.setTextSize(12);
        textView.setTypeface(null, Typeface.BOLD);

        int backgroundColor, textColor;
        if (isUrgent) {
            backgroundColor = adjustColorAlpha(Colors.DANGER_RED, 0.2f);
            textColor = Colors.DANGER_RED;
        } else {
            backgroundColor = adjustColorAlpha(Colors.WARNING_ORANGE, 0.2f);
            textColor = Colors.WARNING_ORANGE;
        }

        GradientDrawable background = createRoundedBackground(backgroundColor, 8);
        textView.setBackground(background);
        textView.setPadding(8, 4, 8, 4);
        textView.setTextColor(textColor);
    }

    public static void stylePriorityBadge(TextView textView, TaskItem.Priority priority) {
        String[] priorityTexts = {"🟢 Low", "🟡 Medium", "🟠 High", "🔴 Urgent"};
        int[] priorityColors = {Colors.SUCCESS_GREEN, Colors.WARNING_ORANGE, Colors.DANGER_RED, Colors.DANGER_RED};

        int index = priority.ordinal();
        textView.setText(priorityTexts[index]);
        textView.setTextSize(10);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(priorityColors[index]);

        GradientDrawable background = createRoundedBackground(
            adjustColorAlpha(priorityColors[index], 0.2f), 6
        );
        textView.setBackground(background);
        textView.setPadding(6, 3, 6, 3);
    }

    public static void styleOverdueCard(LinearLayout layout) {
        GradientDrawable background = createStrokedBackground(
            adjustColorAlpha(Colors.DANGER_RED, 0.1f),
            Colors.DANGER_RED,
            3,
            16
        );
        layout.setBackground(background);
        layout.setElevation(8);
    }

    public static void styleUrgentCard(LinearLayout layout) {
        GradientDrawable background = createStrokedBackground(
            adjustColorAlpha(Colors.WARNING_ORANGE, 0.1f),
            Colors.WARNING_ORANGE,
            2,
            16
        );
        layout.setBackground(background);
        layout.setElevation(6);
    }

    public static LinearLayout createReminderSection(Context context) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(0, 8, 0, 8);

        return container;
    }

    public static String getPriorityEmoji(TaskItem.Priority priority) {
        switch (priority) {
            case LOW: return "🟢";
            case MEDIUM: return "🟡";
            case HIGH: return "🟠";
            case URGENT: return "🔴";
            default: return "🟡";
        }
    }
}
