package com.todolist.app.managers;

import com.todolist.app.models.Character;
import com.todolist.app.models.TaskItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameManager {
    private static GameManager instance;
    private int coins;
    private List<Character> allCharacters;
    private List<Character> ownedCharacters;
    private Character activeCharacter;
    private List<TaskItem> taskHistory;
    private boolean hasCompletedTaskToday;

    private GameManager() {
        coins = 50; // Starting coins
        allCharacters = new ArrayList<>();
        ownedCharacters = new ArrayList<>();
        taskHistory = new ArrayList<>();
        hasCompletedTaskToday = false;
        initializeCharacters();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private void initializeCharacters() {
        // Free starter character
        Character starter = new Character("Starter Pet", "starter_pet", 0, true);
        allCharacters.add(starter);
        ownedCharacters.add(starter);
        activeCharacter = starter;

        // Premium characters
        allCharacters.add(new Character("Dragon", "dragon", 100, false));
        allCharacters.add(new Character("Phoenix", "phoenix", 150, false));
        allCharacters.add(new Character("Unicorn", "unicorn", 200, false));
        allCharacters.add(new Character("Griffin", "griffin", 250, false));
    }

    public boolean completeTask(TaskItem task) {
        if (task.getStatus() == TaskItem.TaskStatus.PENDING) {
            task.setStatus(TaskItem.TaskStatus.COMPLETED);
            task.setCompletedDate(new Date());

            // Add coins for completion
            addCoins(task.getCoinReward());

            // Add experience to active character
            if (activeCharacter != null && activeCharacter.getStatus() == Character.CharacterStatus.ALIVE) {
                activeCharacter.gainExperience(task.getCoinReward());
            }

            // Revive character if dead and this is first task of the day
            if (activeCharacter != null && activeCharacter.getStatus() == Character.CharacterStatus.DEAD) {
                activeCharacter.revive();
            }

            hasCompletedTaskToday = true;
            taskHistory.add(task);
            return true;
        }
        return false;
    }

    public void failTask(TaskItem task) {
        if (task.getStatus() == TaskItem.TaskStatus.PENDING) {
            task.setStatus(TaskItem.TaskStatus.FAILED);

            // Deduct coins for failure
            deductCoins(task.getCoinPenalty());

            // Kill character if coins go negative and no task completed today
            if (coins < 0 && !hasCompletedTaskToday && activeCharacter != null) {
                activeCharacter.kill();
            }

            taskHistory.add(task);
        }
    }

    public boolean buyCharacter(Character character) {
        if (coins >= character.getPrice() && !character.isOwned()) {
            deductCoins(character.getPrice());
            character.setOwned(true);
            ownedCharacters.add(character);
            return true;
        }
        return false;
    }

    public void setActiveCharacter(Character character) {
        if (character.isOwned()) {
            this.activeCharacter = character;
        }
    }

    private void addCoins(int amount) {
        coins += amount;
    }

    private void deductCoins(int amount) {
        coins -= amount;
    }

    public void resetDailyProgress() {
        hasCompletedTaskToday = false;
    }

    // Getters
    public int getCoins() { return coins; }
    public List<Character> getAllCharacters() { return allCharacters; }
    public List<Character> getOwnedCharacters() { return ownedCharacters; }
    public Character getActiveCharacter() { return activeCharacter; }
    public List<TaskItem> getTaskHistory() { return taskHistory; }
    public boolean hasCompletedTaskToday() { return hasCompletedTaskToday; }
}
