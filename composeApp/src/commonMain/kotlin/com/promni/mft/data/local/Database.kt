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
import com.promni.mft.data.local.entities.DefaultTotalRecoveryTime
import com.promni.mft.data.local.entities.ExpectedRecoveryEntity
import com.promni.mft.data.local.entities.FatigueLogEntity
import com.promni.mft.data.local.entities.MuscleEntity
import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.defaultMusclesNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

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
        getPrepopulationSql().forEach { sql ->
            connection.execSQL(sql)
        }
    }
}

// A flag to control whether predefined test data should be added to the database on creation.
private val ADD_PREDEFINED_TEST_DATA = true
private fun getPrepopulationSql(): List<String> {
    val prepopulationList = mutableListOf(getMusclePrepopulationSql())

    if (ADD_PREDEFINED_TEST_DATA) {
        val bicepsId = 1L
        val tricepsId = 2L

        val fatigueLogEntries = generateFatigueLogEntries(bicepsId) + generateFatigueLogEntries(tricepsId, offsetDays = 2)

        prepopulationList.add(getFatigueLogPrepopulationSql(fatigueLogEntries))
        prepopulationList.add(getExpectedRecoveryPrepopulationSql(fatigueLogEntries))
    }

    return prepopulationList
}

private fun getMusclePrepopulationSql() =
    defaultMusclesNames.mapIndexed { index, muscle ->
        // Use "index + 1" to start IDs from 1. An ID of 0 can cause issues with some database operations like updates.
        "(${index + 1}, '$muscle', NULL)"
    }.joinToString(
        prefix = "INSERT INTO muscles (id, name, totalRecoveryMillis) VALUES ",
        postfix = ";",
        separator = ", "
    )

private fun generateFatigueLogEntries(muscleId: MuscleId, offsetDays: Int = 0): List<FatigueLogEntity> {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val baseTime = now.minus(offsetDays, DateTimeUnit.DAY, timeZone)

    return listOf(
        FatigueLogEntity(muscleId = muscleId, value = 100f, timestamp = baseTime.toEpochMilliseconds()),
        FatigueLogEntity(muscleId = muscleId, value = 85f, timestamp = baseTime.minus(1, DateTimeUnit.DAY, timeZone).toEpochMilliseconds()),
        FatigueLogEntity(muscleId = muscleId, value = 90f, timestamp = baseTime.minus(4, DateTimeUnit.DAY, timeZone).toEpochMilliseconds()),
        FatigueLogEntity(muscleId = muscleId, value = 75f, timestamp = baseTime.minus(8, DateTimeUnit.DAY, timeZone).toEpochMilliseconds())
    )
}

private fun getFatigueLogPrepopulationSql(entries: List<FatigueLogEntity>): String {
    if (entries.isEmpty()) return ""
    return entries.joinToString(
        prefix = "INSERT INTO fatigue_logs (muscleId, value, timestamp) VALUES ",
        postfix = ";",
        separator = ", "
    ) { "(${it.muscleId}, ${it.value}, ${it.timestamp})" }
}

private fun getExpectedRecoveryPrepopulationSql(entries: List<FatigueLogEntity>): String {
    if (entries.isEmpty()) return ""

    val latestEntriesByMuscle = entries.groupBy { it.muscleId }
        .mapValues { (_, logs) ->
            logs.maxByOrNull { it.timestamp }
                ?: throw IllegalStateException("Cannot find latest log for a muscle")
        }

    return latestEntriesByMuscle.values.joinToString(
        prefix = "INSERT INTO expected_recovery (muscleId, timestamp, lastUpdated) VALUES ",
        postfix = ";",
        separator = ", "
    ) { latestEntry ->
        val totalRecoveryTime = DefaultTotalRecoveryTime
        val recoveryTimeForLog = (totalRecoveryTime * (latestEntry.value / 100f)).toLong()
        val expectedRecoveryTimestamp = latestEntry.timestamp + recoveryTimeForLog
        val lastUpdatedTimestamp = Clock.System.now().toEpochMilliseconds()
        "(${latestEntry.muscleId}, $expectedRecoveryTimestamp, $lastUpdatedTimestamp)"
    }
}
