package org.tal.rubychip;

import org.bukkit.command.CommandSender;
import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.circuit.InterfaceBlock;

/**
 *
 * @author Tal Eisenberg
 */
public abstract class RubyCircuit {
    protected rubyc parent;
    protected String script;
    protected CommandSender currentSender;
    public String[] args = new String[0];
    public boolean[] inputs;
    public boolean[] outputs;
    public RedstoneChips rc;
    
    public void setParent(rubyc parent) { 
        inputs = new boolean[parent.inputs.length];
        outputs = new boolean[parent.outputs.length];
        rc = parent.getPlugin();
        this.parent = parent; 
    }
            
        
    public abstract boolean init();
    
    public abstract void input(int idx, boolean state);
    
    protected void send(int idx, boolean state) {
        parent.prgSendBoolean(idx, state);
        
        outputs[idx] = state;
    }
    
    protected void send(int startIdx, int length, int val) {
        parent.prgSendInt(startIdx, length, val);
    }
    
    protected void info(String msg) {
        if (currentSender!=null) parent.prgInfo(currentSender, msg);
    }
    
    protected void debug(String msg) {
        parent.prgDebug(msg);
    }
    
    protected void error(String msg) {
        if (currentSender!=null) parent.prgError(currentSender, msg);
    }
    
    public void setStateless(boolean stateless) {
        parent.prgStateless(stateless);
    }
    
    protected int bits_to_i(int startIdx, int length, boolean[] bits) {
        int val = 0;
        for (int i=0; i<length; i++) {
            if (bits[i+startIdx]) val += Math.pow(2,i);
        }

        return val;
    }
    
    public String getScript() { return script; }
    
    public rubyc getRubyc() { return parent; }
    
    public String[] getArgs() { return args; }
    
    public InterfaceBlock[] getInterfaces() { return parent.interfaceBlocks; }
    
    public boolean has_debuggers() { return parent.hasDebuggers(); }
}
