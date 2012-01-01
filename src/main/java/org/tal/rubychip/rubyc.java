/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tal.rubychip;

import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.InvokeFailedException;
import org.jruby.exceptions.RaiseException;
import org.tal.redstonechips.circuit.Circuit;

/**
 *
 * @author Tal Eisenberg
 */
public class rubyc extends Circuit {    
    public final static String initMethod = "init";
    public final static String inputMethod = "input";
    
    private boolean stateless = false;
    private RubyCircuit program;
    
    @Override
    public void inputChange(int index, boolean state) {
        try {
            program.inputs[index] = state;
            program.input(index, state);
            //runtime.callMethod(receiver, inputMethod, new Object[] {index, state});
        } catch (InvokeFailedException e) {
            debug("on input: " + e.getMessage());
        } catch (RuntimeException e) {
            debug("on input: " + e.getMessage());
        } 
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        if (args.length==0) {
            error(sender, "Missing script name argument.");
            return false;
        }
                               
        try {
            program = RubycLibrary.scriptManager.getInstance(this, args[0]);
        } catch (IllegalArgumentException e) {
            error(sender, e.getMessage());
            return false;
        } catch (IOException ex) {
            error(sender, "on init: " + ex.getMessage());
            return false;
        } catch (RuntimeException e) {
            error(sender, "on init: " + e.getMessage());
            e.printStackTrace();
            return false;
        } 
        
        try {
            boolean res;

            program.setParent(this);
            copyInputBits(program);
            copyOutputBits(program);
            program.currentSender = sender;
            
            if (args.length>1) {
                program.args = new String[args.length-1];
                System.arraycopy(args, 1, program.args, 0, args.length-1);                
            }

            res = program.init();
            program.currentSender = null;
            
            if (res) info(sender, "Successfully activated ruby circuit: " + ChatColor.YELLOW + ScriptManager.getScriptFilename(args[0]));
            return res;
            
        } catch (InvokeFailedException e) {
            error(sender, "on init: " + e.getMessage());
            return false;
        } catch (EvalFailedException e) {
            error(sender, "on init: " + e.getMessage());
            return false;            
        } catch (RaiseException e) {
            error(sender, "on init: " + e.getMessage());
            return false;
        }
        
        
    }

    @Override
    protected boolean isStateless() {
        return stateless;
    }

    void prgSendBoolean(int idx, boolean state) {
        sendOutput(idx, state);
    }

    void prgError(CommandSender currentSender, String msg) {
        error(currentSender, msg);
    }

    void prgInfo(CommandSender currentSender, String msg) {
        info(currentSender, msg);
    }

    void prgDebug(String msg) {
        if (hasDebuggers()) debug(msg);
    }

    void prgSendInt(int startIdx, int length, int val) {
        sendInt(startIdx, length, val);
    }
    
    void copyInputBits(RubyCircuit program) {
        for (int i=0; i<program.inputs.length; i++)
            program.inputs[i] = inputBits.get(i);
    }

    void copyOutputBits(RubyCircuit program) {
        for (int i=0; i<program.outputs.length; i++)
            program.outputs[i] = outputBits.get(i);        
    }
}

