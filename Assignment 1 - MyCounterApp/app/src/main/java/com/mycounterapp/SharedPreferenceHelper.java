package com.mycounterapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    private static final String SETTINGS_PREF = "Settings";
    private static final String COUNTS_PREF = "EventCountMem";

    private SharedPreferences settingsPrefs;
    private SharedPreferences countPrefs;

    public SharedPreferenceHelper(Context context) {
        settingsPrefs = context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE);
        countPrefs = context.getSharedPreferences(COUNTS_PREF, Context.MODE_PRIVATE);
    }

    //For Settings

    public String getButtonName(int buttonNum) {
        return settingsPrefs.getString("button" + buttonNum + "Name", "Event " + (char)(64 + buttonNum));
    }

    public int getMaxEvents() {
        return settingsPrefs.getInt("maxEvents", 200);
    }

    //For Counter Data

    public void incrementEventCount(int eventId) {
        String key = "Event" + eventId + "Count";
        int current = countPrefs.getInt(key, 0);
        countPrefs.edit().putInt(key, current + 1).apply();
    }

    public int getEventCount(int eventId) {
        return countPrefs.getInt("Event" + eventId + "Count", 0);
    }

    public int getEventCount(String key) {
        return countPrefs.getInt(key, 0);
    }


    public int getTotalCount() {
        int total = 0;
        total += countPrefs.getInt("Event1Count", 0);
        total += countPrefs.getInt("Event2Count", 0);
        total += countPrefs.getInt("Event3Count", 0);
        return total;
    }


    public void resetEventCount(String key) {
        countPrefs.edit().putInt(key, 0).apply();
    }


    public void resetAllCounts() {
        countPrefs.edit()
                .putInt("Event1Count", 0)
                .putInt("Event2Count", 0)
                .putInt("Event3Count", 0)
                .apply();
    }
}
