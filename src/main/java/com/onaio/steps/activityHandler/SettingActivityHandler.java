package com.onaio.steps.activityHandler;

import android.app.ListActivity;
import android.content.Intent;
import com.onaio.steps.R;
import com.onaio.steps.activity.SettingsActivity;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.helper.KeyValueStore;
import com.onaio.steps.helper.KeyValueStoreFactory;

import static android.app.Activity.RESULT_OK;
import static com.onaio.steps.helper.Constants.ENDPOINT_URL;
import static com.onaio.steps.helper.Constants.PHONE_ID;


public class SettingActivityHandler implements IHandler {

    private ListActivity activity;

    public SettingActivityHandler(ListActivity activity) {

        this.activity = activity;
    }

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id == R.id.action_settings;
    }

    @Override
    public boolean open() {
        Intent intent = new Intent(activity.getBaseContext(), SettingsActivity.class);
        intent.putExtra(PHONE_ID,getPhoneId(activity));
        intent.putExtra(ENDPOINT_URL,getEndpointUrl(activity));
        activity.startActivityForResult(intent, Constants.SETTING_IDENTIFIER);
        return true;
    }

    @Override
    public boolean canHandleResult(int requestCode) {
        return requestCode == Constants.SETTING_IDENTIFIER;
    }

    @Override
    public void handleResult(Intent data, int resultCode) {
        if (resultCode == RESULT_OK)
            handleSuccess(activity, data);
    }

    private void handleSuccess(ListActivity activity, Intent data) {
        String phoneId = data.getStringExtra(PHONE_ID);
        String endpointUrl = data.getStringExtra(ENDPOINT_URL);
        KeyValueStore keyValueStore = KeyValueStoreFactory.instance(activity);
        if (!keyValueStore.putString(PHONE_ID, phoneId))
            saveSettingsErrorHandler(PHONE_ID);
        if (!keyValueStore.putString(ENDPOINT_URL, endpointUrl))
            saveSettingsErrorHandler(ENDPOINT_URL);
    }

    private void saveSettingsErrorHandler(String field) {
        //TODO: toast message for save phone id failure
    }

    private String getPhoneId(ListActivity activity) {
        return KeyValueStoreFactory.instance(activity).getString(PHONE_ID) ;
    }

    private String getEndpointUrl(ListActivity activity) {
        return KeyValueStoreFactory.instance(activity).getString(ENDPOINT_URL) ;
    }

}
