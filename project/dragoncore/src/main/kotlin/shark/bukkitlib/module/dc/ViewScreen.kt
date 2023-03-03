package shark.bukkitlib.module.dc

import eos.moe.dragoncore.network.PacketSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import shark.bukkitlib.module.dc.event.ViewScreenCloseEvent
import shark.bukkitlib.module.dc.event.ViewScreenOpenEvent
import java.util.concurrent.ConcurrentHashMap

class ViewScreen(
    val player: Player,
    override val indexName: String,
    private val values: MutableMap<String, Any?>,
): ViewContainer(indexName) {

    var showing = false
        private set

    constructor(player: Player, indexName: String) : this(player, indexName, mutableMapOf())

    constructor(player: Player, indexName: String, section: ConfigurationSection) : this(player, indexName, section.getValues(false))

    fun open() {
        setOpenedScreen(player, this, false)
    }

    fun show() {
        setOpenedScreen(player, this, true)
    }

    fun syncUpdate() {
        if (showing) {
            PacketSender.sendYaml(player, "Gui/${indexName}.yml", buildYaml())
            PacketSender.sendOpenHud(player, indexName)
        }else if(this == guis[player.name]) {
            PacketSender.sendUpdateGui(player, buildYaml())
        }
    }

    fun close(showing: Boolean = this.showing) {
        if (showing) {
            showings[player.name]?.remove(indexName)
            if (ViewScreenCloseEvent(player, this).callEvent()) {
                PacketSender.sendYaml(player, "Gui/${indexName}.yml", YamlConfiguration())
                PacketSender.sendOpenHud(player, indexName)
            }
        }else {
            closeScreen(player, true)
        }
    }

    fun buildYaml(): YamlConfiguration {
        val yaml = YamlConfiguration()
        for ((k, v) in this.values) {
            yaml.set(k, v)
        }
        this.write(yaml)
        yaml.set("Functions.close", "方法.发包('ViewScreenCloseEvent', '${indexName}');${yaml.getString("Functions.close", "")}")
        return yaml
    }

    companion object {

        internal val guis = ConcurrentHashMap<String, ViewScreen>()
        internal val showings = ConcurrentHashMap<String, MutableMap<String, ViewScreen>>()

        fun getOpenedGui(player: Player): ViewScreen? {
            return guis[player.name]
        }

        fun getOpenedHud(player: Player, indexName: String): ViewScreen? {
            return showings[player.name]?.get(indexName)
        }

        fun getOpenedViewScreen(player: Player, indexName: String): ViewScreen? {
           return getOpenedGui(player)?.let {
                if (it.indexName == indexName) {
                    it
                }else null
            } ?: getOpenedHud(player, indexName)
        }

        internal fun setOpenedScreen(player: Player, screen: ViewScreen, showing: Boolean) {
            if (showing) {
                val map = showings.computeIfAbsent(player.name) { mutableMapOf() }
                // 如果已经是以界面的方式打开，则需要关闭这个界面
                if (guis.containsKey(player.name) && guis[player.name]!!.indexName == screen.indexName) {
                    screen.close(false)
                }
                screen.showing = true
                map[screen.indexName] = screen
                val buildYaml = screen.buildYaml()
                PacketSender.sendYaml(player, "Gui/${screen.indexName}.yml", buildYaml)
                PacketSender.sendOpenHud(player, screen.indexName)
                ViewScreenOpenEvent(player, screen).callEvent()
            }else {
                // 如果这个图形已经是以HUD的方式打开
                if (screen.showing) {
                    screen.close()
                }

                // 如果这个玩家已打开别的界面，则关闭那个界面
                closeScreen(player, false)

                screen.showing = false
                guis[player.name] = screen
                PacketSender.sendYaml(player, "Gui/${screen.indexName}.yml", screen.buildYaml())
                PacketSender.sendOpenGui(player, screen.indexName)
                ViewScreenOpenEvent(player, screen).callEvent()
            }
        }

        internal fun closeScreen(player: Player, force: Boolean): ViewScreen? {
            val remove = guis.remove(player.name)
            if (remove != null) {
                ViewScreenCloseEvent(player, remove).callEvent()
            }
            if (force) {
                player.closeInventory()
            }
            return remove
        }

        internal fun closeScreen(player: Player, guiName: String) {
            val remove = guis[player.name]
            if (remove?.indexName == guiName) {
                guis.remove(player.name)
                ViewScreenCloseEvent(player, remove).callEvent()
            }
        }
    }
}