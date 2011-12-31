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
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.exceptions.RaiseException;
import org.tal.redstonechips.circuit.Circuit;

/**
 *
 * @author Tal Eisenberg
 */
public class rubyc extends Circuit {
    public static ScriptManager scriptManager = new ScriptManager();
    
    public final static String initMethod = "init";
    public final static String inputMethod = "input";
    
    private boolean stateless = false;
    private ScriptingContainer runtime;
    private RubyCircuit program;
    
    @Override
    public void inputChange(int index, boolean state) {
        if (runtime!=null) {
            try {
                program.inputs[index] = state;
                program.input(index, state);
                //runtime.callMethod(receiver, inputMethod, new Object[] {index, state});
            } catch (InvokeFailedException e) {
                debug("on input: " + e.getMessage());
            } catch (EvalFailedException e) {
                debug("on input: " + e.getMessage());
            } catch (RaiseException e) {
                debug("on input: " + e.getMessage());
            }
        }
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        if (args.length==0) {
            error(sender, "Missing script name argument.");
            return false;
        }
                        
        runtime = new ScriptingContainer(LocalContextScope.SINGLETON);
        try {
            program = scriptManager.getInstance(this, runtime, args[0]);
        } catch (IOException ex) {
            error(sender, "on init: " + ex.getMessage());
        } catch (RaiseException e) {
            error(sender, "on init: " + e.getMessage());
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

    void prgOut(int idx, boolean state) {
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

    void copyInputBits(RubyCircuit program) {
        for (int i=0; i<program.inputs.length; i++)
            program.inputs[i] = inputBits.get(i);
    }

    void copyOutputBits(RubyCircuit program) {
        for (int i=0; i<program.outputs.length; i++)
            program.outputs[i] = outputBits.get(i);        
    }

    void prgOutint(int startIdx, int length, int val) {
        sendInt(startIdx, length, val);
    }
}

