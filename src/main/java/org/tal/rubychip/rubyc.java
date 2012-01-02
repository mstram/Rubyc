/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tal.rubychip;

import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jruby.embed.InvokeFailedException;
import org.jruby.embed.ScriptingContainer;
import org.jruby.exceptions.RaiseException;
import org.tal.redstonechips.circuit.Circuit;

/**
 *
 * @author Tal Eisenberg
 */
public class rubyc extends Circuit {    
    private boolean stateless = false;
    private RubyCircuit program;
    private ScriptingContainer runtime;
    
    @Override
    public void inputChange(int index, boolean state) {
        program.inputs[index] = state;
        
        try {
            program.input(index, state);
        } catch (InvokeFailedException e) {
            debug("on input: " + e.getMessage());
        } catch (RaiseException e) {
            rubyException(null, "input", e);
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

        RubycScript script;
        
        try {
            script = ScriptManager.getScript(args[0]);
        } catch (IllegalArgumentException e) {
            error(sender, e.getMessage());
            return false;
        } catch (IOException ex) {
            error(sender, redstoneChips.getPrefs().getInfoColor() + "on load: " + redstoneChips.getPrefs().getErrorColor() + ex.getMessage());
            return false;
        } 
        
        runtime = ScriptManager.createRuntime();
        
        if (initScript(sender, script)) {
            info(sender, "Successfully activated ruby circuit: " + ChatColor.YELLOW + script.getFile());
            return true;
        } else return false;
    }

    private boolean initScript(CommandSender sender, RubycScript script) {
        try {
            boolean res;
            
            RubyCircuit p = script.newInstance(runtime);
            if (p==null) {
                error(sender, "Class instance not found in " + args[0] + ".rb");
                return false;
            }                        

            p.setup(this, args[0]);
            copyInputBits(p);
            copyOutputBits(p);
            p.currentSender = sender;
            
            if (args.length>1) {
                p.args = new String[args.length-1];
                System.arraycopy(args, 1, p.args, 0, args.length-1);                
            }

            res = p.init();
            p.currentSender = null;            
            this.program = p;
            
            return res;            
        } catch (RaiseException e) {
            rubyException(sender, "init", e);
            return false;
        } catch (RuntimeException e) {
            error(sender, "on init: " + e);
            return false;
        } 
        
    }
    
    public void reloadScript(CommandSender sender) {
        this.resetOutputs();
        runtime.clear();
        
        if (initScript(sender, program.getScript()))
            info(sender, "Reloaded script: " + ChatColor.YELLOW + program.getScript().getFile());
        else info(sender, "Can't reload script.");
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
    
    void prgStateless(boolean stateless) {
        this.stateless = stateless;
    }
    
    void copyInputBits(RubyCircuit program) {
        for (int i=0; i<program.inputs.length; i++)
            program.inputs[i] = inputBits.get(i);
    }

    void copyOutputBits(RubyCircuit program) {
        for (int i=0; i<program.outputs.length; i++)
            program.outputs[i] = outputBits.get(i);        
    }

    private void rubyException(CommandSender sender, String event, RaiseException e) {
        if (sender==null && hasDebuggers()) {
            debug("on " + event + ": " + redstoneChips.getPrefs().getErrorColor() + e.getMessage());
            debug(ChatColor.AQUA + e.getException().backtrace().asString().asJavaString());
        } else {
            info(sender, "on " + event + ": " + redstoneChips.getPrefs().getErrorColor() + e.getMessage());
            info(sender, ChatColor.AQUA + e.getException().backtrace().asString().asJavaString());
        }
    }

    public ScriptingContainer getRuntime() {
        return runtime;
    }

    public RubyCircuit getRubyCircuit() {
        return program;
    }

}

