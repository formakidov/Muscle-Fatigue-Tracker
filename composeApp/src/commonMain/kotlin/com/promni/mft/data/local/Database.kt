package com.promni.mft.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.promni.mft.data.local.dao.ExpectedRecoveryDao
import com.promni.mft.data.local.dao.FatigueLogDao
import com.promni.mft.data.local.dao.MuscleDao
import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.FatigueLogEntity
import com.promni.mft.data.local.entities.MuscleEntity
import com.promni.mft.defaultMusclesNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        MuscleEntity::class,
        FatigueLogEntity::class,
        ExpectedRecoveryEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun muscleDao(): MuscleDao
    abstract fun fatigueLogDao(): FatigueLogDao
    abstract fun expectedRecoveryDao(): ExpectedRecoveryDao
}


// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

const val BASE_DB_NAME = "user_db"

fun getDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
//        .addMigrations(MIGRATIONS)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .fallbackToDestructiveMigration(true)
        .addCallback(prepopulationCallback)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

private val prepopulationCallback = object : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        val sqlBuilder = StringBuilder("INSERT INTO muscles (id, name, totalRecoveryMillis) VALUES ")
        for ((index, muscle) in defaultMusclesNames.withIndex()) {
            sqlBuilder.append("($index, '$muscle', NULL)")
            if (index < defaultMusclesNames.size - 1) {
                sqlBuilder.append(", ")
            }
        }
        sqlBuilder.append(";")
        connection.execSQL(sql = sqlBuilder.toString())
    }
}
