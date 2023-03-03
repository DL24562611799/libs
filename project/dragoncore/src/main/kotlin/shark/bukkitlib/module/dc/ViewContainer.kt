package shark.bukkitlib.module.dc

import org.bukkit.configuration.file.FileConfiguration

open class ViewContainer(override val indexName: String): ViewStream {

    val components = LinkedHashMap<String, ViewStream>()

    fun add(stream: ViewStream) {
        this.components[stream.indexName] = stream
    }

    fun remove(indexName: String) {
        this.components.remove(indexName)
    }

    fun clear() { this.components.clear() }

    operator fun get(indexName: String): ViewStream? {
        return components[indexName]
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ViewStream> getOrDefault(indexName: String, def: T): T {
        return this[indexName]?.let { it as T } ?: def
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ViewStream> getOrAdd(indexName: String, def: T): T {
        val stream = this[indexName]
        if (stream == null) {
            this.add(def)
            return def
        }
        return stream as T
    }

    override fun write(config: FileConfiguration) {
        for ((_, stream) in components) {
            stream.write(config)
        }
    }
}