package com.evervc.datacloudsv.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.evervc.datacloudsv.models.AccountRegister;

import java.util.List;

@Dao
public interface IAccountRegisterDAO {
    @Insert
    Long insertNewRegister(AccountRegister registerAcount);
    @Update
    int updateRegister(AccountRegister registerAcount);
    @Delete
    int deleteRegister(AccountRegister registerAcount);
    @Query("SELECT * FROM AccountRegister WHERE id = :id")
    AccountRegister getAccountRegisterById(int id);
    @Query("SELECT * FROM AccountRegister")
    List<AccountRegister> getAllAccountRegisters();
    @Query("SELECT * FROM AccountRegister WHERE title LIKE '%' || :title || '%'")
    List<AccountRegister> getAllAccountsByTitle(String title);
    @Query("SELECT * FROM AccountRegister ORDER BY title ASC")
    List<AccountRegister> getAllByTitleAsc();
    @Query("SELECT * FROM AccountRegister ORDER BY title DESC")
    List<AccountRegister> getAllByTitleDesc();
    @Query("SELECT * FROM AccountRegister ORDER BY createdAt DESC")
    List<AccountRegister> getAllByNewest();
    @Query("SELECT * FROM AccountRegister ORDER BY createdAt ASC")
    List<AccountRegister> getAllByOldest();
    @Query("DELETE FROM AccountRegister")
    int deleteAllRegisters();

}
