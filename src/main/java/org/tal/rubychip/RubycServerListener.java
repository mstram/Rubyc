/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip;

import java.util.logging.Level;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

/**
 *
 * @author Tal Eisenberg
 */
public class RubycServerListener extends ServerListener {
    RubycLibrary lib;
    
    public RubycServerListener(RubycLibrary lib) {
        this.lib = lib;
    }
        
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("Spout") && lib.spout == null) {
            lib.spout = event.getPlugin();
            lib.log(Level.INFO, "Spout support enabled.");
        }
    }

    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("Spout") && lib.spout != null) {
            lib.spout = null;
            if (lib.isEnabled())
                lib.log(Level.INFO, "Spout support disabled.");
        }
    }
    
}
