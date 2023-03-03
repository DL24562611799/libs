package shark.bukkitlib.module.dc

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import shark.bukkitlib.module.dc.ViewUtil.copy

class ViewComponent(override val indexName: String, values: MutableMap<String, Any?>): ViewProperty(values), ViewStream {

    override fun write(config: FileConfiguration) {
        config.set(indexName, values)
    }

    class Builder(val indexName: String, val section: ConfigurationSection) {

        private val values = section.getValues(false)

        fun add(container: ViewContainer, index: Int = -1, replace: (String.() -> String)? = null, callback: ViewComponent.() -> Unit = {}) {
            val component = ViewComponent(
                if (index != -1) "${index}-${indexName}" else indexName,
                this.values.copy(mutableMapOf()) {
                    replace?.invoke(replace("<i>", index.toString())) ?: replace("<i>", index.toString())
                }
            )
            callback.invoke(component)
            container.add(component)
        }

    }
}