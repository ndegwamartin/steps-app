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

package com.onaio.steps.handler.actions;

import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.onaio.steps.R;
import com.onaio.steps.handler.interfaces.IMenuHandler;
import com.onaio.steps.handler.interfaces.IMenuPreparer;
import com.onaio.steps.helper.CustomDialog;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.InterviewStatus;
import com.onaio.steps.model.Member;

public class DeleteMemberHandler implements IMenuHandler,IMenuPreparer {
    private final CustomDialog dialog;
    private AppCompatActivity activity;
    private Member member;
    private static final int MENU_ID= R.id.action_member_delete;
    private Menu menu;

    public DeleteMemberHandler(AppCompatActivity activity, Member member) {
        this(activity, member, new CustomDialog());
    }

    DeleteMemberHandler(AppCompatActivity activity, Member member, CustomDialog dialog) {
        this.activity = activity;
        this.member = member;
        this.dialog = dialog;
    }

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id == MENU_ID;
    }

    @Override
    public boolean open() {
        DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                member.delete(new DatabaseHelper(activity));
                new BackHomeHandler(activity).open();
            }
        };
        dialog.confirm(activity, confirmListener, CustomDialog.EmptyListener, R.string.member_delete_confirm, R.string.confirm_ok);
        return true;
    }

    public DeleteMemberHandler withMenu(Menu menu) {
        this.menu = menu;
        return this;
    }

    @Override
    public boolean shouldDeactivate() {
        boolean isSelectedMember = String.valueOf(member.getId()).equals(member.getHousehold().getSelectedMemberId());
        boolean refusedHousehold = member.getHousehold().getStatus().equals(InterviewStatus.REFUSED);
        boolean surveyDone = member.getHousehold().getStatus().equals(InterviewStatus.DONE);
        return (isSelectedMember || refusedHousehold || surveyDone);
    }

    @Override
    public void deactivate() {
        MenuItem menuItem = menu.findItem(MENU_ID);
        menuItem.setEnabled(false);

    }

    @Override
    public void activate() {
        MenuItem menuItem = menu.findItem(MENU_ID);
        menuItem.setEnabled(true);

    }
}
