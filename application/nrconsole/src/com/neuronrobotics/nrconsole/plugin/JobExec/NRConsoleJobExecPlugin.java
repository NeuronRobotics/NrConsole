package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Dimension;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleJobExecPlugin extends AbstractNRConsoleTabedPanelPlugin{
	public static final String[] myNames ={"bcs.cartesian.*"};
	
	private JobExecPanel gui;
	private JPanel holder;
	private BowlerBoardDevice delt = new BowlerBoardDevice();
	private NRPrinter printer=null;
	private Dimension minSize = new Dimension(693, 476);
	private PluginManager manager;
	public NRConsoleJobExecPlugin(PluginManager pm) {
		super(myNames, pm);
		//getTabPane();
		//manager = pm;
		//manager.addNRConsoleTabedPanelPlugin(this);
		
	}

	@Override
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		if(gui== null){
			//gui = new JPanel(new MigLayout());
			gui = new JobExecPanel();
			gui.setName("Job Executor");
		}
		return gui;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		delt.setConnection(connection);
		delt.connect();
		printer = new NRPrinter(delt);
				
		//printer.
		return delt.isAvailable();
		
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		
		return minSize;
	}

}
