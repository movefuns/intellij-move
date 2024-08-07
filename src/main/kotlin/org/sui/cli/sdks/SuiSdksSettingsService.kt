package org.sui.cli.sdks

import com.intellij.openapi.components.*
import com.intellij.util.SystemProperties
import java.nio.file.Paths

private const val SERVICE_NAME: String = "SuiSdksSettingsService"

@State(
    name = SERVICE_NAME,
    storages = [Storage(StoragePathMacros.NON_ROAMABLE_FILE)],
)
@Service(Service.Level.APP)
class SuiSdksSettingsService :
    SimplePersistentStateComponent<SuiSdksSettingsService.SuiSdksSettings>(SuiSdksSettings()) {

    val sdksDir: String? get() = this.state.sdksDir
    val suiSdkPaths: List<String> get() = this.state.suiSdkPaths
    val suiNetwork: String get() = this.state.network

    class SuiSdksSettings : BaseState() {
        var network: String = "mainnet"

        // null is empty string
        var sdksDir: String? by string(defaultValue = DEFAULT_SDKS_DIR)

        var suiSdkPaths: MutableList<String> by list()
        var aptosSdkPaths: MutableList<String> by list()

        fun copy(): SuiSdksSettings {
            val state = SuiSdksSettings()
            state.copyFrom(this)
            return state
        }
    }

    companion object {
        private val DEFAULT_SDKS_DIR =
            Paths.get(SystemProperties.getUserHome(), "sui-clis").toAbsolutePath().toString()
    }
}

fun sdksService(): SuiSdksSettingsService = service()