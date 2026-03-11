package com.mycounterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mycounterapp.R;

public class SettingsActivity extends AppCompatActivity {

    private EditText  eventAEdit,  eventBEdit,  eventCEdit, maxEdit;
    private Button saveButton;
    private TextView countAText, countBText, countCText, maxText;
    private boolean isEditMode = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //setting toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

         eventAEdit = findViewById(R.id. eventAEdit);
         eventBEdit = findViewById(R.id. eventBEdit);
         eventCEdit = findViewById(R.id. eventCEdit);
        maxEdit = findViewById(R.id.maxEdit);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        saveButton.setOnClickListener(v -> saveSettings());
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        loadSettings();
    }

    @Override
    //ensure reload
    protected void onResume() {
        super.onResume();
        loadSettings();
    }

    private boolean isAlphabeticAndSpaces(String text) {
        return text.matches("[a-zA-Z ]+");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_toggle) {
            toggleEditMode();
            return true;
        } else if (id == android.R.id.home) {
            finish(); // Use this to go back to the previous activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadSettings() {
        String button1Name = sharedPreferences.getString("button1Name", "");
        String button2Name = sharedPreferences.getString("button2Name", "");
        String button3Name = sharedPreferences.getString("button3Name", "");
        int maxEvents = sharedPreferences.getInt("maxEvents", -1);

         eventAEdit.setText(button1Name);
         eventBEdit.setText(button2Name);
         eventCEdit.setText(button3Name);

        if (maxEvents != -1) {
            maxEdit.setText(String.valueOf(maxEvents));
        }

        if (TextUtils.isEmpty(button1Name) && TextUtils.isEmpty(button2Name) &&
                TextUtils.isEmpty(button3Name) && maxEvents == -1) {
            toggleEditMode();
        }

    }

    private void saveSettings() {
        String button1Name =  eventAEdit.getText().toString().trim();
        String button2Name =  eventBEdit.getText().toString().trim();
        String button3Name =  eventCEdit.getText().toString().trim();
        String maxEventsStr = maxEdit.getText().toString().trim();

        if (isValidInput(button1Name, button2Name, button3Name, maxEventsStr)) {
            int maxEvents = Integer.parseInt(maxEventsStr);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("button1Name", button1Name);
            editor.putString("button2Name", button2Name);
            editor.putString("button3Name", button3Name);
            editor.putInt("maxEvents", maxEvents);
            editor.apply();

            SharedPreferenceHelper helper = new SharedPreferenceHelper(this);
            helper.resetAllCounts();

            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
            toggleEditMode();

            // Restart MainActivity to apply changes immediately
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    private boolean isValidInput(String button1Name, String button2Name, String button3Name, String maxEventsStr) {
        if (!isAlphabeticAndSpaces(button1Name)) {
             eventAEdit.setError("Invalid input: only alphabetic characters and spaces allowed");
            return false;
        }
        if (!isAlphabeticAndSpaces(button2Name)) {
             eventBEdit.setError("Invalid input: only alphabetic characters and spaces allowed");
            return false;
        }
        if (!isAlphabeticAndSpaces(button3Name)) {
             eventCEdit.setError("Invalid input: only alphabetic characters and spaces allowed");
            return false;
        }
        if (TextUtils.isEmpty(maxEventsStr)) {
            maxEdit.setError("This field cannot be empty");
            return false;
        }

        int maxEvents;
        //setting up try catch for constraints
        try {
            maxEvents = Integer.parseInt(maxEventsStr);
        } catch (NumberFormatException e) {
            maxEdit.setError("Invalid input: must be a number");
            return false;
        }
        if (maxEvents < 5 || maxEvents > 200) {
            maxEdit.setError("Number of events must be between 5 and 200");
            return false;
        }
        return true;
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

         eventAEdit.setEnabled(isEditMode);
         eventBEdit.setEnabled(isEditMode);
         eventCEdit.setEnabled(isEditMode);
        maxEdit.setEnabled(isEditMode);

        saveButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        invalidateOptionsMenu(); //makes changes immediate
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // making the edit toolbar visible or not based on edit mode flag
        menu.findItem(R.id.action_toggle).setVisible(!isEditMode);
        return super.onPrepareOptionsMenu(menu);
    }

}
