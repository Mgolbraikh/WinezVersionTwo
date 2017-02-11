package com.example.owner.winez.Utils.ModelSQL;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.owner.winez.Model.Entity;
import com.example.owner.winez.Model.Wine;

import java.util.List;

/**
 * Created by owner on 08-Feb-17.
 */

public abstract class EntitySQL<T extends Entity>  {
    protected abstract String getTable();
    protected abstract String getTableName();
    protected abstract String getTableID();


    public abstract void create(SQLiteDatabase db);
    public abstract List<T> getAllEntities(SQLiteDatabase db);
    public abstract void addEntity(SQLiteDatabase db, T toAdd);
    public void deleteAll(SQLiteDatabase db) {
        db.execSQL("delete from  " + this.getTable() + ";");
    }
    public void drop(SQLiteDatabase db){
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", this.getTable()});
            if (cursor.moveToFirst()) {
                db.execSQL("drop table " + this.getTable() + ";");
            }
            cursor.close();
        }catch (Exception e){
            Log.d("Exception:", e.getMessage());
        }
    }


}
