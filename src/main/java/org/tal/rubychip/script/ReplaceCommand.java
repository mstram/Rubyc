package org.tal.rubychip.script;

/**
 *
 * @author Tal Eisenberg
 */
public class ReplaceCommand extends EditCommand {
    String[] replacement, replaced;
    int firstLine, lastLine;
    
    public ReplaceCommand(String[] replacement, int firstLine, int lastLine) {
        this.firstLine = firstLine;
        this.lastLine = lastLine;
        this.replacement = replacement;
    }

    @Override
    public void redo(Script script) {
        replaced = getLines(script.getLines(), firstLine, lastLine);
        script.setLines(replace(script.getLines(), replacement, firstLine, lastLine));
    }

    @Override
    public void undo(Script script) {
        script.setLines(replace(script.getLines(), replaced, firstLine, firstLine+replaced.length-1));
    }

    public static String[] replace(String[] script, String[] replacement, int firstLine, int lastLine) {
        if (lastLine==Script.LAST_LINE) lastLine = script.length-1;        
        if (firstLine<0 || firstLine>script.length-1 || lastLine>=script.length || lastLine<0 || lastLine<firstLine)
            throw new IllegalArgumentException("Line range out of bounds: " + firstLine + ".." + lastLine);

        int lineCount = lastLine-firstLine + 1;
        
        String[] newl = new String[script.length-lineCount + replacement.length];
        
        System.arraycopy(script, 0, newl, 0, firstLine);
        System.arraycopy(replacement, 0, newl, firstLine, replacement.length);
        System.arraycopy(script, lastLine+1, newl, firstLine+replacement.length, script.length-lastLine-1);

        return newl;
    }
}
