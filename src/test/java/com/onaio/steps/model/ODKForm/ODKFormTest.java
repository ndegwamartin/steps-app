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

package com.onaio.steps.model.ODKForm;

import static com.onaio.steps.helper.Constants.HH_PHONE_ID;
import static org.robolectric.Shadows.shadowOf;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.onaio.steps.StepsTestRunner;
import com.onaio.steps.activities.HouseholdActivity;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.helper.FileUtil;
import com.onaio.steps.helper.KeyValueStoreFactory;
import com.onaio.steps.model.Gender;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;
import com.onaio.steps.model.Member;
import com.onaio.steps.model.ODKForm.strategy.HouseholdMemberFormStrategy;
import com.onaio.steps.model.RequestCode;
import com.onaio.steps.model.ShadowDatabaseHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Config(shadows = {ShadowDatabaseHelper.class})
public class ODKFormTest extends StepsTestRunner {
    private HouseholdActivity householdActivity;
    private Member selectedMember;
    private Household householdMock;
    private ODKForm odkForm;
    private IForm blankFormMock;
    private IForm savedFormMock;

    @Before
    public void Setup(){
        stubHousehold();
        stubFileUtil();

        Intent intent = new Intent();
        Mockito.when(householdMock.getPhoneNumber()).thenReturn("8050342");
        Mockito.when(householdMock.getComments()).thenReturn("dummy comments");
        intent.putExtra(Constants.HH_HOUSEHOLD,householdMock);

        householdActivity = Robolectric.buildActivity(HouseholdActivity.class, intent).create().get();
        blankFormMock = Mockito.mock(IForm.class);
        savedFormMock = Mockito.mock(IForm.class);
    }

   /* @Test
    public void ShouldSaveFileWhenOpeningSavedForm() throws IOException {
        String blankFormMediaPath = householdActivity.getFilesDir().getPath();
        String householdName = "household name";
        Mockito.when(householdMock.getName()).thenReturn(householdName);
        Mockito.when(blankFormMock.getPath()).thenReturn(blankFormMediaPath);
        odkForm = new ODKForm(blankFormMock, savedFormMock);


        odkForm.open(new HouseholdMemberFormStrategy(householdMock), householdActivity, RequestCode.SURVEY.getCode());

//      Mockito.verify(fileUtilMock).withHeader(Constants.ODK_FORM_FIELDS.split(","));
        Mockito.verify(blankFormMock).getPath();
        String formNameFormat = getValue(Constants.FORM_ID) + "-%s";
        String formName = String.format(formNameFormat, householdName);
        Mockito.verify(fileUtilMock).withData(Mockito.argThat(formDataValidator(formName)));
        Mockito.verify(fileUtilMock).writeCSV(blankFormMediaPath + "/" + Constants.ODK_DATA_FILENAME);
    }*/

    @Test
    public void ShouldPopulateIntentProperlyWhenOpeningSavedForm() throws IOException {
        String blankFormMediaPath = householdActivity.getFilesDir().getPath();
        String householdName = "household name";
        Uri saveFormURI = Uri.parse("uri");
        Mockito.when(householdMock.getName()).thenReturn(householdName);
        Mockito.when(blankFormMock.getPath()).thenReturn(blankFormMediaPath);
        Mockito.when(savedFormMock.getUri()).thenReturn(saveFormURI);
        odkForm = new ODKForm(blankFormMock, savedFormMock);
        String deviceId = getValue(HH_PHONE_ID);

        odkForm.open(new HouseholdMemberFormStrategy(householdMock, deviceId), householdActivity, RequestCode.SURVEY.getCode());

        ShadowActivity.IntentForResult odkActivity = shadowOf(householdActivity).getNextStartedActivityForResult();

        Intent intent = odkActivity.intent;
        ComponentName component = intent.getComponent();

        Assert.assertEquals(Constants.ODK_COLLECT_PACKAGE,component.getPackageName());
        Assert.assertEquals(Constants.ODK_COLLECT_FORM_CLASS, component.getClassName());
        Assert.assertEquals(Intent.ACTION_EDIT,intent.getAction());
        Assert.assertEquals(saveFormURI,intent.getData());
        Assert.assertEquals(RequestCode.SURVEY.getCode(),odkActivity.requestCode);
    }

