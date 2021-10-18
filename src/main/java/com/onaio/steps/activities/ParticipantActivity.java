/*
 * Copyright 2016. World Health Organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onaio.steps.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onaio.steps.R;
import com.onaio.steps.handler.factories.ParticipantActivityFactory;
import com.onaio.steps.handler.interfaces.IActivityResultHandler;
import com.onaio.steps.handler.interfaces.IMenuHandler;
import com.onaio.steps.handler.interfaces.IMenuPreparer;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Participant;

import java.util.List;

public class ParticipantActivity extends Activity{

    private DatabaseHelper db;
    private Participant participant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_participant);
        participant = (Participant) getIntent().getSerializableExtra(Constants.PARTICIPANT);
        db = new DatabaseHelper(this);
        styleActionBar();
        customizeOptions();
        prepareCustomMenu();
        populateParticipant();
        populateMessage();
    }

    private void customizeOptions() {
        Button cancelButton = (Button) findViewById(R.id.action_cancel_participant);
        cancelButton.setVisibility(View.GONE);
        Button takeSurveyButton = (Button) findViewById(R.id.action_take_survey);
        takeSurveyButton.setText(R.string.enter_data_now);
        Button deferredButton = (Button) findViewById(R.id.action_deferred);
        deferredButton.setText(R.string.enter_data_later);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareCustomMenu();
        populateParticipant();
    }

    private void populateParticipant() {
        TextView participantName = (TextView) findViewById(R.id.selected_participant_name);
        TextView participantDetails = (TextView) findViewById(R.id.selected_participant_details);
        participantName.setText(participant.getFormattedName()+" ("+this.getString(R.string.pid)+" "+participant.getParticipantID()+" )");
        participantDetails.setText(participant.getFormattedDetail(this));
    }

    private void populateMessage() {
        TextView viewById = (TextView) findViewById(R.id.survey_message);
        switch (participant.getStatus()){
            case SUBMITTED:
            case DONE: viewById.setText(R.string.interview_done_message);
                viewById.setTextColor(Color.parseColor(Constants.TEXT_GREEN));
                break;
            case INCOMPLETE_REFUSED: viewById.setText(R.string.interview_partially_completed);
                viewById.setTextColor(Color.RED);
                break;
            case REFUSED: viewById.setText(R.string.interview_refused_message);
                viewById.setTextColor(Color.RED);
                break;
            case NOT_REACHABLE: viewById.setText(R.string.interview_not_reachable_message);
                viewById.setTextColor(Color.RED);
                break;
            default: viewById.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<IMenuHandler> menuHandlers = ParticipantActivityFactory.getMenuHandlers(this, participant);
        for(IMenuHandler menuHandler:menuHandlers)
            if(menuHandler.shouldOpen(item.getItemId()))
                menuHandler.open();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.participant_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<IActivityResultHandler> menuHandlers = ParticipantActivityFactory.getResultHandlers(this, participant);
        for(IActivityResultHandler menuHandler:menuHandlers)
            if(menuHandler.canHandleResult(requestCode))
                menuHandler.handleResult(data, resultCode);
    }

    private void styleActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.ic_action_back);
        actionBar.setTitle(participant.getFormattedName());
    }

    public void handleCustomMenu(View view) {
        List<IMenuHandler> bottomMenuItem = ParticipantActivityFactory.getCustomMenuHandler(this, participant);
        for(IMenuHandler menuItem: bottomMenuItem)
            if(menuItem.shouldOpen(view.getId()))
                menuItem.open();
    }

    private void prepareCustomMenu() {
        List<IMenuPreparer> customMenus = ParticipantActivityFactory.getCustomMenuPreparer(this, participant);
        for(IMenuPreparer customMenu:customMenus)
            if(customMenu.shouldDeactivate())
                customMenu.deactivate();
            else
                customMenu.activate();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        List<IMenuPreparer> menuItemHandlers =ParticipantActivityFactory.getMenuPreparer(this, participant, menu);
        for(IMenuPreparer handler:menuItemHandlers)
            if(handler.shouldDeactivate())
                handler.deactivate();
        super.onPrepareOptionsMenu(menu);
        return true;
    }
}
