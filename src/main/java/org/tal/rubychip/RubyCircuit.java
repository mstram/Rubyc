package org.tal.rubychip;

import org.bukkit.command.CommandSender;
import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.circuit.InterfaceBlock;
import org.tal.redstonechips.util.BitSet7;

/**
 *
 * @author Tal Eisenberg
 */
public abstract class RubyCircuit {
    protected rubyc circuit;
    protected RubycScript script;
    protected String scriptName;
    protected CommandSender currentSender;
    protected String[] args = new String[0];
    public boolean[] inputs;
    public boolean[] outputs;
    public RedstoneChips rc;
    
    public void setup(rubyc circuit, String scriptName) { 
        inputs = new boolean[circuit.inputs.length];
        outputs = new boolean[circuit.outputs.length];
        rc = circuit.getPlugin();
        this.circuit = circuit; 
        this.scriptName = scriptName;
    }
            
        
    public abstract boolean init();
    
    public abstract void input(int idx, boolean state);
    
    public void shutdown() {}
    
    protected void send(int idx, boolean state) {
        circuit.prgSendBoolean(idx, state);
        
        outputs[idx] = state;
    }
    
    protected void send(int startIdx, int length, int val) {
        circuit.prgSendInt(startIdx, length, val);
    }
    
    protected void send(int startIdx, int length, BitSet7 bits) {        
        circuit.prgSendBitSet(startIdx, length, bits);
    }

    protected void send(BitSet7 bits) {
        circuit.prgSendBitSet(bits);
    }
    
    protected void info(String msg) {
        if (currentSender!=null) circuit.prgInfo(currentSender, msg);
    }
    
    protected void debug(String msg) {
        circuit.prgDebug(msg);
    }
    
    protected void error(String msg) {
        if (currentSender!=null) circuit.prgError(currentSender, msg);
    }
    
    public void setStateless(boolean stateless) {
        circuit.prgStateless(stateless);
    }
    
    protected int bits_to_i(int startIdx, int length, boolean[] bits) {
        int val = 0;
        for (int i=0; i<length; i++) {
            if (bits[i+startIdx]) val += Math.pow(2,i);
        }

        return val;
    }
    
    public RubycScript getScript() { return script; }
    
    public rubyc getCircuit() { return circuit; }
    
    public String[] getArgs() { return args; }
    
    public InterfaceBlock[] getInterfaces() { return circuit.interfaceBlocks; }
    
    public boolean has_debuggers() { return circuit.hasDebuggers(); }

    public String getScriptName() {
        return scriptName;
    }
    
    public CommandSender getSender() {
        return currentSender;
    }
}
