/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip.script;

/**
 *
 * @author Tal Eisenberg
 */
public class DeleteCommand extends EditCommand {
    private int firstLine, lastLine;
    private String[] deletedLines;
    
    public DeleteCommand(int firstLine, int lastLine) {
        this.firstLine = firstLine;
        this.lastLine = lastLine;
    }

    @Override
    public void redo(Script script) {
        deletedLines = getLines(script.getLines(), firstLine, lastLine);
        script.setLines(DeleteCommand.delete(script.getLines(), firstLine, lastLine));
    }

    @Override
    public void undo(Script script) {
        script.setLines(InsertCommand.insert(script.getLines(), firstLine, deletedLines));
    }
    
    public static String[] delete(String[] script, int firstLine, int lastLine) {
        if (lastLine==Script.LAST_LINE) lastLine = script.length-1;
        
        if (firstLine<0 || firstLine>script.length-1 || lastLine>=script.length || lastLine<0 || lastLine<firstLine)
            throw new IllegalArgumentException("Line range out of bounds: " + firstLine + ".." + lastLine);
        int lineCount = lastLine - firstLine + 1;
        String[] newl = new String[script.length-lineCount];
        
        System.arraycopy(script, 0, newl, 0, firstLine);
        System.arraycopy(script, lastLine+1, newl, firstLine, newl.length-firstLine);
        
        return newl;
    }
}
