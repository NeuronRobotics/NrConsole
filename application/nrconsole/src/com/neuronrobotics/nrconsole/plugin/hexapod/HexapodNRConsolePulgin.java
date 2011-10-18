package com.neuronrobotics.nrconsole.plugin.hexapod;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;

public class HexapodNRConsolePulgin extends AbstractNRConsoleTabedPanelPlugin {
	public static final String[] myNames ={"neuronrobotics.dyio.*"};
	private HexapodConfigPanel hex;
	public HexapodNRConsolePulgin(PluginManager pm){
		super(myNames,pm);
		hex.setDyIO();
	}
	
	public JPanel getTabPane() {
		if(hex == null)
			hex = new HexapodConfigPanel();
		hex.setSize(getMinimumWimdowDimentions());
		return hex;
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		if(hex == null)
			hex = new HexapodConfigPanel();
		return hex.setConnection(connection);
	}



	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return new Dimension(1250,850);
	}

}