   /* @Test
    public void ShouldSaveFileWhenOpeningBlankForm() throws IOException {
        String blankFormMediaPath = householdActivity.getFilesDir().getPath();
        String householdName = "household name";
        Mockito.when(householdMock.getName()).thenReturn(householdName);
        Mockito.when(blankFormMock.getPath()).thenReturn(blankFormMediaPath);
        odkForm = new ODKForm(blankFormMock, null);


        odkForm.open(new HouseholdMemberFormStrategy(householdMock), householdActivity, RequestCode.SURVEY.getCode());

        Mockito.verify(fileUtilMock).withHeader(Constants.ODK_FORM_FIELDS.split(","));
        Mockito.verify(blankFormMock).getPath();
        String formNameFormat = getValue(Constants.FORM_ID) + "-%s";
        String formName = String.format(formNameFormat, householdName);
        Mockito.verify(fileUtilMock).withData(Mockito.argThat(formDataValidator(formName)));
        Mockito.verify(fileUtilMock).writeCSV(blankFormMediaPath + "/" + Constants.ODK_DATA_FILENAME);
    }*/

    @Test
    public void ShouldPopulateIntentProperlyWhenOpeningBlankForm() throws IOException {
        String blankFormMediaPath = householdActivity.getFilesDir().getPath();
        String householdName = "household name";
        Uri blankFormURI = Uri.parse("uri");
        Mockito.when(householdMock.getName()).thenReturn(householdName);
        Mockito.when(blankFormMock.getPath()).thenReturn(blankFormMediaPath);
        Mockito.when(blankFormMock.getUri()).thenReturn(blankFormURI);
        odkForm = new ODKForm(blankFormMock, null);
        String deviceId = getValue(HH_PHONE_ID);

        odkForm.open(new HouseholdMemberFormStrategy(householdMock, deviceId), householdActivity, RequestCode.SURVEY.getCode());

        ShadowActivity.IntentForResult odkActivity = shadowOf(householdActivity).getNextStartedActivityForResult();

        Intent intent = odkActivity.intent;
        ComponentName component = intent.getComponent();

        Assert.assertEquals(Constants.ODK_COLLECT_PACKAGE,component.getPackageName());
        Assert.assertEquals(Constants.ODK_COLLECT_FORM_CLASS, component.getClassName());
        Assert.assertEquals(Intent.ACTION_EDIT,intent.getAction());
        Assert.assertEquals(blankFormURI,intent.getData());
        Assert.assertEquals(RequestCode.SURVEY.getCode(),odkActivity.requestCode);
    }

    private void stubFileUtil() {
        FileUtil fileUtilMock = Mockito.mock(FileUtil.class);
        Mockito.when(fileUtilMock.withData(Mockito.any(String[].class))).thenReturn(fileUtilMock);
        Mockito.when(fileUtilMock.withHeader(Mockito.any(String[].class))).thenReturn(fileUtilMock);
    }

    private void stubHousehold() {
        householdMock = Mockito.mock(Household.class);
        selectedMember = new Member(1, "surname", "firstName", Gender.Male, 28, householdMock, "householdID-1", false);
        Mockito.when(householdMock.getSelectedMember(Mockito.any(DatabaseHelper.class))).thenReturn(selectedMember);
        Mockito.when(householdMock.getStatus()).thenReturn(InterviewStatus.SELECTION_NOT_DONE);
    }

    private String getValue(String key) {
        return KeyValueStoreFactory.instance(householdActivity).getString(key) ;
    }

    private ArgumentMatcher<String[]> formDataValidator(final String formName) {
        return formData -> {
            List<String> formDataList = Arrays.asList(formData);
            Assert.assertTrue(formDataList.contains(Constants.ODK_HH_ID));
            Assert.assertTrue(formDataList.contains(formName));
            Assert.assertTrue(formDataList.contains(selectedMember.getMemberHouseholdId()));
            Assert.assertTrue(formDataList.contains(selectedMember.getFamilySurname()));
            Assert.assertTrue(formDataList.contains(selectedMember.getFirstName()));
            Assert.assertTrue(formDataList.contains("1"));
            Assert.assertTrue(formDataList.contains(String.valueOf(selectedMember.getAge())));
            return true;
        };
    }
}