/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tal.rubychip.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.tal.rubychip.RubycLibrary;
import org.tal.rubychip.RubycScript;
import org.tal.rubychip.rubyc;

/**
 *
 * @author Tal Eisenberg
 */
class ScriptWindow extends GenericPopup {
    SpoutPlayer player;
    RubycLibrary lib;
    RubycScript script;
    rubyc chip;

    TextField scriptField;
    
    public ScriptWindow(Player player, rubyc chip, RubycLibrary lib) {
        this.player = (SpoutPlayer)player;
        this.chip = chip;
        this.lib = lib;
        this.script = chip.getRubyCircuit().getScript();
        
        initGUI();
    }

    private void initGUI() {
        Label label = new GenericLabel(ChatColor.RED + script.getName());
        scriptField = new GenericTextField();
        scriptField.setMinHeight(150);
        scriptField.setMinWidth(100);
        scriptField.setTabIndex(3);
        Container c = new GenericContainer();
        c.setLayout(ContainerType.VERTICAL);
        c.addChild(label);
        c.addChild(scriptField);
        attachWidget(lib, c);
    }
    
    public void open() {
        player.getMainScreen().attachPopupScreen(this);

        scriptField.setText(script.getScript());
    }
}
