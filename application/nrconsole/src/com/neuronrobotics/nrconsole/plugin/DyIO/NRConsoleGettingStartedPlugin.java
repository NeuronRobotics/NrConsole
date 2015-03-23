package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleGettingStartedPlugin extends AbstractNRConsoleTabedPanelPlugin {

	public static final String[] myNames ={"neuronrobotics.dyio.*"};
	private GettingStartedPanel content;
	public NRConsoleGettingStartedPlugin(PluginManager pm){
		super(myNames,pm);
	}
	
	public JPanel getTabPane() {
		if(content == null)
			content = new GettingStartedPanel();
		content.setSize(getMinimumWimdowDimentions());
		return content;
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		
		return true;
	}



	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return new Dimension(1400,1000);
	}

}
