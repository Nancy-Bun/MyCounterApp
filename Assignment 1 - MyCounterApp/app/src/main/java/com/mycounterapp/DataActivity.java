package com.mycounterapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mycounterapp.R;

import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewTotalCount;
    private TextView[] textViewEventCounts;  //array for counts
    private boolean isEventNameMode = true;  //true boolean sets default mode
    private EventListAdapter adapter;
    private List<Event> eventList;
    private SharedPreferenceHelper preferenceHelper;
    private SharedPreferences sharedPreferences;

    public class Event {
        private String name;
        private String description;

        public Event(String name, String description) {
            this.name = name;
            this.description = description;
        }
        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        preferenceHelper = new SharedPreferenceHelper(this); // Initialize SharedPreferenceHelper

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        textViewTotalCount = findViewById(R.id.textViewTotalCount);
        textViewEventCounts = new TextView[]{

                findViewById(R.id.textViewEventCount1),
                findViewById(R.id.textViewEventCount2),
                findViewById(R.id.textViewEventCount3)
        };

        // Initialize RecyclerView and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        adapter = new EventListAdapter(eventList);
        recyclerView.setAdapter(adapter);

        updateUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle) {
            toggleEventMode();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed(); // Up navigation
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEventMode() {
        isEventNameMode = !isEventNameMode;
        updateUI();
    }

    private void updateUI() {

        // 1) Retrieve counts
        int eventACount = preferenceHelper.getEventCount("Event1Count");
        int eventBCount = preferenceHelper.getEventCount("Event2Count");
        int eventCCount = preferenceHelper.getEventCount("Event3Count");

        // 2) Retrieve names from settings
        String name1 = preferenceHelper.getButtonName(1);
        String name2 = preferenceHelper.getButtonName(2);
        String name3 = preferenceHelper.getButtonName(3);

        // 3) Update TextViews with names + counts
        textViewEventCounts[0].setText("Count for " + name1 + ": " + eventACount);
        textViewEventCounts[1].setText("Count for " + name2 + ": " + eventBCount);
        textViewEventCounts[2].setText("Count for " + name3 + ": " + eventCCount);

        // 4) Update total count
        textViewTotalCount.setText("Total Count: " + getTotalCount());

        // 5) Update event list
        eventList.clear();

        if (isEventNameMode) {
            for (int i = 0; i < eventACount; i++) eventList.add(new Event(name1, ""));
            for (int i = 0; i < eventBCount; i++) eventList.add(new Event(name2, ""));
            for (int i = 0; i < eventCCount; i++) eventList.add(new Event(name3, ""));
        } else {
            for (int i = 0; i < eventACount; i++) eventList.add(new Event("1", ""));
            for (int i = 0; i < eventBCount; i++) eventList.add(new Event("2", ""));
            for (int i = 0; i < eventCCount; i++) eventList.add(new Event("3", ""));
        }

        adapter.notifyDataSetChanged();
    }


    private int getTotalCount() {
        int totalCount = 0;
        for (int i = 1; i <= 3; i++) {
            totalCount += preferenceHelper.getEventCount("Event" + i + "Count");
        }
        return totalCount;
    }
    private void resetCounts() {
        // Reset counts for each event
        preferenceHelper.resetEventCount("Event1Count");
        preferenceHelper.resetEventCount("Event2Count");
        preferenceHelper.resetEventCount("Event3Count");

        // Update UI
        for (TextView textView : textViewEventCounts) {
            textView.setText("Count: 0"); // Reset each event count TextView
        }
        textViewTotalCount.setText("Total Count: 0"); // Reset total count TextView
        eventList.clear(); // Clear the event list
        adapter.notifyDataSetChanged(); // Notify the adapter of data change
    }

    // EventListAdapter class for RecyclerView adapter
    private class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

        private List<Event> eventList;

        public EventListAdapter(List<Event> eventList) {
            this.eventList = eventList;
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            return new EventViewHolder(itemView);
        }

        @Override
        //OnBindViewHolder allows the RecycleViewer to access this information
        public void onBindViewHolder(EventViewHolder holder, int position) {
            Event event = eventList.get(position);
            holder.textViewName.setText(event.getName());
            holder.textViewDescription.setText(event.getDescription());
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public class EventViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewName;
            public TextView textViewDescription;

            public EventViewHolder(View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewDescription = itemView.findViewById(R.id.textViewDescription);
            }
        }
    }
}
