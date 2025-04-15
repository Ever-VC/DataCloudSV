package com.evervc.datacloudsv.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.evervc.datacloudsv.models.RegisterAccount;

import java.util.List;

@Dao
public interface IAccountRegisterDAO {
    @Insert
    Long insertNewRegister(RegisterAccount registerAcount);
    @Update
    int updateRegister(RegisterAccount registerAcount);
    @Delete
    int deleteRegister(RegisterAccount registerAcount);
    @Query("SELECT * FROM RegisterAccount")
    List<RegisterAccount> getAllAccountRegisters();
}
