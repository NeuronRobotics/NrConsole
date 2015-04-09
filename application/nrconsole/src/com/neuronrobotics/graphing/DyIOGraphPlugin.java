package com.neuronrobotics.graphing;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class DyIOGraphPlugin extends AbstractNRConsoleTabedPanelPlugin {

	public static final String[] myNames ={"neuronrobotics.dyio.*"};
	private final GraphingWindow gui =  new GraphingWindow();
	public DyIOGraphPlugin(PluginManager pm) {
		this(myNames,pm);
	}

	public DyIOGraphPlugin(String myNamespaces[],PluginManager pm) {
		super(myNames,pm);
	}
	public JPanel getTabPane() {
		return getGraphingWindow();
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		return true;
	}


	@Override
	public Dimension getMinimumWimdowDimentions() {
		return null;
	}

	public GraphingWindow getGraphingWindow() {
		return gui;
	}

}
