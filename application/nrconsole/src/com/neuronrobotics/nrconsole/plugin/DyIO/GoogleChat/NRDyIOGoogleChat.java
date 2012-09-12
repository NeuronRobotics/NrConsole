package com.neuronrobotics.nrconsole.plugin.DyIO.GoogleChat;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler.SchedulerGui;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRDyIOGoogleChat extends AbstractNRConsoleTabedPanelPlugin {
	public static final String[] myNames ={"neuronrobotics.dyio.*"};
	private GoogleChatLogin gui;
	public NRDyIOGoogleChat(PluginManager pm) {
		super(myNames, pm);
	}

	@Override
	public JPanel getTabPane() {
		if(gui==null)
			gui=new GoogleChatLogin();
		return gui;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		getTabPane();
		return gui.setConnection(connection);
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return null;
	}

}
