/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip.script;

/**
 *
 * @author Tal Eisenberg
 */
public class InsertCommand extends EditCommand {
    private int beforeLine;
    private String[] insert;
    
    public InsertCommand(int beforeLine, String[] insert) {
        this.beforeLine = beforeLine;
        this.insert = insert;
    }

    @Override
    public void redo(Script script) {
        script.setLines(InsertCommand.insert(script.getLines(), beforeLine, insert));
    }

    @Override
    public void undo(Script script) {
        script.setLines(DeleteCommand.delete(script.getLines(), beforeLine, beforeLine+insert.length-1));
    }

    public static String[] insert(String[] script, int beforeLine, String[] insert) {        
        if (beforeLine>=script.length) 
            throw new IllegalArgumentException("Line out of bounds: " + beforeLine);
        
        String[] newl = new String[script.length+insert.length];
        
        System.arraycopy(script, 0, newl, 0, beforeLine);
        System.arraycopy(insert, 0, newl, beforeLine, insert.length);
        System.arraycopy(script, beforeLine, newl, beforeLine+insert.length, newl.length-beforeLine-insert.length);
        
        return newl;       
    }
}
