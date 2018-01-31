package com.khsoftsolutions.parenttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by myxroft2 on 9/8/17.
 *
 * Handles CRUD Operations.
 */

public class ChildManager extends ChildDB {


    private ContentValues cv;
    private SQLiteDatabase sq;

    public ChildManager(Context ct) {
        super(ct);
    }
}
