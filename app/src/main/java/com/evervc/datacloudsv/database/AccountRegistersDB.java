package com.evervc.datacloudsv.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.evervc.datacloudsv.dao.IAccountRegisterDAO;
import com.evervc.datacloudsv.models.RegisterAccount;

@Database(entities = {RegisterAccount.class}, version = 1, exportSchema = false)
public abstract class AccountRegistersDB extends RoomDatabase {
    public abstract IAccountRegisterDAO accountRegisterDAO();
    private static AccountRegistersDB INSTANCE;
    public static synchronized AccountRegistersDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AccountRegistersDB.class, "passwordManager")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
