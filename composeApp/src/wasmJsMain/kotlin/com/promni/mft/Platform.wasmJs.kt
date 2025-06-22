package com.promni.mft

import org.koin.core.module.Module
import org.koin.dsl.module

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual val platformModule: Module = module {
    single<Platform> { WasmPlatform() }
}
