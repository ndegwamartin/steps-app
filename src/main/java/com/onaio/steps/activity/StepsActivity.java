package com.onaio.steps.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.onaio.steps.R;
import com.onaio.steps.activityHandler.ActivityHandlerFactory;
import com.onaio.steps.activityHandler.IHandler;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Household;

import java.util.ArrayList;
import java.util.List;

public class StepsActivity extends ListActivity {

    private DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        populateHouseholds();
        bindHouseholdItems();
    }

    private void bindHouseholdItems() {
        ListView households = getListView();
        households.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String householdName = ((TextView) view).getText().toString();
                Household household = Household.find_by(db, householdName);
                ActivityHandlerFactory.getHouseholdItemHandler(StepsActivity.this, household).open();
            }
        });
    }

    private void populateHouseholds() {
        db = new DatabaseHelper(getApplicationContext());
        getListView().setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fetchHouseholds()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<IHandler> activityHandlers = ActivityHandlerFactory.getMainMenuHandlers(this);
        for(IHandler handler : activityHandlers){
            if(handler.shouldOpen(item.getItemId()))
                return handler.open();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<IHandler> activityHandlers = ActivityHandlerFactory.getMainMenuHandlers(this);
        for(IHandler activityHandler: activityHandlers){
            if(activityHandler.canHandleResult(requestCode))
                activityHandler.handleResult(data,resultCode);
        }
    }

    private List<String> fetchHouseholds() {
        List<Household> households = Household.getAll(db);
        List<String> householdNames = new ArrayList<String>();
        for(Household household: households)
            householdNames.add(household.getName());
        return householdNames;
    }
}
