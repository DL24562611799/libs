package shark.bukkitlib.module.dc

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin
import java.util.*

object ViewUtil {

    fun setup(plugin: Plugin) {
        Bukkit.getPluginManager().registerEvents(ViewListener(plugin), plugin)
    }

    internal fun MutableMap<String, Any?>.copy(collections: MutableMap<String, Any?>, replace: (String.() -> String)? = null): MutableMap<String, Any?> = handlerEReplace(this, collections, replace)

    private fun handlerEReplace(originals: MutableMap<String, Any?>, collections: MutableMap<String, Any?>, replace: (String.() -> String)? = null): MutableMap<String, Any?> {
        if (replace != null) {
            var key: String
            var value: Any?
            originals.entries.forEach {
                key = it.key
                value = it.value
                when (value) {
                    is String -> {
                        value = replace.invoke(value as String)
                    }
                    is List<*> -> {
                        val newList = LinkedList<String>()
                        (value as List<*>).forEach { text ->
                            newList.add(replace.invoke(text.toString()))
                        }
                        value = newList
                    }
                    is ConfigurationSection -> {
                        value = handlerEReplace((value as ConfigurationSection).getValues(false), mutableMapOf(), replace)
                    }
                }
                collections[key] = value!!
            }
            return collections
        }else {
            collections.putAll(originals)
            return collections
        }
    }

}