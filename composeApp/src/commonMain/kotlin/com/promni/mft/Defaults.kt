package com.promni.mft

import kmpmft.composeapp.generated.resources.Res
import kmpmft.composeapp.generated.resources.abs
import kmpmft.composeapp.generated.resources.biceps
import kmpmft.composeapp.generated.resources.calves
import kmpmft.composeapp.generated.resources.calves_front
import kmpmft.composeapp.generated.resources.chest
import kmpmft.composeapp.generated.resources.forearm
import kmpmft.composeapp.generated.resources.glutes
import kmpmft.composeapp.generated.resources.hams
import kmpmft.composeapp.generated.resources.lats
import kmpmft.composeapp.generated.resources.lower_back
import kmpmft.composeapp.generated.resources.neck
import kmpmft.composeapp.generated.resources.outer_thigh
import kmpmft.composeapp.generated.resources.quads
import kmpmft.composeapp.generated.resources.shoulder
import kmpmft.composeapp.generated.resources.side_abs
import kmpmft.composeapp.generated.resources.traps
import kmpmft.composeapp.generated.resources.triceps
import org.jetbrains.compose.resources.DrawableResource

data class DefaultMuscle(
    val stringId: String,
    val name: String,
    val image: DrawableResource,
    val order: Int,
    val recoveryMillis: Long = DefaultRecoveryTimeMillis,
)


const val DefaultRecoveryTimeMillis = 4 * 24 * 60 * 60 * 1000L // 4 days

val defaultMuscles = listOf(
    DefaultMuscle(stringId = "neck", name = "Neck", image = Res.drawable.neck, order = 0),
    DefaultMuscle(stringId = "traps", name = "Traps", image = Res.drawable.traps, order = 1),
    DefaultMuscle(stringId = "shoulders", name = "Shoulders", image = Res.drawable.shoulder, order = 2),
    DefaultMuscle(stringId = "chest", name = "Chest", image = Res.drawable.chest, order = 3),
    DefaultMuscle(stringId = "biceps", name = "Biceps", image = Res.drawable.biceps, order = 4),
    DefaultMuscle(stringId = "triceps", name = "Triceps", image = Res.drawable.triceps, order = 5),
    DefaultMuscle(stringId = "forearms", name = "Forearms", image = Res.drawable.forearm, order = 6),
    DefaultMuscle(stringId = "abs", name = "Abs", image = Res.drawable.abs, order = 7),
    DefaultMuscle(stringId = "side_abs", name = "Side Abs", image = Res.drawable.side_abs, order = 8),
    DefaultMuscle(stringId = "lats", name = "Lats", image = Res.drawable.lats, order = 9),
    DefaultMuscle(stringId = "lower_back", name = "Lower Back", image = Res.drawable.lower_back, order = 10),
    DefaultMuscle(stringId = "glutes", name = "Glutes", image = Res.drawable.glutes, order = 11),
    DefaultMuscle(stringId = "outer_thigh", name = "Outer Thigh", image = Res.drawable.outer_thigh, order = 12),
    DefaultMuscle(stringId = "quadriceps", name = "Quadriceps", image = Res.drawable.quads, order = 13),
    DefaultMuscle(stringId = "hamstrings", name = "Hamstrings", image = Res.drawable.hams, order = 14),
    DefaultMuscle(stringId = "calves", name = "Calves", image = Res.drawable.calves, order = 15),
    DefaultMuscle(stringId = "front_calves", name = "Front Calves", image = Res.drawable.calves_front, order = 16)
)

val muscleImageMap: Map<String, DrawableResource> = defaultMuscles.associate { it.name to it.image }
