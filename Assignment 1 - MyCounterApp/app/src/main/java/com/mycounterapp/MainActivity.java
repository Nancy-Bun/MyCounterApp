package com.mycounterapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mycounterapp.R;

public class MainActivity extends AppCompatActivity {

    private int counter = 0;
    private TextView countText;
    private Button settings, eventA, eventB, eventC, countMem;

    private SharedPreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = findViewById(R.id.settings);
        eventA = findViewById(R.id.eventA);
        eventB = findViewById(R.id.eventB);
        eventC = findViewById(R.id.eventC);
        countMem = findViewById(R.id.countMem);
        countText = findViewById(R.id.countText);

        preferenceHelper = new SharedPreferenceHelper(this);

        loadButtonNames();

        settings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        countMem.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DataActivity.class));
        });

        eventA.setOnClickListener(v -> incrementCounter(1));
        eventB.setOnClickListener(v -> incrementCounter(2));
        eventC.setOnClickListener(v -> incrementCounter(3));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadButtonNames(); // <-- THIS fixes button name update
        countText.setText("Total Count: " + preferenceHelper.getTotalCount());
    }

    private void loadButtonNames() {
        eventA.setText(preferenceHelper.getButtonName(1));
        eventB.setText(preferenceHelper.getButtonName(2));
        eventC.setText(preferenceHelper.getButtonName(3));
    }

    private void incrementCounter(int eventId) {

        int maxEvents = preferenceHelper.getMaxEvents();
        int totalCount = preferenceHelper.getTotalCount();

        // Enforce max
        if (totalCount >= maxEvents) {
            Toast.makeText(this, "Maximum event count reached (" + maxEvents + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        // Increment memory count
        preferenceHelper.incrementEventCount(eventId);

        // Update UI count
        int newTotal = preferenceHelper.getTotalCount();
        countText.setText("Total Count: " + newTotal);
    }
}
