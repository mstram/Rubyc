/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import org.jruby.RubyInstanceConfig.CompileMode;
import org.jruby.embed.ScriptingContainer;

/**
 *
 * @author Tal Eisenberg
 */
public class ScriptManager {
    private static final String defaultScript = "/default.rb";
    
    RubyCircuit getInstance(rubyc rbc, ScriptingContainer runtime, String name) throws IOException {        
        String script;
        try {
            script = load(name);                        
        } catch (FileNotFoundException f) {
            script = defaultScript(name);
            save(name, script);
        } 
                            
        RubyCircuit c = newInstance(rbc, runtime, script);
        if (c!=null) c.script = script;
        return c;
    }

    private RubyCircuit newInstance(rubyc c, ScriptingContainer runtime, String script) {
        runtime.setCompileMode(CompileMode.JIT);
        runtime.setLoadPaths(Arrays.asList(new String[] { c.getPlugin().getDataFolder().getAbsolutePath() }));
        runtime.setClassLoader(rubyc.class.getClassLoader());
        
        Object receiver = runtime.runScriptlet(script);
        if (receiver instanceof RubyCircuit) return (RubyCircuit)receiver;
        else return null;
        
    }
    
    public String defaultScript(String name) throws IOException {
        URL u = getClass().getResource(defaultScript);
        InputStream stream;
        stream = u.openStream();
        
        StringBuilder scriptBuilder = new StringBuilder();
        String NL = System.getProperty("line.separator");        
        Scanner scanner = new Scanner(stream);
        try {
            while (scanner.hasNextLine()) {
                scriptBuilder.append(scanner.nextLine());
                scriptBuilder.append(NL);
            }
        } finally {
            scanner.close();
        }  
        String script = scriptBuilder.toString();
        script = script.replaceAll("CLASS123597132467298", name.substring(0,1).toUpperCase() + name.substring(1));
        return script;
    }
    
    private String load(String name) throws IOException {
        File f = getScriptFilename(name);

        return readFile(f);
        
    }
    
    public static File getScriptFilename(String name) {
        return new File(RubycLibrary.folder, name + ".rb");
    }

    private String readFile(File f) throws IOException {
        if (!f.exists()) throw new FileNotFoundException();

        StringBuilder scriptBuilder = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(f));
        try {
            while (scanner.hasNextLine()) {
                scriptBuilder.append(scanner.nextLine());
                scriptBuilder.append(NL);
            }
        } finally {
            scanner.close();
        }  
        
        return scriptBuilder.toString();
    }

    public void save(String name, String script) throws IOException {
        Writer out = new OutputStreamWriter(new FileOutputStream(getScriptFilename(name)));
        try {
            out.write(script);
        } finally {
            out.close();
        }
    }
}