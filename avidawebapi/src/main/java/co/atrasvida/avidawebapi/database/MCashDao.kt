package co.atrasvida.avidawebapi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MCashDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: MCash)

    @Query("delete from m_cash")
    fun deleteAll(): Int

    @Query("select * from m_cash limit 1")
    fun getOneById(): MCash?

    @Query("select * from m_cash where m_cash.token= :mToken limit 1")
    fun getObjByToken(mToken: String): MCash?

    @Query("UPDATE m_cash SET  data_val = :vae where m_cash.token= :mToken ")
    fun updatePresentToday(mToken: String, vae: String): Int
}