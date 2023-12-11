package org.sui.cli.manifest

import com.intellij.util.io.readText
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path

data class SuiConfigYaml(
    val configYamlPath: Path,
    val profiles: Set<String>
) {
    companion object {
        fun fromPath(configYamlPath: Path): SuiConfigYaml? {
            val yaml = Yaml().load<Map<String, Any>>(configYamlPath.readText())

            @Suppress("UNCHECKED_CAST")
            val profiles = (yaml["profiles"] as? Map<*, *>)?.keys as? Set<String> ?: return null
            return SuiConfigYaml(configYamlPath, profiles)
        }
    }
}
