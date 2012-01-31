package org.tal.rubychip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.circuit.CircuitLibrary;
import org.tal.rubychip.command.RubycCommand;

/**
 *
 * @author Tal Eisenberg
 */
public class RubycLibrary extends CircuitLibrary {
    public static final String jrubyAddress = "http://repo1.maven.org/maven2/org/jruby/jruby-complete/1.6.5.1/jruby-complete-1.6.5.1.jar";
    
    public static File folder;
    public static File jrubyJar;
    public static File jrubyHome;
    public static File jrubyGem;
    
    Plugin spout;
    
    private RedstoneChips rc;    
    protected RubycCommand command;
    
    private boolean jrubyLoaded = false;
    
    
    @Override
    public Class[] getCircuitClasses() {
        return new Class[] { rubyc.class };
    }

    @Override
    public void onRedstoneChipsEnable(RedstoneChips instance) {
        rc = instance;
    }

    @Override
    public void onLoad() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        folder = getDataFolder();
        jrubyJar = new File(getDataFolder(), "jruby.jar");
        jrubyHome = new File(getDataFolder(), "jruby");
        jrubyGem = new File(RubycLibrary.jrubyHome, "gems");
        if (!jrubyHome.exists()) jrubyHome.mkdirs();
        if (!jrubyGem.exists()) jrubyGem.mkdirs();
        
        if (!registerJRubyJar(jrubyJar)) {
            getServer().getScheduler().scheduleAsyncDelayedTask(this, new JRubyDownloader());
        }        
    }
    
    @Override
    public void onEnable() {
        if (jrubyJar==null || !jrubyJar.exists()) { 
            getLogger().log(Level.SEVERE, "Can't find jruby.jar in plugin folder.");
            disable(); 
        } else {
            registerCommand();
            
            RubycServerListener listener = new RubycServerListener(this);
            
            this.findSpout();
            this.getServer().getPluginManager().registerEvents(listener, this);
            
            getLogger().log(Level.INFO, getName() + " " + getVersion() + " enabled." + (spout!=null?" Spout support enabled.":""));
        }
    }

    @Override
    public void onDisable() {
    }       
    
    private void registerCommand() {
        command = new RubycCommand(this);
        getCommand("rubyc").setExecutor(command);
    }
    
    private boolean registerJRubyJar(File jrubyFile) {
        try {
            // sanity checks
            if (!jrubyFile.exists()) {
                getLogger().log(Level.SEVERE, getPrefix() + "JRuby runtime not found: " + jrubyFile.getPath());
                return false;
            }              
            
            URL jrubyURL = jrubyFile.toURI().toURL();
            
            URLClassLoader syscl = (URLClassLoader)ClassLoader.getSystemClassLoader();
            URL[] urls = syscl.getURLs();
            for (URL url : urls)
                if (url.sameFile(jrubyURL)) {
                    getLogger().log(Level.INFO, "Using present JRuby.jar from the classpath.");
                    jrubyLoaded = true;
                    return true;
                }

            // URLClassLoader.addUrl is protected
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{ URL.class });
            addURL.setAccessible(true);

            // add jruby.jar to Bukkit's class path
            addURL.invoke(syscl, new Object[]{ jrubyURL });
            
            getLogger().log(Level.INFO, "Loading JRuby runtime " + jrubyFile.getPath() + "...");
            jrubyLoaded = true;
            return true;            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, e.getMessage() + " while adding JRuby.jar to the classpath");
            e.printStackTrace();
            return false;
        }
    }
    
    private void disable() {
        getServer().getPluginManager().disablePlugin(this);
    }    
    
    private void enable() {
        getServer().getPluginManager().enablePlugin(this);
    }    

    private String getPrefix() {
        return "[" + getDescription().getName() + "] ";
    }
    
    public boolean isJRubyLoaded() { return jrubyLoaded; }

    public RedstoneChips getRC() {
        return rc;
    }

    public boolean isSpoutEnabled() {
        return spout!=null;
    }
    
    class JRubyDownloader implements Runnable {

        @Override
        public void run() {
            URL url = null;
            getLogger().log(Level.INFO, "Downloading jruby 1.6.5.1...");
            
            try {
                url = new URL(jrubyAddress);
            } catch (MalformedURLException ex) {
                getLogger().log(Level.SEVERE, ex.getMessage());
                return;
            }

            try {
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                File f = new File(getDataFolder(), "jruby.jar");
                FileOutputStream fos = new FileOutputStream(f);
                fos.getChannel().transferFrom(rbc, 0, 1 << 24);
                
                getLogger().log(Level.INFO, "Finished downloading jruby. Enabling rubyc.");
                if (isEnabled()) disable();
                
                if (registerJRubyJar(jrubyJar)) 
                    enable();
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "While downloading jruby.jar, " + ex);
            }
        }
        
    }    
    
    public Plugin findSpout() {
        if (rc!=null)
            return rc.getServer().getPluginManager().getPlugin("Spout");
        else return null;
    }
    
}
