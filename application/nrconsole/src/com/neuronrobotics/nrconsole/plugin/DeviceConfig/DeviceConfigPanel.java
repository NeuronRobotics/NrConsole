package com.neuronrobotics.nrconsole.plugin.DeviceConfig;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.jme3.system.AppSettings;
import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.GCodeFilter;
import com.neuronrobotics.nrconsole.util.StlFilter;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.PrinterStatus;
import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.replicator.driver.PrinterStatusListener;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.replicator.driver.SliceStatusData;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;







//import com.sun.deploy.uitoolkit.impl.fx.Utils;
//import com.sun.deploy.uitoolkit.impl.fx.Utils;
import javax.swing.JSplitPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Font;
import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JTabbedPane;
import javax.swing.JRadioButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DeviceConfigPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 12345L;
	private NRPrinter printer;
	File gCodes = null;
	FileInputStream gCodeStream;
	double currpos = 0;
	private int channelHotEnd = -1;
	private int channelBed = -1;


	private boolean isWarn = false;
	private boolean isIllegal = false;
	private boolean isPaused;
	long lastUpdate = 0;
	public String fileName = "None";
	private ArrayList<File> files = new ArrayList<File>();
	private ArrayList<String> fileNames = new ArrayList<String>();
	private JPanel pnlAction;
	private JButton btnReloadConfigs;
	private JButton btnWriteConfigs;
	private JTabbedPane tabPnlSettings;
	private JScrollPane pnlSlic3rSetts;
	private JPanel pnlViewSetts;
	private JRadioButton rdbtnShowAllSettings;
	private JPanel pnlSlic3rSetts_1;
	public DeviceConfigPanel() {
		setLayout(new MigLayout("", "[grow]", "[][grow]"));
		add(getPnlAction(), "cell 0 0,grow");
		add(getTabPnlSettings(), "cell 0 1,grow");
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				initComponents();
			}
		});

	}

	public void setDevices(BowlerBoardDevice delt, NRPrinter printer) {
		this.printer = printer;

		for (LinkConfiguration link : printer.getLinkConfigurations()) {
			if (link.getName().toLowerCase().contains("hotend")) {
				channelHotEnd = link.getHardwareIndex();
			}
			if (link.getName().toLowerCase().contains("heat")) {
				channelHotEnd = link.getHardwareIndex();
			}
			if (link.getName().toLowerCase().contains("bed")) {
				channelBed = link.getHardwareIndex();
			}
		}
		
	}

	private void initComponents() {

	}

	private JPanel getPnlAction() {
		if (pnlAction == null) {
			pnlAction = new JPanel();
			pnlAction.setLayout(new MigLayout("", "[][]", "[]"));
			pnlAction.add(getBtnReloadConfigs(), "cell 0 0");
			pnlAction.add(getBtnWriteConfigs(), "cell 1 0");
		}
		return pnlAction;
	}
	private JButton getBtnReloadConfigs() {
		if (btnReloadConfigs == null) {
			btnReloadConfigs = new JButton("Reload Configs");
		}
		return btnReloadConfigs;
	}
	private JButton getBtnWriteConfigs() {
		if (btnWriteConfigs == null) {
			btnWriteConfigs = new JButton("Write Configs");
		}
		return btnWriteConfigs;
	}
	private JTabbedPane getTabPnlSettings() {
		if (tabPnlSettings == null) {
			tabPnlSettings = new JTabbedPane(JTabbedPane.TOP);
			tabPnlSettings.addTab("Slic3r Settings", null, getPnlSlic3rSetts(), null);
		}
		return tabPnlSettings;
	}
	private JScrollPane getPnlSlic3rSetts() {
		if (pnlSlic3rSetts == null) {
			pnlSlic3rSetts = new JScrollPane();
			pnlSlic3rSetts.setColumnHeaderView(getPnlViewSetts());
			pnlSlic3rSetts.setViewportView(getPnlSlic3rSetts_1());
		}
		return pnlSlic3rSetts;
	}
	private JPanel getPnlViewSetts() {
		if (pnlViewSetts == null) {
			pnlViewSetts = new JPanel();
			pnlViewSetts.add(getRdbtnShowAllSettings());
		}
		return pnlViewSetts;
	}
	private JRadioButton getRdbtnShowAllSettings() {
		if (rdbtnShowAllSettings == null) {
			rdbtnShowAllSettings = new JRadioButton("Show All Settings");
		}
		return rdbtnShowAllSettings;
	}
	private JPanel getPnlSlic3rSetts_1() {
		if (pnlSlic3rSetts_1 == null) {
			pnlSlic3rSetts_1 = new JPanel();
			pnlSlic3rSetts_1.setLayout(new MigLayout("", "[grow]", "[grow]"));
		}
		return pnlSlic3rSetts_1;
	}
}

	
