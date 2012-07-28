package com.neuronrobotics.nrconsole.plugin.kinematics;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleKinematicsLabPlugin extends AbstractNRConsoleTabedPanelPlugin {

	public static final String[] myNames ={"neuronrobotics.dyio.*","bcs.pid.*"};
	
	public NRConsoleKinematicsLabPlugin( PluginManager pm) {
		super(myNames, pm);
	}

	@Override
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		return new KinematicsLab();
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return null;
	}

}
