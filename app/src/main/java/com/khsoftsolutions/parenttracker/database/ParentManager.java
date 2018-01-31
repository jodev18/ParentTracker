package com.khsoftsolutions.parenttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.khsoftsolutions.parenttracker.objects.ParentObject;

/**
 * Created by myxroft2 on 9/8/17.
 *
 * Handles CRUD Operations.
 */

public class ParentManager extends ChildDB {

    private ContentValues cv;
    private SQLiteDatabase sq;

    public ParentManager(Context ct) {
        super(ct);
        this.cv = new ContentValues();
        this.sq = getWritableDatabase();
    }

    public long insertParent(ParentObject parent){

        this.cv.clear(); //Just to be sure

        //Initialize values
        this.cv.put(ParentTable.ADDRESS,parent.PARENT_ADDRESS);
        this.cv.put(ParentTable.PHONE_NUMBER,parent.PARENT_PHONE_NUMBER);
        this.cv.put(ParentTable.AGE,parent.PARENT_AGE);
        this.cv.put(ParentTable.FIRST_NAME,parent.PARENT_FIRST_NAME);
        this.cv.put(ParentTable.MIDDLE_NAME,parent.PARENT_MID_NAME);
        this.cv.put(ParentTable.LAST_NAME,parent.PARENT_LAST_NAME);

        long instat = this.sq.insert(ParentTable.TABLE_NAME,ParentTable.ID,this.cv);

        return instat;
    }

    public ParentObject getParent(){

        Cursor c = this.sq.rawQuery("SELECT * FROM " + ParentTable.TABLE_NAME,null);

        if(c.getCount() > 0){
            ParentObject pObj = new ParentObject();

            c.moveToNext();

            pObj.PARENT_PHONE_NUMBER = c.getString(c.getColumnIndex(ParentTable.PHONE_NUMBER));
            pObj.PARENT_ADDRESS = c.getString(c.getColumnIndex(ParentTable.ADDRESS));
            pObj.PARENT_AGE = c.getString(c.getColumnIndex(ParentTable.AGE));
            pObj.PARENT_FIRST_NAME = c.getString(c.getColumnIndex(ParentTable.FIRST_NAME));
            pObj.PARENT_MID_NAME = c.getString(c.getColumnIndex(ParentTable.MIDDLE_NAME));
            pObj.PARENT_FIRST_NAME = c.getString(c.getColumnIndex(ParentTable.LAST_NAME));

            return pObj;
        }
        else{
            return null;
        }

    }

    public void dropParent(){
        sq.execSQL("DELETE FROM " + ParentTable.TABLE_NAME);
    }

    public void cleanUp(){
        if(this.cv != null){
            this.cv.clear();
            if(this.sq != null){
                if(this.sq.isOpen()){
                    this.sq.close();
                }
            }
        }

    }
}
