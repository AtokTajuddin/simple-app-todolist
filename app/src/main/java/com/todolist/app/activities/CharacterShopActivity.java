package com.todolist.app.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.todolist.app.R;
import com.todolist.app.managers.GameManager;
import com.todolist.app.models.Character;
import com.todolist.app.ui.UIStyleHelper;

public class CharacterShopActivity extends AppCompatActivity {
    private GameManager gameManager;
    private TextView coinsDisplay;
    private LinearLayout charactersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameManager = GameManager.getInstance();
        createBeautifulShopUI();
        loadCharacters();
    }

    private void createBeautifulShopUI() {
        // Main container with background
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setBackgroundColor(UIStyleHelper.Colors.BACKGROUND);

        // Create header section
        createShopHeader(mainContainer);

        // Create characters display section
        createCharactersSection(mainContainer);

        // Create back button
        createBackButton(mainContainer);

        setContentView(mainContainer);
    }

    private void createShopHeader(LinearLayout parent) {
        LinearLayout headerContainer = UIStyleHelper.createGameStatusBar(this);
        headerContainer.setPadding(20, 16, 20, 16);

        // Shop title with icon
        LinearLayout titleSection = new LinearLayout(this);
        titleSection.setOrientation(LinearLayout.HORIZONTAL);
        titleSection.setGravity(Gravity.CENTER_VERTICAL);
        titleSection.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView shopIcon = new TextView(this);
        shopIcon.setText("🏪");
        shopIcon.setTextSize(28);
        shopIcon.setPadding(0, 0, 16, 0);

        TextView shopTitle = new TextView(this);
        shopTitle.setText("Character Shop");
        shopTitle.setTextSize(24);
        shopTitle.setTextColor(UIStyleHelper.Colors.WHITE);
        shopTitle.setTypeface(null, android.graphics.Typeface.BOLD);

        titleSection.addView(shopIcon);
        titleSection.addView(shopTitle);

        // Coins display
        coinsDisplay = new TextView(this);
        updateCoinsDisplay();

        headerContainer.addView(titleSection);
        headerContainer.addView(coinsDisplay);
        parent.addView(headerContainer);

        // Add subtitle
        LinearLayout subtitleContainer = new LinearLayout(this);
        subtitleContainer.setOrientation(LinearLayout.VERTICAL);
        subtitleContainer.setBackgroundColor(UIStyleHelper.Colors.WHITE);
        subtitleContainer.setPadding(20, 16, 20, 16);

        TextView subtitle = new TextView(this);
        UIStyleHelper.styleSubHeaderText(subtitle, "✨ Choose your epic companion!");

        TextView description = new TextView(this);
        UIStyleHelper.styleBodyText(description, "Characters gain experience and level up as you complete quests. Choose wisely!");

        subtitleContainer.addView(subtitle);
        subtitleContainer.addView(description);
        parent.addView(subtitleContainer);

        // Add separator
        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
        separator.setBackgroundColor(UIStyleHelper.Colors.DARK_GRAY);
        parent.addView(separator);
    }

    private void createCharactersSection(LinearLayout parent) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        charactersContainer = new LinearLayout(this);
        charactersContainer.setOrientation(LinearLayout.VERTICAL);
        charactersContainer.setPadding(16, 16, 16, 16);

        scrollView.addView(charactersContainer);
        parent.addView(scrollView);
    }

    private void createBackButton(LinearLayout parent) {
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setBackgroundColor(UIStyleHelper.Colors.WHITE);
        buttonContainer.setPadding(20, 16, 20, 16);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);

        Button backButton = new Button(this);
        backButton.setText("🏠 " + getString(R.string.back));
        backButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        UIStyleHelper.styleSecondaryButton(backButton);
        backButton.setOnClickListener(v -> finish());

        buttonContainer.addView(backButton);
        parent.addView(buttonContainer);
    }

    private void loadCharacters() {
        charactersContainer.removeAllViews();

        for (Character character : gameManager.getAllCharacters()) {
            createCharacterCard(character);
        }
    }

    private void createCharacterCard(Character character) {
        LinearLayout characterCard = new LinearLayout(this);
        characterCard.setOrientation(LinearLayout.VERTICAL);

        // Determine card color based on character status
        int cardColor = character.isFree() ? UIStyleHelper.Colors.SUCCESS_GREEN :
                       character.isOwned() ? UIStyleHelper.Colors.PRIMARY_BLUE : UIStyleHelper.Colors.GOLD;

        UIStyleHelper.styleTaskCard(characterCard, cardColor);
        characterCard.setPadding(24, 20, 24, 20);

        // Character header
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Character avatar (using emoji for now)
        TextView characterAvatar = new TextView(this);
        characterAvatar.setText(getCharacterEmoji(character.getName()));
        characterAvatar.setTextSize(32);
        characterAvatar.setPadding(0, 0, 16, 0);

        // Character info
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView nameText = new TextView(this);
        nameText.setText(character.getName());
        nameText.setTextSize(20);
        nameText.setTextColor(UIStyleHelper.Colors.DARK_GRAY);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView statusText = new TextView(this);
        String statusStr = getCharacterStatusText(character);
        statusText.setText(statusStr);
        statusText.setTextSize(14);
        statusText.setTextColor(UIStyleHelper.Colors.DARK_GRAY);

        infoLayout.addView(nameText);
        infoLayout.addView(statusText);

        // Price or status badge
        TextView priceBadge = new TextView(this);
        if (character.isOwned()) {
            boolean isAlive = character.getStatus() == Character.CharacterStatus.ALIVE;
            priceBadge.setText(isAlive ? "👑 OWNED" : "💀 DEAD");
            priceBadge.setTextColor(isAlive ? UIStyleHelper.Colors.SUCCESS_GREEN : UIStyleHelper.Colors.DANGER_RED);
            priceBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(isAlive ? UIStyleHelper.Colors.SUCCESS_GREEN : UIStyleHelper.Colors.DANGER_RED, 0.2f), 12
            ));
        } else if (character.isFree()) {
            priceBadge.setText("🆓 FREE");
            priceBadge.setTextColor(UIStyleHelper.Colors.SUCCESS_GREEN);
            priceBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.SUCCESS_GREEN, 0.2f), 12
            ));
        } else {
            priceBadge.setText("💰 " + character.getPrice());
            priceBadge.setTextColor(UIStyleHelper.Colors.GOLD);
            priceBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.GOLD, 0.2f), 12
            ));
        }
        priceBadge.setTextSize(14);
        priceBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        priceBadge.setPadding(16, 8, 16, 8);

        headerLayout.addView(characterAvatar);
        headerLayout.addView(infoLayout);
        headerLayout.addView(priceBadge);

        characterCard.addView(headerLayout);

        // Character stats (if owned)
        if (character.isOwned()) {
            LinearLayout statsLayout = new LinearLayout(this);
            statsLayout.setOrientation(LinearLayout.HORIZONTAL);
            statsLayout.setPadding(48, 12, 0, 12);

            TextView levelBadge = new TextView(this);
            levelBadge.setText("⭐ Lv." + character.getLevel());
            levelBadge.setTextSize(12);
            levelBadge.setTextColor(UIStyleHelper.Colors.PRIMARY_BLUE);
            levelBadge.setTypeface(null, android.graphics.Typeface.BOLD);
            levelBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.PRIMARY_BLUE, 0.2f), 8
            ));
            levelBadge.setPadding(12, 6, 12, 6);

            View spacing = new View(this);
            spacing.setLayoutParams(new LinearLayout.LayoutParams(16, LinearLayout.LayoutParams.MATCH_PARENT));

            TextView expBadge = new TextView(this);
            expBadge.setText("✨ " + character.getExperience() + " XP");
            expBadge.setTextSize(12);
            expBadge.setTextColor(UIStyleHelper.Colors.PRIMARY_PURPLE);
            expBadge.setTypeface(null, android.graphics.Typeface.BOLD);
            expBadge.setBackground(UIStyleHelper.createRoundedBackground(
                UIStyleHelper.adjustColorAlpha(UIStyleHelper.Colors.PRIMARY_PURPLE, 0.2f), 8
            ));
            expBadge.setPadding(12, 6, 12, 6);

            statsLayout.addView(levelBadge);
            statsLayout.addView(spacing);
            statsLayout.addView(expBadge);
            characterCard.addView(statsLayout);
        }

        // Action button
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setPadding(0, 16, 0, 0);

        Button actionButton = new Button(this);
        actionButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (character.isOwned()) {
            if (gameManager.getActiveCharacter() != null &&
                gameManager.getActiveCharacter().getId().equals(character.getId())) {
                actionButton.setText("👑 " + getString(R.string.active));
                UIStyleHelper.styleSuccessButton(actionButton);
                actionButton.setEnabled(false);
            } else {
                actionButton.setText("⚔️ " + getString(R.string.select));
                UIStyleHelper.stylePrimaryButton(actionButton);
                actionButton.setOnClickListener(v -> {
                    gameManager.setActiveCharacter(character);
                    loadCharacters(); // Refresh
                    Toast.makeText(this, "🎉 " + character.getName() + " is now your active companion!", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            actionButton.setText("💰 " + getString(R.string.buy) + " (" + character.getPrice() + " coins)");
            boolean canAfford = gameManager.getCoins() >= character.getPrice();

            if (canAfford) {
                UIStyleHelper.styleWarningButton(actionButton);
            } else {
                UIStyleHelper.styleDangerButton(actionButton);
                actionButton.setEnabled(false);
            }

            actionButton.setOnClickListener(v -> {
                if (gameManager.buyCharacter(character)) {
                    updateCoinsDisplay();
                    loadCharacters(); // Refresh
                    Toast.makeText(this, "🎉 " + character.getName() + " purchased successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "💸 Not enough coins! Need " + character.getPrice() + " coins.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        buttonLayout.addView(actionButton);
        characterCard.addView(buttonLayout);

        // Add ripple effect
        UIStyleHelper.addRippleEffect(characterCard);

        charactersContainer.addView(characterCard);
    }

    private String getCharacterEmoji(String name) {
        switch (name.toLowerCase()) {
            case "starter pet": return "🐾";
            case "dragon": return "🐉";
            case "phoenix": return "🔥";
            case "unicorn": return "🦄";
            case "griffin": return "🦅";
            default: return "🐾";
        }
    }

    private String getCharacterStatusText(Character character) {
        if (character.isFree()) {
            return "🆓 Free starter companion";
        } else if (character.isOwned()) {
            boolean isAlive = character.getStatus() == Character.CharacterStatus.ALIVE;
            return isAlive ? "✅ Ready for adventure!" : "💀 Needs revival through completing quests";
        } else {
            return "💎 Premium companion - " + character.getPrice() + " coins";
        }
    }

    private void updateCoinsDisplay() {
        UIStyleHelper.styleCoinDisplay(coinsDisplay, gameManager.getCoins());
    }
}
