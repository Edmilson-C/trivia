package com.example.trivia.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void saveData(int currentScore, int currentQuestionIndex) {
        if(currentScore > preferences.getInt("highscore", 0))
            preferences.edit().putInt("highscore", currentScore).apply();
        preferences.edit().putInt("index", currentQuestionIndex).apply();
    }

    public int getHighScore() {
        return preferences.getInt("highscore", 0);
    }

    public int getQuestionIndex() {
        return preferences.getInt("index", 0);
    }
}
