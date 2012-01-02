/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip;

import org.jruby.embed.ScriptingContainer;

/**
 *
 * @author Tal Eisenberg
 */
public class RubycScript {
    public static final int LAST_LINE = -1;
    
    private String[] lines;
    
    public RubycScript(String script) {
        lines = script.split(System.getProperty("line.separator"));
    }   
    
    public String[] getLines() {
        return lines;
    }

    public void deleteLines(int firstLine, int lastLine) throws IllegalArgumentException {
        if (lastLine==RubycScript.LAST_LINE) lastLine = lines.length-1;
        
        if (firstLine<0 || firstLine>lines.length-1 || lastLine>=lines.length || lastLine<0 || lastLine<firstLine)
            throw new IllegalArgumentException("Line range out of bounds: " + firstLine + ".." + lastLine);
        int lineCount = lastLine - firstLine + 1;
        String[] newl = new String[lines.length-lineCount];
        
        System.arraycopy(this.lines, 0, newl, 0, firstLine);
        System.arraycopy(this.lines, lastLine+1, newl, firstLine, newl.length-firstLine);
                
        lines = newl;
    }

    public void insertLines(int beforeLine, String[] insert) throws IllegalArgumentException {
        if (beforeLine>=lines.length) 
            throw new IllegalArgumentException("Line out of bounds: " + beforeLine);
        
        String[] newl = new String[lines.length+insert.length];
        
        System.arraycopy(lines, 0, newl, 0, beforeLine);
        System.arraycopy(insert, 0, newl, beforeLine, insert.length);
        System.arraycopy(lines, beforeLine, newl, beforeLine+insert.length, newl.length-beforeLine-insert.length);
        
        lines = newl;
    }

    public void addLines(String[] addition) {
        String[] newl = new String[lines.length+addition.length];
        System.arraycopy(lines, 0, newl, 0, lines.length);
        System.arraycopy(addition, 0, newl, lines.length, addition.length);
    }
    
    public void replaceLines(String[] replacement, int firstLine, int lastLine) throws IllegalArgumentException {
        if (lastLine==RubycScript.LAST_LINE) lastLine = lines.length-1;
        
        if (firstLine<0 || firstLine>lines.length-1 || lastLine>=lines.length || lastLine<0 || lastLine<firstLine)
            throw new IllegalArgumentException("Line range out of bounds: " + firstLine + ".." + lastLine);
        int lineCount = lastLine-firstLine + 1;
        String[] newl = new String[lines.length-lineCount + replacement.length];
        
        System.arraycopy(this.lines, 0, newl, 0, firstLine);
        System.arraycopy(replacement, 0, newl, firstLine, replacement.length);
        System.arraycopy(this.lines, lastLine+1, newl, firstLine+replacement.length, lines.length-lastLine-1);
                
        lines = newl;
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

    public String getScript() {
        String script = "";
        
        for (String s : lines) {
            script += s + System.getProperty("line.separator");
        }
        
        return script.substring(0, script.length()-1);
    }
    
}
