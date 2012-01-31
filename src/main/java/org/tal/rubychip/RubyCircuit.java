package org.tal.rubychip;

import org.bukkit.command.CommandSender;
import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.bitset.BitSet7;
import org.tal.redstonechips.circuit.io.InterfaceBlock;

/**
 * Represents the internal ruby circuit inside a rubyc chip. All scripts must extend this class.
 * 
 * @author Tal Eisenberg
 */
public abstract class RubyCircuit {
    /** The rubyc chip this circuit is running in. */
    protected rubyc circuit;
    
    /** A reference to the ruby script that is using this RubyCircuit class. */
    protected RubycScript script;
    
    /** The script name. This is the script filename without the .rb extension */
    protected String scriptName;
    
    /** The currently set CommandSender. In init() method this is set to the chip activator if there is one. */
    protected CommandSender currentSender;
    
    /** The rubyc chip sign arguments. */
    protected String[] args = new String[0];
    
    /** An array containing the current state of each of the rubyc's input pins. */
    public boolean[] inputs;
    
    /** An array containing the current state of each of the rubyc's output pins. */
    public boolean[] outputs;
    
    /** A reference to the RedstoneChips plugin */
    public RedstoneChips rc;
    
    /** 
     * Initializes script fields. This is called when the rubyc is activated.
     * 
     * @param circuit The rubyc using this RubyCircuit.
     * @param scriptName The rubyc script name without an .rb extension.
     */
    public void setup(rubyc circuit, String scriptName) { 
        inputs = new boolean[circuit.inputs.length];
        outputs = new boolean[circuit.outputs.length];
        rc = circuit.getPlugin();
        this.circuit = circuit; 
        this.scriptName = scriptName;
    }
            
    /**
     * Called when the chip is activated.
     * 
     * @return true if initialization was successful.
     */
    public abstract boolean init();
    
    /**
     * Called when an input pin state changes.
     * 
     * @param idx The changed input pin index.
     * @param state The new input pin state.
     */
    public abstract void input(int idx, boolean state);
    
    /**
     * Called when the chip is unloaded. This happens when its broken, its world is unloaded or when the server 
     * is shutting-down.
     */
    public void shutdown() {}
    
    /**
     * Called when RedstoneChips is requested to save its data.
     */
    public void save() {}
    
    /**
     * Called when the circuit is broken.
     */
    public void destroyed() {}
    
    /**
     * Changes the state of an output pin.
     * 
     * @param idx The index of the output pin.
     * @param state The new state of the output pin.
     */
    protected void send(int idx, boolean state) {
        circuit.prgSendBoolean(idx, state);
        
        outputs[idx] = state;
    }
    
    /**
     * Sends an integer number to the chip output pins.
     * @param startIdx The output pin index to start from.
     * @param length The number of output pins to use.
     * @param val The integer value that will be sent.
     */
    protected void send(int startIdx, int length, int val) {
        circuit.prgSendInt(startIdx, length, val);
    }
    
    /**
     * Sends a BitSet to the chip output pins.
     * @param startIdx The output pin index to start from.
     * @param length The number of output pins to use.
     * @param bits The BitSet that will be sent.
     */
    protected void send(int startIdx, int length, BitSet7 bits) {        
        circuit.prgSendBitSet(startIdx, length, bits);
    }

    /**
     * Sends a BitSet to the chip output pins starting from the 1st pin.
     * @param bits The BitSet that will be sent.
     */
    protected void send(BitSet7 bits) {
        circuit.prgSendBitSet(bits);
    }
    
    /**
     * Sends an info message to the current command sender. This should only be called from the init() method.
     * @param msg The message to send.
     */
    protected void info(String msg) {
        if (currentSender!=null) circuit.prgInfo(currentSender, msg);
    }
    
    /**
     * Sends a debug message to any listening debuggers.
     * @param msg The message to send.
     */
    protected void debug(String msg) {
        circuit.prgDebug(msg);
    }
    
    /**
     * Sends an error message to the current command sender. This should only be called from the init() method.
     * @param msg 
     */
    protected void error(String msg) {
        if (currentSender!=null) circuit.prgError(currentSender, msg);
    }
    
    /**
     * Sets whether this circuit is stateless or not. A stateless circuit is expected to always have the same output 
     * for a set of input pin states. 
     * 
     * @param stateless 
     */
    public void setStateless(boolean stateless) {
        circuit.prgStateless(stateless);
    }
    
