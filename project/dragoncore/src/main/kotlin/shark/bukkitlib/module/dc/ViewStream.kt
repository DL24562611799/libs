package shark.bukkitlib.module.dc

import org.bukkit.configuration.file.FileConfiguration

interface ViewStream {

    val indexName: String

    fun write(config: FileConfiguration)

}