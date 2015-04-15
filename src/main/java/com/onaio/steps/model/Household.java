package com.onaio.steps.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.onaio.steps.helper.CursorHelper;
import com.onaio.steps.helper.DatabaseHelper;

import java.io.Serializable;
import java.util.List;

public class Household implements Serializable {
    private static String FIND_BY_ID_QUERY = "SELECT * FROM HOUSEHOLD WHERE id = %d";
    public static final String FIND_BY_NAME_QUERY = "SELECT * FROM HOUSEHOLD WHERE %s = '%s'";
    private static String FIND_ALL_QUERY = "SELECT * FROM HOUSEHOLD ORDER BY Id desc";
    private static String FIND_ALL_COUNT_QUERY = "SELECT count(*) FROM HOUSEHOLD ORDER BY Id desc";
    public static final String TABLE_NAME = "household";
    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String STATUS = "Status";
    public static final String PHONE_NUMBER = "Phone_Number";
    public static final String SELECTED_MEMBER_ID = "selected_member_id";
    public static final String CREATED_AT = "Created_At";
    public static final String TABLE_CREATE_QUERY = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT)", TABLE_NAME, ID, NAME, PHONE_NUMBER, SELECTED_MEMBER_ID,STATUS, CREATED_AT);

    String id;
    String name;
    String phoneNumber;
    HouseholdStatus status;
    String selectedMemberId;
    String createdAt;
    
    public Household(String id, String name, String phoneNumber, String selectedMemberId, HouseholdStatus status, String createdAt) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.selectedMemberId = selectedMemberId;
        this.status = status;
        this.createdAt=createdAt;
    }

    public Household(String name, String phoneNumber, HouseholdStatus status, String createdAt) {
        this.name= name;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.createdAt=createdAt;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public HouseholdStatus getStatus() {
        return status;
    }

    public void setStatus(HouseholdStatus status) {
        this.status = status;
    }

    public void setSelectedMemberId(String selectedMemberId) {
        this.selectedMemberId = selectedMemberId;
    }

    public String getSelectedMemberId() {
        return selectedMemberId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Member getSelectedMember(DatabaseHelper db){
        if(selectedMemberId == null)
            return null;
        return findMember(db,Long.parseLong(selectedMemberId));
    }

    public long save(DatabaseHelper db){
        ContentValues householdValues = populateWithBasicDetails();
        householdValues.put(CREATED_AT, createdAt);

        long savedId = db.save(householdValues, TABLE_NAME);
        if(savedId != -1)
            id = String.valueOf(savedId);
        return savedId;
    }

    public long update(DatabaseHelper db){
        ContentValues householdValues = populateWithBasicDetails();
        householdValues.put(SELECTED_MEMBER_ID, selectedMemberId);
        return db.update(householdValues, TABLE_NAME,ID +" = "+getId(),null);
    }

    private ContentValues populateWithBasicDetails() {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(PHONE_NUMBER,phoneNumber);
        values.put(STATUS,status.toString());
        return values;
    }

    public static Household find_by(DatabaseHelper db, Long id) {
        Cursor cursor = db.exec(String.format(FIND_BY_ID_QUERY,id));
        Household household = new CursorHelper().getHouseholds(cursor).get(0);
        db.close();
        return household;
    }

    public static Household find_by(DatabaseHelper db, String name) {
        Cursor cursor = db.exec(String.format(FIND_BY_NAME_QUERY,NAME,name));
        List<Household> households = new CursorHelper().getHouseholds(cursor);
        db.close();
        if(households.isEmpty())
            return null;
        return households.get(0);
    }

    public static List<Household> getAll(DatabaseHelper db){
        Cursor cursor = db.exec(FIND_ALL_QUERY);
        List<Household> households = new CursorHelper().getHouseholds(cursor);
        db.close();
        return households;
    }

    public static int getAllCount(DatabaseHelper db){
        Cursor cursor = db.exec(FIND_ALL_COUNT_QUERY);
        cursor.moveToFirst();
        int householdCounts = cursor.getInt(0);
        db.close();
        return householdCounts;
    }

    public List<Member> getAllUnselectedMembers(DatabaseHelper db){
        if(getSelectedMemberId()==null)
            return getAllNonDeletedMembers(db);
        String query = String.format(Member.FIND_ALL_UNSELECTED_QUERY, Member.HOUSEHOLD_ID, this.getId(), Member.DELETED, Member.NOT_DELETED_INT, Member.ID, getSelectedMemberId());
        return getMembers(db,query);
    }

    public List<Member> getAllNonDeletedMembers(DatabaseHelper db){
        String query = String.format(Member.FIND_ALL_QUERY, Member.HOUSEHOLD_ID, this.getId(), Member.DELETED, Member.NOT_DELETED_INT, Member.ID, getSelectedMemberId());
        return getMembers(db,query);
    }

    public List<Member> getAllMembersForExport(DatabaseHelper db){
        String query = String.format(Member.FIND_ALL_WITH_DELETED_QUERY, Member.HOUSEHOLD_ID, this.getId());
        return getMembers(db, query);
    }

    public Member findMember(DatabaseHelper db, Long id) {
        String query = String.format(Member.FIND_BY_ID_QUERY, id);
        return getMembers(db,query).get(0);
    }


    private List<Member> getMembers(DatabaseHelper db, String query) {
        Cursor cursor = db.exec(query);
        List<Member> members = new CursorHelper().getMembers(cursor, this);
        db.close();
        return members;
    }



    public int numberOfNonDeletedMembers(DatabaseHelper db){
        String query = String.format(Member.FIND_ALL_QUERY, Member.HOUSEHOLD_ID, getId(), Member.DELETED, Member.NOT_DELETED_INT);
        return getCount(db, query);
    }

    public int numberOfNonSelectedMembers(DatabaseHelper db){
        if(getSelectedMemberId()==null)
            return numberOfNonDeletedMembers(db);
        String query = String.format(Member.FIND_ALL_UNSELECTED_QUERY, Member.HOUSEHOLD_ID, this.getId(), Member.DELETED, Member.NOT_DELETED_INT, Member.ID, getSelectedMemberId());
        return getCount(db, query);
    }

    public int getCount(DatabaseHelper db, String query) {
        Cursor cursor = db.exec(query);
        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public int numberOfMembers(DatabaseHelper db){
        Cursor cursor = db.exec(String.format(Member.FIND_ALL_WITH_DELETED_QUERY,Member.HOUSEHOLD_ID,getId()));
        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}
