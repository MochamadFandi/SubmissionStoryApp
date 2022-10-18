package com.fanindo.submissionstoryapp.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fanindo.submissionstoryapp.data.local.entity.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAll(remoteKeys: List<RemoteKeys>)

    @Query("SELECT * from remote_keys where id = :id")
     fun getRemoteKeysId(id: String): RemoteKeys?

    @Query("DELETE from remote_keys")
     fun deleteRemoteKeys()
}