package com.todolist.app.models;

public class Character {
    public enum CharacterStatus {
        ALIVE, DEAD
    }

    private String id;
    private String name;
    private String imagePath;
    private CharacterStatus status;
    private int price;
    private boolean isOwned;
    private boolean isFree;
    private int level;
    private int experience;

    public Character(String name, String imagePath, int price, boolean isFree) {
        this.id = System.currentTimeMillis() + "_" + Math.random();
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.isFree = isFree;
        this.status = CharacterStatus.ALIVE;
        this.isOwned = isFree; // Free characters are owned by default
        this.level = 1;
        this.experience = 0;
    }

    public void gainExperience(int exp) {
        this.experience += exp;
        // Level up logic (every 100 exp = 1 level)
        if (this.experience >= this.level * 100) {
            this.level++;
        }
    }

    public void kill() {
        this.status = CharacterStatus.DEAD;
    }

    public void revive() {
        this.status = CharacterStatus.ALIVE;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public CharacterStatus getStatus() { return status; }
    public void setStatus(CharacterStatus status) { this.status = status; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public boolean isOwned() { return isOwned; }
    public void setOwned(boolean owned) { isOwned = owned; }
    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
}
