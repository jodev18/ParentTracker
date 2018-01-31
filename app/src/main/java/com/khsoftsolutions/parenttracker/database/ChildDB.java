package com.khsoftsolutions.parenttracker.database;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by myxroft2 on 9/8/17.
 */

public class ChildDB extends SQLiteOpenHelper {

    private static final Integer VERSION = 1;
    private static final String DB_NAME = "child_db";

    public ChildDB(Context ct){
        super(ct,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(ChildTable.TABLE_CREATE);
        sqLiteDatabase.execSQL(ParentTable.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    protected class ChildTable{

        public static final String TABLE_NAME = "tbl_child_info";

        public static final String ID = "child_id";

        public static final String FIRST_NAME = "child_fn";

        public static final String MIDDLE_NAME = "child_mn";

        public static final String LAST_NAME = "child_ln";

        public static final String AGE = "child_age";

        public static final String DOB =  "child_dob";

        public static final String GENDER = "child_gender";

        public static final String ADDRESS = "child_address";

        public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
                + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIRST_NAME + " TEXT,"
                + MIDDLE_NAME + " TEXT,"
                + LAST_NAME + " TEXT,"
                + AGE + " TEXT,"
                + DOB + " TEXT,"
                + GENDER + " TEXT,"
                + ADDRESS + " TEXT);";

    }

    protected class ParentTable{

        public static final String TABLE_NAME = "tbl_parent_info";

        public static final String ID = "parent_id";

        public static final String FIRST_NAME = "parent_fn";

        public static final String MIDDLE_NAME = "parent_mn";

        public static final String LAST_NAME = "parent_ln";

        public static final String AGE = "parent_age";

        public static final String PHONE_NUMBER = "parent_phonenum";

        public static final String ADDRESS = "parent_address";

        public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIRST_NAME + " TEXT,"
                + MIDDLE_NAME + " TEXT,"
                + LAST_NAME + " TEXT,"
                + AGE + " TEXT,"
                + PHONE_NUMBER + " TEXT,"
                + ADDRESS + " TEXT);";

    }

    /*
    //Disabled since login is already handled by Parse Server
    protected class LoginTable{

        public static final String TABLE_NAME = "tbl_login";
        public static final String ID = "login_id";
        public static final String USERNAME = "login_user";
        public static final String PASSWORD = "login_pass";

    }*/

}
