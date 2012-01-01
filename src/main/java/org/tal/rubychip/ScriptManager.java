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
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author Tal Eisenberg
 */
public class ScriptManager {
    private static final String defaultScript = "/default.rb";
    
    public final static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
    
    RubyCircuit getInstance(rubyc rbc, String name) throws IOException, IllegalArgumentException {        
        String script;
        try {
            script = load(name);                        
        } catch (FileNotFoundException f) {
            if (validateName(name)) {
                script = defaultScript(name);
                save(name, script);
            } else throw new IllegalArgumentException("Invalid script name: " + name);
        } 
                            
        RubyCircuit c = newInstance(rbc, script);
        if (c!=null) c.script = script;
        return c;
    }

    private RubyCircuit newInstance(rubyc c, String script) {
        Object receiver = c.getRuntime().runScriptlet(script);
        if (receiver==null || !(receiver instanceof RubyCircuit)) return null;
        else return (RubyCircuit)receiver;        
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
        script = script.replaceAll("CLASS123597132467298", getClassName(name));
        return script;
    }
    
    private String load(String name) throws IOException {
        File f = getScriptFilename(name);

        return readFile(f);
        
    }
    
    public static File getScriptFilename(String name) {
        return new File(RubycLibrary.folder, name + ".rb");
    }

    public static String getClassName(String name) {
        return name.substring(0,1).toUpperCase() + name.substring(1);
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

    private boolean validateName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }
}
