/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip.script;

import java.util.Stack;

/**
 *
 * @author Tal Eisenberg
 */
public class Script {
    public static final int LAST_LINE = -1;
    
    protected String[] lines;
    protected String name;
    
    protected Stack<EditCommand> undoStack = new Stack<EditCommand>();
    protected Stack<EditCommand> redoStack = new Stack<EditCommand>();
    
    public void setScript(String name, String script) {
        lines = script.split(System.getProperty("line.separator"));
        this.name = name;        
    }
    
    public String getScript() {
        String script = "";
        
        for (String s : lines) {
            script += s + System.getProperty("line.separator");
        }
        
        return script.substring(0, script.length()-1);
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }
    
    public String[] getLines() {
        return lines;
    }

    public String getName() {
        return name;
    }
    
    public Stack<EditCommand> getUndoStack() {
        return undoStack;
    }

    public Stack<EditCommand> getRedoStack() {
        return redoStack;
    }

    public void runEditCommand(EditCommand c) {
        c.redo(this);
        undoStack.push(c);
    }
    
    public void undoEditCommand() {
        if (undoStack.isEmpty()) return;
        
        EditCommand command = undoStack.pop();
        command.undo(this);
        redoStack.push(command);
    }
    
    public void redoEditCommand() {
        if (redoStack.isEmpty()) return;
        
        EditCommand command = redoStack.pop();
        runEditCommand(command);
    }
    
    public boolean hasUndo() { return !undoStack.isEmpty(); }
    
    public boolean hasRedo() { return !redoStack.isEmpty(); }
    
}
