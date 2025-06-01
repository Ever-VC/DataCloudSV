package com.evervc.datacloudsv.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class AccountRegister {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    @NonNull
    private String title;
    @ColumnInfo
    @Nullable
    private String acount;
    @ColumnInfo
    @NonNull
    private String username;
    @ColumnInfo
    @NonNull
    private String password;
    @ColumnInfo
    @Nullable
    private String website;
    @ColumnInfo
    @Nullable
    private String notes;
    @ColumnInfo
    @NonNull
    private Long createdAt;
    @ColumnInfo
    @Nullable
    private Long modifiedAt;
    @ColumnInfo
    @NonNull
    private String saltBase64;
    @ColumnInfo
    @NonNull
    private String ivBase64;

    public AccountRegister(@NonNull String title, @Nullable String acount, @NonNull String username,
                           @NonNull String password, @Nullable String website, @Nullable String notes,
                           @NonNull Long createdAt, @Nullable Long modifiedAt, @NonNull String saltBase64,
                           @NonNull String ivBase64) {
        this.title = title;
        this.acount = acount;
        this.username = username;
        this.password = password;
        this.website = website;
        this.notes = notes;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.saltBase64 = saltBase64;
        this.ivBase64 = ivBase64;
    }

    public AccountRegister() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getAcount() {
        return acount;
    }

    public void setAcount(@Nullable String acount) {
        this.acount = acount;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    public void setWebsite(@Nullable String website) {
        this.website = website;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    @NonNull
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull Long createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public Long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(@Nullable Long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @NonNull
    public String getSaltBase64() {
        return saltBase64;
    }

    public void setSaltBase64(@NonNull String saltBase64) {
        this.saltBase64 = saltBase64;
    }

    @NonNull
    public String getIvBase64() {
        return ivBase64;
    }

    public void setIvBase64(@NonNull String ivBase64) {
        this.ivBase64 = ivBase64;
    }
}
