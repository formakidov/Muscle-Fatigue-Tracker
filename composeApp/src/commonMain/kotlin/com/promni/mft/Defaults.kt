package com.promni.mft

import kmpmft.composeapp.generated.resources.Res
import kmpmft.composeapp.generated.resources.abs
import kmpmft.composeapp.generated.resources.biceps
import kmpmft.composeapp.generated.resources.calves
import kmpmft.composeapp.generated.resources.chest
import kmpmft.composeapp.generated.resources.forearm
import kmpmft.composeapp.generated.resources.glutes
import kmpmft.composeapp.generated.resources.hams
import kmpmft.composeapp.generated.resources.lats
import kmpmft.composeapp.generated.resources.lower_back
import kmpmft.composeapp.generated.resources.quads
import kmpmft.composeapp.generated.resources.shoulder
import kmpmft.composeapp.generated.resources.traps
import kmpmft.composeapp.generated.resources.triceps
import org.jetbrains.compose.resources.DrawableResource

val defaultMusclesNames = listOf(
    "Biceps",
    "Triceps",
    "Chest",
    "Shoulders",
    "Abdomen",
    "Quadriceps",
    "Hamstrings",
    "Glutes",
    "Calves",
    "Forearms",
    "Traps",
    "Lats",
    "Lower Back"
)

val defaultMuscleImages = listOf(
    Res.drawable.biceps,
    Res.drawable.triceps,
    Res.drawable.chest,
    Res.drawable.shoulder,
    Res.drawable.abs,
    Res.drawable.quads,
    Res.drawable.hams,
    Res.drawable.glutes,
    Res.drawable.calves,
    Res.drawable.forearm,
    Res.drawable.traps,
    Res.drawable.lats,
    Res.drawable.lower_back
)

val muscleImageMap: Map<String, DrawableResource> = defaultMusclesNames.zip(defaultMuscleImages).toMap()
