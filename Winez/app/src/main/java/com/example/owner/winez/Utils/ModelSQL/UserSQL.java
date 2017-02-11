package com.example.owner.winez.Utils.ModelSQL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.example.owner.winez.Model.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by owner on 31-Jan-17.
 */


// TODO : Michael - Complite here all actions on table user and add others tables
public class UserSQL extends EntitySQL<User> {
    private static UserSQL _instance;

    private UserSQL(){}
    public static UserSQL getInstance(){
        if(_instance == null){
            _instance = new UserSQL();
        }
        return _instance;
    }

    @Override
    protected String getTable() {
        return "users";
    }

    @Override
    protected String getTableName() {
        return "name";
    }

    @Override
    protected String getTableID() {
        return "_id";
    }

    String getTableMail() {
        return "email";
    }

    public void create(SQLiteDatabase db) {
        db.execSQL("create table " + this.getTable() + " (" +
                this.getTableID() + " TEXT PRIMARY KEY," +
                this.getTableName() + " TEXT," +
                this.getTableMail() + " TEXT);");
    }

    @Override
    public List<User> getAllEntities(SQLiteDatabase db) {
        Cursor cursor = db.query(this.getTable(), null, null , null, null, null, null);
        List<User> users = new LinkedList<User>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(this.getTableID());
            int nameIndex = cursor.getColumnIndex(this.getTableName());
            int emailIndex = cursor.getColumnIndex(this.getTableMail());
            do {
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String email = cursor.getString(emailIndex);
                User st = new User(name,email, id);
                users.add(st);
            } while (cursor.moveToNext());
        }
        return users;
    }

    @Override
    public void addEntity(SQLiteDatabase db, User toAdd) {
        ContentValues values = new ContentValues();
        values.put(this.getTableID(), toAdd.getUid());
        values.put(this.getTableName(), toAdd.getName());
        values.put(this.getTableMail(), toAdd.getEmail());
        db.insertWithOnConflict(this.getTable(), this.getTableID(), values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Nullable
    public User getUser(SQLiteDatabase db, String id) {
        //String where = USER_TABLE_ID + " = ?";
        //String[] args = {id};
        Cursor cursor = db.query(this.getTable(), null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(this.getTableName());
            int nameIndex = cursor.getColumnIndex(this.getTableName());
            int mailIndex = cursor.getColumnIndex(this.getTableMail());
            String _id = cursor.getString(idIndex);
            String name = cursor.getString(nameIndex);
            String mail = cursor.getString(mailIndex);
            User usr = new User(name, mail, _id);
            return usr;
        }

        return null;
    }

}
