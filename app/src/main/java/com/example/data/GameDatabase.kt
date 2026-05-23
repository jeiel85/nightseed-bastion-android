package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM account_state WHERE id = 1 LIMIT 1")
    fun getAccountStateFlow(): Flow<AccountStateEntity?>

    @Query("SELECT * FROM account_state WHERE id = 1 LIMIT 1")
    suspend fun getAccountState(): AccountStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountState(state: AccountStateEntity)

    @Query("SELECT * FROM active_run WHERE id = 1 LIMIT 1")
    fun getActiveRunFlow(): Flow<ActiveRunEntity?>

    @Query("SELECT * FROM active_run WHERE id = 1 LIMIT 1")
    suspend fun getActiveRun(): ActiveRunEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveRun(run: ActiveRunEntity)

    @Query("DELETE FROM active_run WHERE id = 1")
    suspend fun clearActiveRun()
}

@Database(entities = [AccountStateEntity::class, ActiveRunEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "nightseed_bastion_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class GameRepository(private val gameDao: GameDao) {
    val accountStateFlow: Flow<AccountStateEntity?> = gameDao.getAccountStateFlow()
    val activeRunFlow: Flow<ActiveRunEntity?> = gameDao.getActiveRunFlow()

    suspend fun getAccountState(): AccountStateEntity {
        val state = gameDao.getAccountState()
        return if (state == null) {
            val default = AccountStateEntity()
            gameDao.insertAccountState(default)
            default
        } else {
            state
        }
    }

    suspend fun saveAccountState(state: AccountStateEntity) {
        gameDao.insertAccountState(state)
    }

    suspend fun getActiveRun(): ActiveRunEntity? {
        return gameDao.getActiveRun()
    }

    suspend fun saveActiveRun(run: ActiveRunEntity) {
        gameDao.insertActiveRun(run)
    }

    suspend fun clearActiveRun() {
        gameDao.clearActiveRun()
    }
}
