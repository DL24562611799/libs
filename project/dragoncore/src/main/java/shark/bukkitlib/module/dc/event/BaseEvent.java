package shark.bukkitlib.module.dc.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class BaseEvent extends Event {

    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        if (this instanceof Cancellable) {
            return !((Cancellable) this).isCancelled();
        } else {
            return true;
        }
    }

}
