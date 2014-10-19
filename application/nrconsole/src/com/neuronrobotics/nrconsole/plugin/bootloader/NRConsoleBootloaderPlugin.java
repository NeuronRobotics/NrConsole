package com.neuronrobotics.nrconsole.plugin.bootloader;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleBootloaderPlugin extends AbstractNRConsoleTabedPanelPlugin {
	private  BootloaderPanel bcp = new  BootloaderPanel();
	public static final String[] myNames ={"neuronrobotics.bootloader.*","bcs.bootloader.*"};

	public NRConsoleBootloaderPlugin(PluginManager pm){
		super(myNames,pm);
	}
	
	
	public JPanel getTabPane() {
		return bcp;
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		return bcp.setConnection(connection);
	}


	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return null;
	}
}
