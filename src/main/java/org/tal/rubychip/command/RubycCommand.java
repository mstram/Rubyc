package org.tal.rubychip.command;

import java.io.IOException;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tal.redstonechips.RedstoneChips;
import org.tal.redstonechips.circuit.Circuit;
import org.tal.redstonechips.command.CommandUtils;
import org.tal.redstonechips.util.ParsingUtils;
import org.tal.redstonechips.util.Range;
import org.tal.redstonechips.util.Tokenizer;
import org.tal.rubychip.RubyCircuit;
import org.tal.rubychip.RubycLibrary;
import org.tal.rubychip.RubycScript;
import org.tal.rubychip.ScriptManager;
import org.tal.rubychip.rubyc;
import org.tal.rubychip.script.AddCommand;
import org.tal.rubychip.script.DeleteCommand;
import org.tal.rubychip.script.InsertCommand;
import org.tal.rubychip.script.ReplaceCommand;

/**
 *
 * @author Tal Eisenberg
 */
public class RubycCommand implements CommandExecutor {
    private RedstoneChips rc;    
    private RubycLibrary lib;
            
    public RubycCommand(RubycLibrary lib) { 
        this.rc = lib.getRC();
        this.lib = lib;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equals("rubyc")) return false;
        if (args.length>0) {
            if ("list".startsWith(args[0])) {
                listScripts(sender);
                return true;
            }
        } 

        rubyc r = null;
        String[] newargs;
        
