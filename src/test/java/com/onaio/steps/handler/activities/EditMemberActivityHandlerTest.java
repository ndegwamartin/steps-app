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

package com.onaio.steps.handler.activities;


import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.onaio.steps.R;
import com.onaio.steps.StepsTestRunner;
import com.onaio.steps.activities.EditMemberActivity;
import com.onaio.steps.activities.MemberActivity;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;
import com.onaio.steps.model.Member;
import com.onaio.steps.model.RequestCode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

public class EditMemberActivityHandlerTest extends StepsTestRunner {

    private MemberActivity memberActivityMock;
    private Member memberMock;
    private EditMemberActivityHandler editMemberActivityHandler;

    @Before
    public void setup(){
        memberActivityMock = Mockito.mock(MemberActivity.class);
        memberMock= Mockito.mock(Member.class);
        editMemberActivityHandler = new EditMemberActivityHandler(memberActivityMock, memberMock);
    }

    @Test
    public void ShouldHandleResultForResultOkCode(){
        Intent intentMock = Mockito.mock(Intent.class);
        editMemberActivityHandler.handleResult(intentMock, Activity.RESULT_OK);
        Mockito.verify(memberActivityMock).finish();
    }

    @Test
    public void ShouldBeAbleToOpenEditMemberActivityWhenMenuIdMatches(){
        assertTrue(editMemberActivityHandler.shouldOpen(R.id.action_edit));
    }

    @Test
    public void ShouldNotBeAbleToOpenEditMemberActivityForOtheRMenuId(){
        assertFalse(editMemberActivityHandler.shouldOpen(R.id.action_settings));
    }

    @Test
    public void ShouldCheckWhetherResultForProperRequestCodeCanBeHandled(){
        assertTrue(editMemberActivityHandler.canHandleResult(RequestCode.EDIT_MEMBER.getCode()));
    }

    @Test
    public void ShouldCheckWhetherResultForOtherRequestCodeCanNotBeHandled(){
        assertFalse(editMemberActivityHandler.canHandleResult(RequestCode.NEW_MEMBER.getCode()));
    }

    @Test
    public void ShouldOpenWhenMemberIsNotNull(){
        editMemberActivityHandler.open();
        Mockito.verify(memberActivityMock).startActivityForResult(Mockito.argThat(matchIntent()), Mockito.eq(RequestCode.EDIT_MEMBER.getCode()));
    }

    private ArgumentMatcher<Intent> matchIntent() {
        return intent -> {
            Member actualMember = (Member) intent.getSerializableExtra(Constants.HH_MEMBER);
            Assert.assertEquals(memberMock, actualMember);
            Assert.assertEquals(EditMemberActivity.class.getName(),intent.getComponent().getClassName());
            return true;
        };
    }

    @Test
    public void ShouldInactivateEditOptionForSelectedMember(){
        Menu menuMock = Mockito.mock(Menu.class);
        Household household = new Household("1234", "any name", "123456789", "1", InterviewStatus.SELECTION_NOT_DONE, "", "uniqueDevId","Dummy comments", null);
        when(memberMock.getHousehold()).thenReturn(household);
        when(memberMock.getId()).thenReturn(1);

        assertTrue(editMemberActivityHandler.withMenu(menuMock).shouldDeactivate());
    }

    @Test
    public void ShouldInactivateWhenHouseholdIsSurveyed(){
        Menu menuMock = Mockito.mock(Menu.class);
        when(memberMock.getId()).thenReturn(1);
        when(memberMock.getHousehold()).thenReturn(new Household("12","name","321","", InterviewStatus.DONE,"12-12-2001", "uniqueDevId","Dummy comments", null));
        Assert.assertTrue(editMemberActivityHandler.withMenu(menuMock).shouldDeactivate());
    }

    @Test
    public void ShouldInactivateWhenSurveyIsRefused(){
        Menu menuMock = Mockito.mock(Menu.class);
        when(memberMock.getId()).thenReturn(1);
        when(memberMock.getHousehold()).thenReturn(new Household("12","name","321","", InterviewStatus.REFUSED,"12-12-2001", "uniqueDevId","Dummy comments", null));
        Assert.assertTrue(editMemberActivityHandler.withMenu(menuMock).shouldDeactivate());

    }

    @Test
    public void ShouldBeAbleToActivateEditOptionInMenuItem(){
        Menu menuMock = Mockito.mock(Menu.class);
        MenuItem menuItemMock = Mockito.mock(MenuItem.class);
        when(menuMock.findItem(R.id.action_edit)).thenReturn(menuItemMock);

        editMemberActivityHandler.withMenu(menuMock).activate();

        Mockito.verify(menuItemMock).setEnabled(true);
    }

    @Test
    public void ShouldBeAbleToInactivateEditOptionInMenuItem(){
        Menu menuMock = Mockito.mock(Menu.class);
        MenuItem menuItemMock = Mockito.mock(MenuItem.class);
        when(menuMock.findItem(R.id.action_edit)).thenReturn(menuItemMock);

        editMemberActivityHandler.withMenu(menuMock).deactivate();

        Mockito.verify(menuItemMock).setEnabled(false);
    }
    }

