/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip.script;

/**
 *
 * @author Tal Eisenberg
 */
public abstract class EditCommand {
    public abstract void redo(Script script);
    
    public abstract void undo(Script script);
    
    public static String[] getLines(String[] script, int firstLine, int lastLine) {
        if (lastLine==Script.LAST_LINE) 
            lastLine = script.length-1;        
        else if (firstLine<0 || firstLine>script.length-1 || lastLine>=script.length || lastLine<0 || lastLine<firstLine)
            throw new IllegalArgumentException("Line range out of bounds: " + firstLine + ".." + lastLine);
        
        int lineCount = lastLine - firstLine + 1;
        String[] newl = new String[lineCount];
        
        System.arraycopy(script, firstLine, newl, 0, lineCount);
        
        return newl;        
    }
    
    public String getName() {
        String className = getClass().getSimpleName();
        return className.substring(0, className.length()-7).toLowerCase();
    }
}
