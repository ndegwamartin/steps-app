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

package com.onaio.steps.utils;

import android.database.Cursor;

import com.onaio.steps.model.Gender;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.Member;

import org.mockito.Mockito;


public class CursorStub {
    private Cursor cursor;

    public CursorStub(Cursor cursor) {
        this.cursor = cursor;
    }

    public void stubCursorForHousehold(Household household) {
        Mockito.stub(cursor.moveToFirst()).toReturn(true);
        Mockito.stub(cursor.getColumnIndex(Household.NAME)).toReturn(1);
        Mockito.stub(cursor.getColumnIndex(Household.PHONE_NUMBER)).toReturn(2);
        Mockito.stub(cursor.getColumnIndex(Household.ID)).toReturn(3);
        Mockito.stub(cursor.getColumnIndex(Household.SELECTED_MEMBER_ID)).toReturn(4);
        Mockito.stub(cursor.getColumnIndex(Household.STATUS)).toReturn(5);
        Mockito.stub(cursor.getColumnIndex(Household.CREATED_AT)).toReturn(6);
        Mockito.stub(cursor.getColumnIndex(Household.SERVER_STATUS)).toReturn(7);

        Mockito.stub(cursor.getString(1)).toReturn(household.getName());
        Mockito.stub(cursor.getString(2)).toReturn(household.getPhoneNumber());
        Mockito.stub(cursor.getString(3)).toReturn(household.getId());
        Mockito.stub(cursor.getString(4)).toReturn(household.getSelectedMemberId());
        Mockito.stub(cursor.getString(5)).toReturn(household.getStatus().toString());
        Mockito.stub(cursor.getString(6)).toReturn(household.getCreatedAt());
        Mockito.stub(cursor.getString(7)).toReturn(household.getServerStatus().toString());
    }

    public void stubCursorForMember(long id, String memberFamilyName, String memberFirstName, Gender memberGender, String memberAge, String householdId, int isDeleted, String memberHouseholdId) {
        Mockito.stub(cursor.moveToFirst()).toReturn(true);
        Mockito.stub(cursor.getColumnIndex(Member.FAMILY_SURNAME)).toReturn(1);
        Mockito.stub(cursor.getColumnIndex(Member.FIRST_NAME)).toReturn(2);
        Mockito.stub(cursor.getColumnIndex(Member.GENDER)).toReturn(3);
        Mockito.stub(cursor.getColumnIndex(Member.AGE)).toReturn(4);
        Mockito.stub(cursor.getColumnIndex(Member.DELETED)).toReturn(5);
        Mockito.stub(cursor.getColumnIndex(Member.ID)).toReturn(6);
        Mockito.stub(cursor.getColumnIndex(Member.MEMBER_HOUSEHOLD_ID)).toReturn(7);
        Mockito.stub(cursor.getColumnIndex(Member.HOUSEHOLD_ID)).toReturn(8);

        Mockito.stub(cursor.getString(1)).toReturn(memberFamilyName);
        Mockito.stub(cursor.getString(2)).toReturn(memberFirstName);
        Mockito.stub(cursor.getString(3)).toReturn(memberGender.toString());
        Mockito.stub(cursor.getString(4)).toReturn(String.valueOf(memberAge));
        Mockito.stub(cursor.getInt(5)).toReturn(isDeleted);
        Mockito.stub(cursor.getString(6)).toReturn(String.valueOf(id));
        Mockito.stub(cursor.getString(7)).toReturn(memberHouseholdId);
        Mockito.stub(cursor.getString(8)).toReturn(householdId);
    }

    public void stubCursorForCount(int count) {
        Mockito.stub(cursor.getCount()).toReturn(count);
    }

}