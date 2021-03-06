package com.neuronrobotics.nrconsole.plugin.DyIO.hexapod;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class HexapodNRConsolePulgin extends AbstractNRConsoleTabedPanelPlugin {
	public static final String[] myNames ={"neuronrobotics.dyio.*"};
	private HexapodConfigPanel hex;
	public HexapodNRConsolePulgin(PluginManager pm){
		super(myNames,pm);
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
		hex.setDyIO();
		return hex.setConnection(connection);
	}



	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return new Dimension(1050,1200);
	}

}
