package org.tal.rubychip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jruby.RubyInstanceConfig.CompileMode;
import org.jruby.embed.LocalContextScope;
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
        ScriptingContainer runtime = new ScriptingContainer(LocalContextScope.SINGLETHREAD);

        runtime.setCompileMode(CompileMode.JIT);
        String[] paths = new String[] {
            "file:" + RubycLibrary.jrubyJar.getAbsoluteFile() + "!/META_INF/jruby.home/lib/ruby/site_ruby/1.9",
            "file:" + RubycLibrary.jrubyJar.getAbsoluteFile() + "!/META_INF/jruby.home/lib/ruby/site_ruby/shared",
            "file:" + RubycLibrary.jrubyJar.getAbsoluteFile() + "!/META_INF/jruby.home/lib/ruby/1.9",
            RubycLibrary.folder.getAbsolutePath()                
        };
        runtime.setLoadPaths(Arrays.asList(paths));
        runtime.setClassLoader(rubyc.class.getClassLoader());
        
        return runtime;
    }
    
    public static List<String> getAvailableScripts(ScriptingContainer runtime) {
        List<String> ret = new ArrayList<String>();
        
        File[] files = RubycLibrary.folder.listFiles();
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
        
        return ret;
    }
    
}
