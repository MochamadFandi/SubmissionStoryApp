package com.fanindo.submissionstoryapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fanindo.submissionstoryapp.data.local.entity.Story

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStory(story: Story)

    @Query("SELECT * from story")
    fun getAllStories(): PagingSource<Int, Story>

    @Query("DELETE from story")
    fun deleteAll()
}