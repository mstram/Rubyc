/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip;

import org.tal.rubychip.script.Script;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.jruby.embed.ScriptingContainer;

/**
 *
 * @author Tal Eisenberg
 */
public class RubycScript extends Script {
    private static final String defaultScript = "/default.rb";
    
    public final static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
        
    public RubycScript(String name, String script) {
        this.setScript(name, script);
    }   
    
    public void save() throws IOException {
        Writer out = new OutputStreamWriter(new FileOutputStream(getScriptFile(name)));
        try {
            out.write(getScript());
        } finally {
            out.close();
        }
    }
        
    public void save(String newName) throws IOException {
        setName(newName, true);
        save();
    }
    
    public void setName(String newName, boolean replace) {
        if (newName.equals(name)) return;
        
        if (replace) {
            String oldClass = classNameFor(name);
            String newClass = classNameFor(newName);

            for (int i=0; i<lines.length; i++) 
                    lines[i] = lines[i].replaceAll(oldClass, newClass);
        }
        
        this.name = newName;
    }
    
    RubyCircuit newInstance(ScriptingContainer runtime) {
        Object receiver = runtime.runScriptlet(getScript());
        if (receiver==null || !(receiver instanceof RubyCircuit)) return null;
        else {
            RubyCircuit c = (RubyCircuit)receiver;
            c.script = this;
            return (RubyCircuit)receiver;
        }
    }
    
    public File getFile() {
        return RubycScript.getScriptFile(name);
    }
    
    public static File getScriptFile(String name) {
        return new File(RubycLibrary.folder, name + ".rb");
    }
    
    public static boolean isValidScriptName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }       

    public static String classNameFor(String name) {
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }
    
    public static RubycScript fromFile(String name) throws IOException, IllegalArgumentException {
        if (!isValidScriptName(name)) throw new IllegalArgumentException("Invalid script name: " + name);
        
        File f = getScriptFile(name);
        
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
        
        return new RubycScript(name, scriptBuilder.toString());
    }
    
    public static RubycScript defaultScript(String name) throws IOException {
        URL u = RubycScript.class.getResource(defaultScript);
        InputStream stream = u.openStream();
        
        StringBuilder scriptBuilder = new StringBuilder();
        String nl = System.getProperty("line.separator");        
        Scanner scanner = new Scanner(stream);
        try {
            while (scanner.hasNextLine()) {
                scriptBuilder.append(scanner.nextLine());
                scriptBuilder.append(nl);
            }
        } finally {
            scanner.close();
        }
        
        String script = scriptBuilder.toString();
        script = script.replaceAll("CLASS123597132467298", classNameFor(name));
        return new RubycScript(name, script);
    }
}