        if (args.length>0 && args[0].startsWith("#")) {
            String id = args[0].substring(1);
            Circuit c = rc.getCircuitManager().getCircuitById(id);
            newargs = new String[args.length-1];
            System.arraycopy(args, 1, newargs, 0, newargs.length);
            
            if (c!=null && c instanceof rubyc) {
                r = (rubyc)c;
            } else {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "Unknown chip id or not a rubyc chip: " + id);
                return true;
            }            
        } else {
            Circuit c = CommandUtils.findTargetCircuit(rc, sender, false);
            if (c!=null && (c instanceof rubyc)) {
                r = (rubyc)c;
                newargs = args;
            } else {
                generalHelp(sender);
                return true;
            }
        }
        
        if (r==null) return true;
        
        if (!chipCommands(sender, newargs, r)) {
            sender.sendMessage(rc.getPrefs().getErrorColor() + "Bad command syntax.");
        }
        
        return true;
    }

    private boolean chipCommands(CommandSender sender, String[] args, rubyc chip) {
        if (args.length==0) {
            // spout popup
            /*
            if (lib.isSpoutEnabled() && (sender instanceof Player)) {
                ScriptWindow popup = new ScriptWindow((Player)sender, chip, lib);
                popup.open();
            }
            */
                
            editHelp(sender, chip);
            return true;
        } else {
            if ("print".startsWith(args[0])) {
                printLines(sender, args, chip.getRubyCircuit());
                return true;
            } else {
                boolean res;
                boolean automaticReload = true;
                
                if ("replace".startsWith(args[0])) {
                    res = replaceLines(sender, args, chip);
                } else if ("delete".startsWith(args[0])) {
                    res = deleteLines(sender, args, chip);
                } else if ("insert".startsWith(args[0])) {
                    res = insertLines(sender, args, chip);
                } else if ("add".startsWith(args[0])) {
                    res = addLines(sender, args, chip);
                } else if (args[0].equals("save")) {
                    save(sender, args, chip.getRubyCircuit());
                    res = true;
                } else if ("undo".startsWith(args[0])) {
                    RubycScript s = chip.getRubyCircuit().getScript();
                    if (s.hasUndo()) {
                        sender.sendMessage(ChatColor.GRAY + "Undo " + s.getUndoStack().peek().getName() + ".");
                        s.undoEditCommand();
                        res = true;
                    } else {
                        sender.sendMessage(rc.getPrefs().getInfoColor() + "Nothing to undo.");
                        return true;
                    }
                    
                } else if ("redo".startsWith(args[0])) {
                    RubycScript s = chip.getRubyCircuit().getScript();
                    if (s.hasRedo()) {
                        sender.sendMessage(ChatColor.GRAY + "Redo " + s.getRedoStack().peek().getName() + ".");
                        s.redoEditCommand();
                        res = true;
                    } else {
                        sender.sendMessage(rc.getPrefs().getInfoColor() + "Nothing to redo.");
                        return true;
                    }
                    
                } else res = false;
                
                if (res && automaticReload) chip.reloadScript(sender);
                
                return res;
            } 
        } 
    }
    
    private void generalHelp(CommandSender sender) {        
        ChatColor extraColor = ChatColor.YELLOW;        
        ChatColor infoColor = rc.getPrefs().getInfoColor();
        ChatColor errorColor = rc.getPrefs().getErrorColor();
        
        String help = "";
        String title = ChatColor.LIGHT_PURPLE + lib.getName() + " " + lib.getVersion() + " " + 
                (lib.isJRubyLoaded()?ChatColor.AQUA + "(JRuby loaded)":ChatColor.GRAY + "(JRuby missing!)");

        help += infoColor + "Run the command while pointing at a rubyc chip to edit script.\n";
        help += ChatColor.WHITE + "/rubyc #<id> ..." + extraColor + " - enter a chip id as 1st argument to remote edit.\n";
        help += ChatColor.WHITE + "/rubyc list" + extraColor + " - lists all available scripts.\n";
        help += infoColor + "alias: " + ChatColor.WHITE + "/rb";

        CommandUtils.pageMaker(sender, title, "rubyc", help, infoColor, errorColor);
    }
    
    private void editHelp(CommandSender sender, rubyc r) {
        ChatColor extraColor = ChatColor.YELLOW;        
        ChatColor infoColor = rc.getPrefs().getInfoColor();
        
        String scriptPath = RubycScript.getScriptFile(r.getRubyCircuit().getScriptName()).getName();
        String help = "";
        String title = extraColor + r.getChipString() + infoColor + " running " + extraColor + scriptPath + "\n";
        help += infoColor + "Script editing commands:" + "\n";
        help += ChatColor.WHITE + "/rubyc print [line-range]" + extraColor + " - prints the rubyc script.\n";
        help += ChatColor.WHITE + "/rubyc replace <line-range> 'line'...'line'" + extraColor + " - replaces selected lines with a new line.\n";
        help += ChatColor.WHITE + "/rubyc delete <line-range>" + extraColor + " - deletes all lines in range" + "\n";
        help += ChatColor.WHITE + "/rubyc insert <line-num> 'line'...'line'" + extraColor + " - insert new code lines after line-num.\n";
        help += ChatColor.WHITE + "/rubyc save [new-name]" + extraColor + " - save any changes to the same script or a new one." + "\n";
        help += ChatColor.WHITE + "/rubyc undo" + extraColor + " - undo last edit command.\n";
        help += ChatColor.WHITE + "/rubyc redo" + extraColor + " - redo last edit command.\n\n";

        help += infoColor + "line-range syntax: " + extraColor + " first-line..last-line" + infoColor + ".\n";
        help += infoColor + "code lines must be surrounded by ''." + "\n";
        help += infoColor + "alias: " + ChatColor.WHITE + "/rb" + infoColor + ". Partial command names are allowed.\n\n";
        help += infoColor + "examples:\n";
        help += ChatColor.WHITE + "   /rb p" + extraColor + " - print the whole script.\n";
        help += ChatColor.WHITE + "   /rb print 10..15" + extraColor + " - print script lines 10 to 15.\n";
        help += ChatColor.WHITE + "   /rb rep 10..11 '# comment'" + extraColor + " - replace lines 10 to 11 with a new line.\n";
        help += ChatColor.WHITE + "   /rb del 10.." + extraColor + " - delete script from line 10 to its end.\n";
        help += ChatColor.WHITE + "   /rb i 11 'def init' '   info \"test\"'" + extraColor + " - insert 2 lines between lines 10 and 11.\n";
        CommandUtils.pageMaker(sender, title, "rubyc", help, infoColor, rc.getPrefs().getErrorColor());
    }
    
    private void listScripts(CommandSender sender) {
        List<String> list = ScriptManager.getAvailableScripts(ScriptManager.createRuntime());
        if (list.isEmpty()) {
            sender.sendMessage(rc.getPrefs().getInfoColor() + "There are no scripts yet.");
        } else {
            String c = "";
            for (String name : list) {
                c += name + ", ";
            }

            c = c.substring(0, c.length()-2);
            
            CommandUtils.pageMaker(sender, "Available scripts", "rubyc list", c, rc.getPrefs().getInfoColor(), rc.getPrefs().getErrorColor());
        }
        
    }

    private void printLines(CommandSender sender, String[] args, RubyCircuit r) {
        int[] range;

        if (args.length>1) {
            range = rangeToLines(sender, args[1]);
        } else range = new int[] {0, RubycScript.LAST_LINE};
        
        if (range!=null)
            CommandUtils.pageMaker(sender, r.getScriptName() + ".rb", "rubyc", new ScriptLineSource(r.getScript(), range[0], range[1]), rc.getPrefs().getInfoColor(), rc.getPrefs().getErrorColor());
    }

    private boolean replaceLines(CommandSender sender, String[] args, rubyc chip) {
        if (args.length<3) return false;
        
        int[] range = rangeToLines(sender, args[1]);
        String[] lines = parseLines(sender, args, 2);
        
        if (lines==null || range==null) return false;
        
        try {
            chip.getRubyCircuit().getScript().runEditCommand(new ReplaceCommand(lines, range[0], range[1]));
        } catch (IllegalArgumentException e) { 
            sender.sendMessage(rc.getPrefs().getErrorColor() + e.getMessage());
        }

        return true;        
    }
    
    private boolean deleteLines(CommandSender sender, String[] args, rubyc chip) {
        if (args.length<2) return false;
        
        int[] range = rangeToLines(sender, args[1]);

        if (range==null) return false;

        try {
            chip.getRubyCircuit().getScript().runEditCommand(new DeleteCommand(range[0], range[1]));
        } catch (IllegalArgumentException e) { 
            sender.sendMessage(rc.getPrefs().getErrorColor() + e.getMessage());
        }

        return true;
    }

    private boolean insertLines(CommandSender sender, String[] args, rubyc chip) {
        if (args.length<3) return false;
        if (!ParsingUtils.isInt(args[1])) return false;

        int beforeLine = Integer.parseInt(args[1]);

        String[] insert = parseLines(sender, args, 2);
        if (insert==null) return true;

        try {
            chip.getRubyCircuit().getScript().runEditCommand(new InsertCommand(beforeLine, insert));
        } catch (IllegalArgumentException e) { 
            sender.sendMessage(rc.getPrefs().getErrorColor() + e.getMessage());
        }

        return true;        
    }

    private boolean addLines(CommandSender sender, String[] args, rubyc chip) {
        if (args.length<2) return false;
        
        String[] lines = parseLines(sender, args, 1);
        if (lines==null) return true;
        
        chip.getRubyCircuit().getScript().runEditCommand(new AddCommand(lines));
        
        return true;
    }
    
    private void save(CommandSender sender, String[] args, RubyCircuit c) {
        try {
            if (args.length<2)
                c.getScript().save();
            else c.getScript().save(args[1]);
            sender.sendMessage(rc.getPrefs().getInfoColor() + "Saved script to: " + c.getScript().getFile());
        } catch (IOException e) {
            sender.sendMessage(rc.getPrefs().getErrorColor() + e.toString());
        } catch (RuntimeException e) {
            sender.sendMessage(rc.getPrefs().getErrorColor() + e.getMessage());
        }         
    }

    private int[] rangeToLines(CommandSender sender, String range) {
        try {
            Range r = new Range(range, Range.Type.OPEN_ALLOWED);
            int[] ret = new int[2];
            ret[0] = (int)(r.hasLowerLimit()?r.getOrderedRange()[0]:0);
            ret[1] = (int)(r.hasUpperLimit()?r.getOrderedRange()[1]:RubycScript.LAST_LINE);

            return ret;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(rc.getPrefs().getErrorColor() + e.getMessage());
            return null;
        }

    }

    private String[] parseLines(CommandSender sender, String[] args, int idx) {
        String arg = "";
        for (int i=idx; i<args.length; i++)
            arg += args[i] + " ";
        
        Tokenizer t = new Tokenizer(arg, ' ');
        String[] lines = t.getTokens();
        for (int i=0; i<lines.length; i++) {
            if (lines[i].charAt(0)=='\'' && lines[i].charAt(lines[i].length()-1)=='\'')
                lines[i] = lines[i].substring(1, lines[i].length()-1);
            else {
                sender.sendMessage("Lines must be surrounded by ''.");
                return null;
            }                
        }
        
        return lines;
    }

    
}
