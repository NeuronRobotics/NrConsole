package com.neuronrobotics.nrconsole.plugin;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JPanel;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public interface INRConsoleTabedPanelPlugin {
	public JPanel getTabPane();
	public ArrayList<JMenu> getMenueItems();
	public boolean isMyNamespace( ArrayList<String> names);
	public boolean isAcvive();
	public boolean setConnection(BowlerAbstractConnection connection);
	public Dimension getMinimumWimdowDimentions();
	public void setActive(boolean b);
}
