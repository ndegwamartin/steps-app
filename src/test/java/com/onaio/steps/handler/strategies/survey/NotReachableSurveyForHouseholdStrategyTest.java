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

package com.onaio.steps.handler.strategies.survey;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.onaio.steps.activities.HouseholdActivity;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 16,manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class NotReachableSurveyForHouseholdStrategyTest {

    private Household household;
    private HouseholdActivity householdActivity;
    private NotReachableSurveyForHouseholdStrategy notReachableSurveyForHouseholdStrategy;

    @Before
    public void Setup(){
        household = Mockito.mock(Household.class);
        householdActivity = Mockito.mock(HouseholdActivity.class);
        notReachableSurveyForHouseholdStrategy = new NotReachableSurveyForHouseholdStrategy(household, householdActivity);
    }

    @Test
    public void ShouldNotInactivateWhenMemberIsSelected(){
        Mockito.stub(household.getStatus()).toReturn(InterviewStatus.NOT_DONE);
        assertFalse(notReachableSurveyForHouseholdStrategy.shouldInactivate());
    }

    @Test
    public void ShouldInactivateWhenSurveyIsDone(){
        Mockito.stub(household.getStatus()).toReturn(InterviewStatus.DONE);
        assertTrue(notReachableSurveyForHouseholdStrategy.shouldInactivate());
    }

    @Test
    public void ShouldInactivateWhenSurveyIsDeferred(){
        Mockito.stub(household.getStatus()).toReturn(InterviewStatus.DEFERRED);
        assertFalse(notReachableSurveyForHouseholdStrategy.shouldInactivate());
    }

    @Test
    public void ShouldOpenAndUpdateStatus(){
        Mockito.stub(household.getStatus()).toReturn(InterviewStatus.DEFERRED);
        notReachableSurveyForHouseholdStrategy.open();
        Mockito.verify(household).setStatus(InterviewStatus.NOT_REACHABLE);
        Mockito.verify(household).update(Mockito.any(DatabaseHelper.class));
    }

}