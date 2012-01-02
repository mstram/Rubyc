/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jruby.RubyInstanceConfig.CompileMode;
import org.jruby.embed.ScriptingContainer;

/**
 *
 * @author Tal Eisenberg
 */
public class ScriptManager {
   
    
    public static RubycScript getScript(String name) throws IOException, IllegalArgumentException {        
        RubycScript script;
        try {
            script = RubycScript.fromFile(name);
        } catch (FileNotFoundException f) {
            if (RubycScript.isValidScriptName(name)) {
                script = RubycScript.defaultScript(name);
                script.save();
            } else throw new IllegalArgumentException("Invalid script name: " + name);
        } 
    
        return script;
    }
                         
    public static ScriptingContainer createRuntime() {
        ScriptingContainer runtime = new ScriptingContainer();
        runtime.setCompileMode(CompileMode.JIT);
        runtime.setLoadPaths(Arrays.asList(new String[] {RubycLibrary.folder.getAbsolutePath()}));
        runtime.setClassLoader(rubyc.class.getClassLoader());
        
        return runtime;
    }
    
    public static List<String> getAvailableScripts(ScriptingContainer runtime) {
        List<String> paths = runtime.getLoadPaths();        
        List<String> ret = new ArrayList<String>();
        
        for (String p : paths) {
            File d = new File(p);
            File[] files = d.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(".rb")) {
                    String name = f.getName().substring(0, f.getName().length()-3);
                    try {
                        RubycScript s = getScript(name);
                        if (s.newInstance(runtime)!=null);
                            ret.add(name);
                    } catch (IOException ex) {
                    } catch (RuntimeException ex) {}
                }
            }
                
        }
        
        return ret;
    }
}
