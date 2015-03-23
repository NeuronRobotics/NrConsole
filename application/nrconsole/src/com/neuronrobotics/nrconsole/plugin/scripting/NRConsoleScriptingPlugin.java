package com.neuronrobotics.nrconsole.plugin.scripting;

import java.awt.Dimension;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIORegestry;



public class NRConsoleScriptingPlugin extends AbstractNRConsoleTabedPanelPlugin {

	private static final String[] myNamespaces = new String[]{"neuronrobotics.dyio.*"};
	
	ScriptingEngine se =null;

	private PluginManager pm;

	
	public NRConsoleScriptingPlugin( PluginManager pm) {
		super(myNamespaces, pm);
		this.pm = pm;
		
	}
	
	public ArrayList<JMenu> getMenueItems() {
		if(se!=null)
			return se.getMenueItems();
		return null;
	}

	@Override
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		return se;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		// TODO Auto-generated method stub
		se = new ScriptingEngine(DyIORegestry.get(),pm);
		return true;
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return new Dimension(1050,1200);
	}

}
