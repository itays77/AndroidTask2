package com.example.task1.Utilities;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.task1.Model.GameScore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
public class SharePreferencesManager {

    private static volatile SharePreferencesManager instance = null;
    private SharedPreferences sharedPref;
    private static final String SP_FILE = "SP_FILE";
    private static final String SCORES_KEY = "SCORES";

    private Gson gson;

    private SharePreferencesManager(Context context) {
        sharedPref = context.getApplicationContext().getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static SharePreferencesManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SharePreferencesManager is not initialized, call init(Context) first");
        }
        return instance;
    }

    public static synchronized SharePreferencesManager init(Context context) {
        if (instance == null) {
            instance = new SharePreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return sharedPref.getString(key, defValue);
    }

    public void saveScores(ArrayList<GameScore> scores) {
        String scoresJson = gson.toJson(scores);
        putString("SCORES", scoresJson);
    }

    public ArrayList<GameScore> getScores() {
        String scoresJson = getString("SCORES", "[]");
        Type listType = new TypeToken<ArrayList<GameScore>>(){}.getType();
        return gson.fromJson(scoresJson, listType);
    }
}
