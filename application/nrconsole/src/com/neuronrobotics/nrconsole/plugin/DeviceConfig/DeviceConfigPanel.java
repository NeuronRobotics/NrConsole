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
import com.neuronrobotics.replicator.driver.Slic3r;
import com.neuronrobotics.replicator.driver.SliceStatusData;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.commands.bcs.io.GetValueCommand;
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
	

	
	long lastUpdate = 0;
	public String fileName = "None";

	private JPanel pnlAction;
	private JButton btnReloadConfigs;
	private JButton btnWriteConfigs;
	private JTabbedPane tabPnlSettings;
	private JScrollPane pnlSlic3rSetts;
	
	private Slic3rMasterPanel slic3rSettingsPanel = new Slic3rMasterPanel();
	private LocalSettingsPanel localSettingsPanel = new LocalSettingsPanel();
	
	public DeviceConfigPanel() {
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				initComponents();
				
			}
		});

	}

	public void setDevices(BowlerBoardDevice delt, NRPrinter printer) {
		this.printer = printer;

		
		
	}

	
	private void initComponents() {
		setLayout(new MigLayout("", "[grow]", "[][grow]"));
		add(getPnlAction(), "cell 0 0,grow");
		add(getTabPnlSettings(), "cell 0 1,grow");
	}

	
	public void updateSettings(){
			printer.reloadSlic3rSettings();
		
		
			slic3rSettingsPanel.setValue(0, new MachineSetting<Double>("NozzleDia" ,printer.getSlicer().getNozzle_diameter()));
			slic3rSettingsPanel.setValue(1, new MachineSetting<Double>("PCenterX" ,printer.getSlicer().getPrintCenter()[0]));
			slic3rSettingsPanel.setValue(2, new MachineSetting<Double>("PCenterY" ,printer.getSlicer().getPrintCenter()[1]));
			slic3rSettingsPanel.setValue(3, new MachineSetting<Double>("FilaDia" ,printer.getSlicer().getFilimentDiameter()));
			slic3rSettingsPanel.setValue(4, new MachineSetting<Double>("ExtMult" ,printer.getSlicer().getExtrusionMultiplier()));
			slic3rSettingsPanel.setValue(5, new MachineSetting<Integer>("PTemp" , printer.getSlicer().getTempreture()));
			slic3rSettingsPanel.setValue(6, new MachineSetting<Integer>("BTemp" , printer.getSlicer().getBedTempreture()));
			slic3rSettingsPanel.setValue(7, new MachineSetting<Double>("LayerHeight" ,printer.getSlicer().getLayerHeight()));
			slic3rSettingsPanel.setValue(8, new MachineSetting<Integer>("WallThickness" , printer.getSlicer().getWallThickness()));
			slic3rSettingsPanel.setValue(9, new MachineSetting<Boolean>("UseSupport" ,printer.getSlicer().isUseSupportMaterial()));
			slic3rSettingsPanel.setValue(10, new MachineSetting<Double>("RetractLength" ,printer.getSlicer().getRetractLength()));
			slic3rSettingsPanel.setValue(11, new MachineSetting<Integer>("TravelSpd" ,printer.getSlicer().getTravilSpeed()));
			slic3rSettingsPanel.setValue(12, new MachineSetting<Integer>("PeriSpd" ,printer.getSlicer().getPerimeterSpeed()));
			slic3rSettingsPanel.setValue(13, new MachineSetting<Integer>("BridgeSpd" ,printer.getSlicer().getBridgeSpeed()));
			slic3rSettingsPanel.setValue(14, new MachineSetting<Integer>("GapFillSpd" ,printer.getSlicer().getGapFillSpeed()));
			slic3rSettingsPanel.setValue(15, new MachineSetting<Integer>("InfillSpd" ,printer.getSlicer().getInfillSpeed()));
			slic3rSettingsPanel.setValue(16, new MachineSetting<Integer>("SupPeriSpdPcnt" ,printer.getSlicer().getSupportMaterialInterfaceSpeedPercent()));
			slic3rSettingsPanel.setValue(17, new MachineSetting<Integer>("SmPeriSpdPcnt" ,printer.getSlicer().getSmallPerimeterSpeedPercent()));
			slic3rSettingsPanel.setValue(18, new MachineSetting<Integer>("ExtPeriSpdPcnt" ,printer.getSlicer().getExternalPerimeterSpeedPercent()));
			slic3rSettingsPanel.setValue(19, new MachineSetting<Integer>("SolidInfillSpdPcnt" ,printer.getSlicer().getSolidInfillSpeedPercent()));
			slic3rSettingsPanel.setValue(20, new MachineSetting<Integer>("TopSolidInfillSpdPcnt" ,printer.getSlicer().getTopSolidInfillSpeedPercent()));
			slic3rSettingsPanel.setValue(21, new MachineSetting<Integer>("SupportMatIntSpdPcnt" ,printer.getSlicer().getSupportMaterialSpeed()));
			slic3rSettingsPanel.setValue(22, new MachineSetting<Integer>("FirstLayerSpdPcnt" ,printer.getSlicer().getFirstLayerSpeedPercent()));
			localSettingsPanel.reloadAllSettings();
		
	}
	
	public void writeSettings(){
		slic3rSettingsPanel.checkNewSettings();
		
		Slic3r newSettings = new Slic3r(
				 slic3rSettingsPanel.getDoubleValue(0),
				 new double[]{slic3rSettingsPanel.getDoubleValue(1),slic3rSettingsPanel.getDoubleValue(2)},
				 slic3rSettingsPanel.getDoubleValue(3),
				 slic3rSettingsPanel.getDoubleValue(4),
				 slic3rSettingsPanel.getIntegerValue(5),
				 slic3rSettingsPanel.getIntegerValue(6),
				 slic3rSettingsPanel.getDoubleValue(7),
				 slic3rSettingsPanel.getIntegerValue(8),
				 slic3rSettingsPanel.getBooleanValue(9),
				 slic3rSettingsPanel.getDoubleValue(10),
				 slic3rSettingsPanel.getIntegerValue(11),
				 slic3rSettingsPanel.getIntegerValue(12),
				 slic3rSettingsPanel.getIntegerValue(13),
				 slic3rSettingsPanel.getIntegerValue(14),
				 slic3rSettingsPanel.getIntegerValue(15),
				 slic3rSettingsPanel.getIntegerValue(16),
				 slic3rSettingsPanel.getIntegerValue(17),
				 slic3rSettingsPanel.getIntegerValue(18),
				 slic3rSettingsPanel.getIntegerValue(19),
				 slic3rSettingsPanel.getIntegerValue(20),
				 slic3rSettingsPanel.getIntegerValue(21),
				 slic3rSettingsPanel.getIntegerValue(22)); 
		printer.getDeltaDevice().setSlic3rConfiguration(newSettings);
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
			btnReloadConfigs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					updateSettings();
				}
			});
		}
		return btnReloadConfigs;
	}
	private JButton getBtnWriteConfigs() {
		if (btnWriteConfigs == null) {
			btnWriteConfigs = new JButton("Write Configs");
			btnWriteConfigs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					writeSettings();
				}
			});
		}
		return btnWriteConfigs;
	}
	private JTabbedPane getTabPnlSettings() {
		if (tabPnlSettings == null) {
			tabPnlSettings = new JTabbedPane(JTabbedPane.TOP);
			tabPnlSettings.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					try {
						updateSettings();
					} catch (Exception e) {
						
					}
					
				}
			});
			
			tabPnlSettings.addTab(slic3rSettingsPanel.getPanelName(), null, slic3rSettingsPanel, null);
			tabPnlSettings.addTab(localSettingsPanel.getPanelName(), null, localSettingsPanel, null);
			
		}
		return tabPnlSettings;
	}
	
	
}

	
