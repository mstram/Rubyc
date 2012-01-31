package org.tal.rubychip;

import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

/**
 *
 * @author Tal Eisenberg
 */
public class RubycServerListener implements Listener {
    RubycLibrary lib;
    
    public RubycServerListener(RubycLibrary lib) {
        this.lib = lib;
    }
        
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("Spout") && lib.spout == null) {
            lib.spout = event.getPlugin();
            lib.getLogger().log(Level.INFO, "Spout support enabled.");
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)    
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("Spout") && lib.spout != null) {
            lib.spout = null;
            if (lib.isEnabled())
                lib.getLogger().log(Level.INFO, "Spout support disabled.");
        }
    }
    
}
