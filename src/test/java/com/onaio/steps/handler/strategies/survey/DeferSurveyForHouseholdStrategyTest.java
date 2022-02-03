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

import com.onaio.steps.StepsTestRunner;
import com.onaio.steps.activities.HouseholdActivity;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DeferSurveyForHouseholdStrategyTest extends StepsTestRunner {


    private Household household;
    private DeferSurveyForHouseholdStrategy deferSurveyForHouseholdStrategy;

    @Before
    public void Setup(){
        household = Mockito.mock(Household.class);
        HouseholdActivity householdActivity = Mockito.mock(HouseholdActivity.class);
        deferSurveyForHouseholdStrategy = new DeferSurveyForHouseholdStrategy(household, householdActivity);
    }

    @Test
    public void ShouldNotInactivateWhenSurveyIsDone(){
        Mockito.when(household.getStatus()).thenReturn(InterviewStatus.NOT_DONE);
        assertFalse(deferSurveyForHouseholdStrategy.shouldInactivate());
    }

    @Test
    public void ShouldInactivateWhenSurveyIsDone(){
        Mockito.when(household.getStatus()).thenReturn(InterviewStatus.DONE);
        assertTrue(deferSurveyForHouseholdStrategy.shouldInactivate());
    }

    @Test
    public void ShouldInactivateWhenSurveyIsRefused(){
        Mockito.when(household.getStatus()).thenReturn(InterviewStatus.REFUSED);
        assertTrue(deferSurveyForHouseholdStrategy.shouldInactivate());
    }

    @Test
    public void ShouldOpenAndUpdateStatus(){
        Mockito.when(household.getStatus()).thenReturn(InterviewStatus.REFUSED);
        deferSurveyForHouseholdStrategy.open();
        Mockito.verify(household).setStatus(InterviewStatus.DEFERRED);
        Mockito.verify(household).update(Mockito.any(DatabaseHelper.class));
    }




}