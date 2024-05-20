package org.sui.cli.manifest

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import java.nio.file.Path
import kotlin.io.path.readText

data class SuiConfigYaml(
    val configYamlPath: Path,
    val profiles: Set<String>
) {
    companion object {
        fun fromPath(configYamlPath: Path): SuiConfigYaml? {
            val yaml =
                try {
                    Yaml().load<Map<String, Any>>(configYamlPath.readText())
                } catch (e: YAMLException) {
                    // TODO: error notification?
                    return null
                }
            @Suppress("UNCHECKED_CAST")
            val profiles = (yaml["profiles"] as? Map<*, *>)?.keys as? Set<String> ?: return null
            return SuiConfigYaml(configYamlPath, profiles)
        }
    }
}
