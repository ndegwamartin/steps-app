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

import com.onaio.steps.StepsTestRunner;
import com.onaio.steps.activities.HouseholdActivity;
import com.onaio.steps.handler.strategies.survey.interfaces.IDoNotTakeSurveyStrategy;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class NotReachableSurveyForEmptyHouseholdStrategyTest extends StepsTestRunner {

    protected Household household;
    protected IDoNotTakeSurveyStrategy notReachableSurveyForEmptyHouseholdStrategy;

    @Before
    public void Setup(){
        household = Mockito.mock(Household.class);
        HouseholdActivity householdActivity = Mockito.mock(HouseholdActivity.class);
        notReachableSurveyForEmptyHouseholdStrategy = new NotReachableSurveyForEmptyHouseholdStrategy(household, householdActivity);
    }

    @Test
    public void ShouldInactivateWhenHouseholdIsNotEmpty(){
        Mockito.when(household.getStatus()).thenReturn(InterviewStatus.EMPTY_HOUSEHOLD);
        assertFalse(notReachableSurveyForEmptyHouseholdStrategy.shouldInactivate());
    }
}