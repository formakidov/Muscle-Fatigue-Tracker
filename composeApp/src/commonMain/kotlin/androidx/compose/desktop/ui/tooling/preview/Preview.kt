package androidx.compose.desktop.ui.tooling.preview

// workaround to be able to see previews in commonMain
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
expect annotation class Preview()
