package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.JobExec.Slic3rZip.UnzipUtility;
import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.PrefsLoader;
import com.neuronrobotics.nrconsole.util.Slic3rFilter;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.replicator.driver.Slic3r;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleJobExecPlugin extends AbstractNRConsoleTabedPanelPlugin {
	public static final String[] myNames = { "bcs.cartesian.*" };

	private JobExecPanel gui = new JobExecPanel();
	private JPanel holder;
	private BowlerBoardDevice delt = new BowlerBoardDevice();
	private static NRPrinter printer = null;
	private Dimension minSize = new Dimension(693, 476);

	public NRConsoleJobExecPlugin(PluginManager pm) {
		super(myNames, pm);
		// getTabPane();
		// manager = pm;
		// manager.addNRConsoleTabedPanelPlugin(this);

	}

	@Override
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		if (gui != null) {
			// gui = new JPanel(new MigLayout());
			gui.setName("Job Executor");

		}
		return gui;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		delt.setConnection(connection);
		delt.connect();
		
		PrefsLoader prefs = new PrefsLoader();
		String path = prefs.getSlic3rLocation();
		if (new File(path).exists() == false) {
			try {
				path = UnzipUtility.extractSlic3r();
				path = FileSelectionFactory.GetFile(new File(path),
						"Confirm Location of slic3r", "Select",
						new Slic3rFilter()).getPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				path = FileSelectionFactory.GetFile(null,
						"Location of slic3r", "Select",
						new Slic3rFilter()).getPath();
			}
			prefs.setSlic3rLocation(path);

		}
		Slic3r.setExecutableLocation(path);
		if (NRConsoleJobExecPlugin.getPrinter() == null)
			NRConsoleJobExecPlugin.setPrinter(new NRPrinter(delt));

		if (delt.isAvailable()) {
			gui.setDevices(delt, getPrinter());
		}
		// printer.
		return delt.isAvailable();

	}

	@Override
	public Dimension getMinimumWimdowDimentions() {

		return minSize;
	}

	public static NRPrinter getPrinter() {
		return printer;
	}

	public static void setPrinter(NRPrinter printer) {
		NRConsoleJobExecPlugin.printer = printer;
	}

}
