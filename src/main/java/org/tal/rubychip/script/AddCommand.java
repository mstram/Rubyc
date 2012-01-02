/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip.script;

/**
 *
 * @author Tal Eisenberg
 */
public class AddCommand extends EditCommand {
    String[] addition;

    public AddCommand(String[] addition) {
        this.addition = addition;
    }
    
    @Override
    public void redo(Script script) {
        script.setLines(add(script.getLines(), addition));
    }

    @Override
    public void undo(Script script) {
        String[] s = script.getLines();
        script.setLines(DeleteCommand.delete(s, s.length-addition.length, s.length));
    }

    public static String[] add(String[] script, String[] addition) {
        String[] newl = new String[script.length+addition.length];
        System.arraycopy(script, 0, newl, 0, script.length);
        System.arraycopy(addition, 0, newl, script.length, addition.length);
        
        return newl;
    }
}
