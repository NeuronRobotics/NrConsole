package com.neuronrobotics.nrconsole.plugin.BowlerCam;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleBowlerCameraPlugin extends AbstractNRConsoleTabedPanelPlugin{

	private BowlerCamPanel bcp = new BowlerCamPanel();
	public static final String[] myNames ={"neuronrobotics.bowlercam.*"};
	public NRConsoleBowlerCameraPlugin(PluginManager pm){
		super(myNames,pm);
	}
	
	
	public JPanel getTabPane() {
		return bcp;
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		// TODO Auto-generated method stub
		return bcp.setConnection(connection);
	}


	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return null;
	}

}