    /**
     * Convenience method for converting a boolean bit array into an integer number.
     * 
     * @param startIdx The array index to start converting from.
     * @param length Number of bits to read from the array.
     * @param bits Boolean bit array.
     * @return The converted integer value.
     */
    protected int bits_to_i(int startIdx, int length, boolean[] bits) {
        int val = 0;
        for (int i=0; i<length; i++) {
            if (bits[i+startIdx]) val += Math.pow(2,i);
        }

        return val;
    }
    
    /**
     * @return The RubycScript object of this rubyc.
     */
    public RubycScript getScript() { return script; }
    
    /**
     * @return The rubyc chip running this RubyCircuit.
     */
    public rubyc getCircuit() { return circuit; }
    
    /**
     * @return The rubyc chip sign arguments.
     */
    public String[] getArgs() { return args; }
    
    /**
     * @return A list of the circuit interface blocks.
     */
    public InterfaceBlock[] getInterfaces() { return circuit.interfaceBlocks; }
    
    /**
     * @return true if any circuit listeners are registered to this rubyc chip. 
     */
    public boolean has_listeners() { return circuit.hasListeners(); }

    /**
     * @return This RubyCircuit script name without the .rb extension.
     */
    public String getScriptName() {
        return scriptName;
    }
    
    /**
     * @return The current command sender if there is one.
     */
    public CommandSender getSender() {
        return currentSender;
    }
    
    /**
     * Schedules a synchronized task to run immediately using the bukkit scheduler.
     * This task will be executed on the main server thread.
     * 
     * @param task The task to schedule.
     * @return The id number of this task.
     */
    public int scheduleSyncTask(Runnable task) {
        return rc.getServer().getScheduler().scheduleSyncDelayedTask(rc, task);
    }

    /**
     * Schedules a synchronized task to run after a time delay using the bukkit scheduler.
     * This task will be executed on the main server thread.
     * 
     * @param delay Delay time in server ticks.
     * @param task Task to schedule.
     * @return The id number of this task.
     */
    public int scheduleSyncDelayedTask(long delay, Runnable task) {
        return rc.getServer().getScheduler().scheduleSyncDelayedTask(rc, task, delay);
    }
    
    /**
     * Schedules a repeating synchronized task to run after a certain time delay using the bukkit scheduler.
     * This task will be executed on the main server thread.
     * 
     * @param delay Delay time in server ticks before the task is run for the 1st time.
     * @param period The duration in server ticks between each repeated execution of the task.
     * @param task Task to schedule.
     * @return The id number of this task.
     */
    public int scheduleSyncRepeatingTask(long delay, long period, Runnable task) {
        return rc.getServer().getScheduler().scheduleSyncRepeatingTask(rc, task, delay, period);
    }

    /**
     * Schedules a task to run immediately using the bukkit scheduler.
     * This task will be executed by a thread managed by the scheduler.
     * 
     * @param task The task to schedule.
     * @return The id number of this task.
     */    
    public int scheduleAsyncTask(Runnable task) {
        return rc.getServer().getScheduler().scheduleAsyncDelayedTask(rc, task);
    }

    /**
     * Schedules a task to run after a time delay using the bukkit scheduler.
     * This task will be executed by a thread managed by the scheduler.
     * 
     * @param delay Delay time in server ticks.
     * @param task Task to schedule.
     * @return The id number of this task.
     */
    public int scheduleAsyncDelayedTask(long delay, Runnable task) {
        return rc.getServer().getScheduler().scheduleAsyncDelayedTask(rc, task, delay);
    }

    /**
     * Schedules a repeating task to run after a certain time delay using the bukkit scheduler.
     * This task will be executed by a thread managed by the scheduler.
     * 
     * @param delay Delay time in server ticks before the task is run for the 1st time.
     * @param period The duration in server ticks between each repeated execution of the task.
     * @param task Task to schedule.
     * @return The id number of this task.
     */    
    public int scheduleAsyncRepeatingTask(long delay, long period, Runnable task) {
        return rc.getServer().getScheduler().scheduleAsyncRepeatingTask(rc, task, delay, period);
    }
    
    /**
     * Removes task from scheduler.
     *
     * @param taskId Id number of task to be removed
     */    
    public void cancelTask(int taskId) {
        rc.getServer().getScheduler().cancelTask(taskId);
    }
}
