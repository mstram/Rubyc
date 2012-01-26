package org.tal.rubychip.script;

import org.bukkit.ChatColor;
import net.eisental.common.page.LineSource;
import org.tal.rubychip.RubycScript;

/**
 *
 * @author Tal Eisenberg
 */
public class ScriptLineSource implements LineSource {
    private String[] script;
    private int firstLine, lastLine;
    
    public ScriptLineSource(Script script, int firstLine, int lastLine) {
        this.script = script.getLines();
        this.firstLine = firstLine;

        if (lastLine==RubycScript.LAST_LINE || lastLine>=this.script.length)
            this.lastLine = this.script.length-1;
        else this.lastLine = lastLine;        
    }

    @Override
    public String getLine(int idx) {
        return ChatColor.AQUA + zeroPad(idx+firstLine, lastLine) + " " + ChatColor.WHITE + script[idx+firstLine];
    }

    @Override
    public int getLineCount() {
        return lastLine-firstLine+1;
    }
    
    private String zeroPad(int a, int max) {
        String pad = "";
        String address = Integer.toString(a);
        int charCount = Integer.toString(max).length();
        for (int i=0; i<charCount-address.length(); i++) pad += "0";
        return pad + address;
    }
    
}
