package org.tal.rubychip;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.circuit.CircuitLibrary;

/**
 *
 * @author Tal Eisenberg
 */
public class RubycLibrary extends CircuitLibrary {
    private static final Logger logger = Logger.getLogger("Minecraft");
    public static File folder;
       
    protected static File jrubyJar;
    
    @Override
    public Class[] getCircuitClasses() {
        return new Class[] { rubyc.class };
    }

    @Override
    public void onRedstoneChipsEnable(RedstoneChips instance) {
    }

    @Override
    public void onLoad() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        folder = getDataFolder();
        jrubyJar = new File(getDataFolder() + File.separator + "jruby.jar");
        
        if (!registerJRubyJar(jrubyJar)) {
            jrubyJar = null;
            return;
        }        
    }
    
    @Override
    public void onEnable() {
        if (jrubyJar==null) disable();
    }

    @Override
    public void onDisable() {
        // Called when the circuit library plugin is disabled.

    }
    
    private void disable() {
        getServer().getPluginManager().disablePlugin(this);
    }    
    
    private boolean registerJRubyJar(File jrubyFile) {
        try {
            // sanity checks
            if (!jrubyFile.exists()) {
                logger.log(Level.SEVERE, "JRuby runtime not found: " + jrubyFile.getPath());
                return false;
            }              
            
            URL jrubyURL = jrubyFile.toURI().toURL();
            
            URLClassLoader syscl = (URLClassLoader)ClassLoader.getSystemClassLoader();
            URL[] urls = syscl.getURLs();
            for (URL url : urls)
                if (url.sameFile(jrubyURL)) {
                    log(Level.INFO, "Using present JRuby.jar from the classpath.");
                    return true;
                }

            // URLClassLoader.addUrl is protected
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{ URL.class });
            addURL.setAccessible(true);

            // add jruby.jar to Bukkit's class path
            addURL.invoke(syscl, new Object[]{ jrubyURL });
            
            log(Level.INFO, "Using JRuby runtime " + jrubyFile.getPath());
            return true;            
        } catch (Exception e) {
            log(Level.SEVERE, e.getMessage() + " while adding JRuby.jar to the classpath");
            e.printStackTrace();
            return false;
        }
    }
    
    public void log(Level l, String m) {
        logger.log(l, "[" + this.getName() + "] " + m);
    }
}
