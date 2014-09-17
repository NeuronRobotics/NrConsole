package com.neuronrobotics.nrconsole.plugin.JobExec;

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

import com.jme3.math.Vector3f;
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

import javax.swing.JSplitPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

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

public class JobExecPanel extends JPanel implements PrinterStatusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 12345L;
	private NRPrinter printer;
	
	private GCodeLoader codeOps;
	File gCodes = null;
	FileInputStream gCodeStream;
	
	double currpos = 0;
	
	private int channelHotEnd = -1;
	private int channelBed = -1;
	MachineSimDisplay app;
	
	private boolean isGoodFile;
	private boolean isWarn = false;
	private boolean isIllegal = false;
	private boolean isPaused;
	
	long lastUpdate = 0;
	
	public String fileName = "None";	
	private JProgressBar progressBar;

	
	private JPanel topButtonsPanel;//This is the JPanel containing btnOpen3DFile, btnRunJob, btnPausePrint. It is located at column 1, row 0 of the JobExecPanel.
	private JButton btnOpen3DFile;//JButton used to open a gcode file. Set up using the "getBtnOpen3DFile", calls the "actionForBtnOpen3DFile" function when pressed.
	private JButton btnRunJob;
	private JButton btnPausePrint;
	
	private JPanel panel_6;//contains btnHomePrinter, lblTempSetpoint, spinnerTemp.
	private JButton btnHomePrinter;
	private JLabel lblTempSetpoint;
	private JSpinner spinnerTemp;
	

	private JSplitPane splitPane;//Contains panel_1, panel_2
	
	private JPanel panel_1;//Contains splitPane_1
	private JSplitPane splitPane_1;//Contains panel_7, panel_8
	private JPanel panel_7;//contains lblPrintLog, scroll pane
	private JLabel lblPrintLog;
	private JScrollPane scrollPane;//contains textPaneLog
	private JTextPane textPaneLog;
	private JPanel panel_8;//Contains lblPrintQueue, list;
	private JLabel lblPrintQueue;
	private JList<String> list;
	
	
	private JPanel panel_2;//Contains panel
	private JPanel panel;//Contains 3d rendering, layersSlider, panel_5
	private JSlider layersSlider;
	private JPanel panel_5;//Contains chckbxShowAxes, tfLayerShown
	private JCheckBox chckbxShowAxes;
	private JTextField tfLayerShown;
	
	private JPanel panel_4;//Contains chckbxShowGoodLines, chckbxShowTroubledLines, chckbxShowDangerousLines, chckbxShowNonextrudeLines.
	private JCheckBox chckbxShowGoodLines;
	private JCheckBox chckbxShowTroubledLines;
	private JCheckBox chckbxShowDangerousLines;
	private JCheckBox chckbxShowNonextrudeLines;
	private BowlerBoardDevice delt;
	public ArrayList<PrintObject> objects = new ArrayList<PrintObject>();

	private JButton btnEmergencyStop;

	
	public JobExecPanel() {
		

	}

	public void setDevices(BowlerBoardDevice _delt, NRPrinter printer) {
		this.printer = printer;
		delt = _delt;
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
		printer.addPrinterStatusListener(this);
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				initComponents();
			}
		});
	}

	/**
	 * Initialize and add components and panels to this JobExecPanel.
	 * Set layout of this JobExecPanel
	 */
	private void initComponents() {
		getPanel_1();// initialize the 3d engine
		setLayout(new MigLayout("", "[][157px,grow]", "[][center][grow][grow][]"));
		add(getTopButtonsPanel(), "cell 1 0,growx");
		add(getPanel_6(), "cell 1 1,grow");
		add(getProgressBar(), "cell 0 0 1 5,growy");
		add(getSplitPane(), "cell 1 2,grow");

		setMinimumSize(new Dimension(693, 476));
		add(getPanel_4(), "cell 1 3,grow");
		add(getBtnEmergencyStop(), "cell 1 4,growx");

	}

	/**
	 * Instantiate, set up, and return the "btnRunJob" JButton.
	 * This button is located in the topButtonsPanel, and calls the method
	 * "startPrint" when pressed.
	 * 
	 * @return btnRunJob The newly setup "btnRunJob" JButton
	 */
	private JButton getJButtonRunJob() {
		if (btnRunJob == null) {
			btnRunJob = new JButton();
			btnRunJob.setText("Run Job");
			btnRunJob.setEnabled(false);
			btnRunJob.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (isIllegal) {
						showDangerDialog();

					}
					if (isWarn) {
						showWarningDialog();
					}
				}
			});
			btnRunJob.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					startPrint();
				}
			});
		}
		return btnRunJob;
	}

	/**
	 * Instantiates,  sets up, and returns the "btnOpen3DFile" JButton.
	 * This button is located in the topButtonsPanel, and calls the method
	 * "actionForBtnOpen3DFile" when pressed.
	 * 
	 * @return btnOpen3DFile The newly setup "btnOpen3DFile" JButton
	 */
	private JButton getBtnOpen3DFile() {
		if (btnOpen3DFile == null) {
			btnOpen3DFile = new JButton();
			btnOpen3DFile.setText("Open 3d File");
			btnOpen3DFile.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					actionForBtnOpen3DFile(event);
				}
			});
		}
		return btnOpen3DFile;
	}


	public PrintObject objToDisplay(){
		if (getList().isSelectionEmpty()){
			return null;
		}
		return objects.get(getList().getSelectedIndex());
	}
	public void updatePrintInfo() {
		try {
			getLblNumdanger().setText(Integer.toString(objToDisplay().getNumFailLines()));
			getLblNumgood().setText(Integer.toString(objToDisplay().getNumGoodLines()));
			getLblNumtroubled().setText(Integer.toString(objToDisplay().getNumProblemLines()));
			getLblNumnonextrude().setText(Integer.toString(objToDisplay().getNumMoveLines()));
				getTfLayerShown().setText(
						"(File: " + objToDisplay().getName() + ") (# Layers: "
								+ getSliderLayer().getMaximum() + ") (# Layers Shown: "
								+ getSliderLayer().getValue() + ")");
		} catch (Exception e) {
			// TODO: handle exception
		}
	
	}

	private void loadGcodeFile(File _gCodes) {
		try {
			
			
			isIllegal = false;
			isWarn = false;
			fileName = _gCodes.getName();
			updatePrintInfo();
			app.loadingGCode();
			gCodeStream = new FileInputStream(_gCodes);
			codeOps = new GCodeLoader(getConfigs());

			isGoodFile = codeOps.loadCodes(gCodeStream);

			if (!isGoodFile) {
				JOptionPane
						.showMessageDialog(
								null,
								"The selected G-Code File is broken and cannot be loaded or printed",
								"Error Loading File", JOptionPane.ERROR_MESSAGE);

			}
			// codeOps.getCodes().printOutput();
			getJButtonRunJob().setEnabled(isGoodFile);
			getBtnOpen3DFile().setEnabled(true);
			PrintObject newObj = new PrintObject(codeOps.getCodes(),app, _gCodes);
			
			objects.add(newObj);
			refreshPrintQueue();
			int numLayers = newObj.getNumLayers();
			
			layersSlider.setMaximum(numLayers);
			layersSlider.setValue(numLayers);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return;

		}

	}
	
	private void switchPrintObject(PrintObject _obj){
		app.loadPrintObject(_obj);
		int numLayers = _obj.getNumLayers();
		layersSlider.setMaximum(numLayers);
		layersSlider.setValue(numLayers);
		updatePrintInfo();
	}
	
	private void switchPrintObject(int _objIndex){
		switchPrintObject(objects.get(_objIndex));	
	}
	private void actionForBtnOpen3DFile(ActionEvent event) {
		new Thread() {
			public void run() {
				ThreadUtil.wait(100);
				File rawObject = FileSelectionFactory.GetFile(null,
						new StlFilter(), new GCodeFilter());
				// No file selected
				if (rawObject == null) {
					getBtnOpen3DFile().setEnabled(true);
					return;
				}
				String gCodePath = rawObject.getAbsolutePath();
				//gCodePath = gCodePath.replaceAll(".stl", ".gcode");
				if (!gCodePath.endsWith(".gcode")){
					gCodePath = gCodePath + ".gcode";
				}
				
				gCodes = new File(gCodePath);
				// Only if it is a .stl file should we slice it
				if (new StlFilter().accept(rawObject)) {
					printer.slice(rawObject, gCodes);
					
				}
				// If this is a gcode file, load in the codes
				if (new GCodeFilter().accept(rawObject)) {
					
					loadGcodeFile(gCodes);
				
					
				}

			}
		}.start();
		getBtnOpen3DFile().setEnabled(false);

	}
	public void refreshPrintQueue(){
		String [] names = new String[objects.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = objects.get(i).getName();
		}
		getList().setListData(names);
		if (names.length > 0){
			getList().setSelectedIndex(names.length -1);
		}
		
}
	
	
	public void doNotPrint() {
		isIllegal = true;
		getJButtonRunJob().setEnabled(false);
		showDangerDialog();
		getJButtonRunJob()
				.setToolTipText(
						"This G-Code is dangerous and cannot be printed!  "
								+ "Dangerous G-Codes will be shown in red in visualizer panel.  \n"
								+ "Possible causes: \n"
								+ "- Print is ouside build volume");

	}

	public void warnPrint() {
		isWarn = true;
		showWarningDialog();
		getJButtonRunJob()
				.setToolTipText(
						"This G-Code may have some issues, you can still continue with the print,"
								+ "but you should probably adjust your slicing settings and try again. "
								+ "Troubled G-Codes will be shown in orange in visualizer panel.  \n"
								+ "Possible causes: \n"
								+ "- Extrusion is too thin \n"
								+ "- Layers are too thick");

	}

	

	public void showDangerDialog() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JOptionPane
						.showMessageDialog(
								null,
								"This G-Code is dangerous and cannot be printed! \n"
										+ "Dangerous G-Codes will be shown in red in visualizer panel.\n"
										+ "Possible causes: \n"
										+ "- Print is ouside build volume",
								"Dangerous G-Code", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public void showWarningDialog() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				JOptionPane
						.showMessageDialog(
								null,
								"This G-Code may have some issues.\n"
										+ "You can still continue with the print, \n"
										+ "but you should probably adjust your slicing settings and try again. \n"
										+ "Troubled G-Codes will be shown in orange in visualizer panel.  \n"
										+ "Possible causes: \n"
										+ "- Extrusion is too thin \n"
										+ "- Layers are too thick",
								"Potential Problems",
								JOptionPane.WARNING_MESSAGE);
			}
		});
	}
	public DisplayConfigs getConfigs(){
		DisplayConfigs dC = new DisplayConfigs();
		
		dC.configure(new Vector3f(200, 0, 200),
				new Vector3f((float) printer.getSlicer().getPrintCenter()[0], 
						(float) printer.getSlicer().getPrintCenter()[1],
						0));
		dC.setFilaDia(printer.getSlicer().getFilimentDiameter());
		dC.setNozzleDia(printer.getSlicer().getNozzle_diameter());
	

	
	
	return dC;
}
	private JPanel getPanel_1() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.setLayout(new BorderLayout(0, 0));
panel.setToolTipText("Left Click + Drag to Rotate \n"
		+ "Right Click + Drag to Strafe \n"
		+ "Scroll for Zoom");
			app = new MachineSimDisplay(panel, getConfigs());
			
			app.addListener(new PrintTestListener() {

				@Override
				public void printIsIllegal() {
					doNotPrint();

				}

				@Override
				public void printIsWarn() {
					warnPrint();

				}

			});
			panel.add(getSliderLayer(), BorderLayout.EAST);
			panel.add(getPanel_5_1(), BorderLayout.SOUTH);
			app.start();
			new AppSettings(true);

			// settings.setWidth(640);
			// settings.setHeight(480);
			// app.setSettings(settings);

			// Dimension dim = new Dimension(640, 480);

			// ctx.getCanvas().setPreferredSize(dim);

		}
		return panel;
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getPanel_1_1());
			splitPane.setRightComponent(getPanel_2());
		}
		return splitPane;
	}

	private JPanel getPanel_1_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setLayout(new MigLayout("", "[grow]", "[grow]"));
			panel_1.add(getSplitPane_1(), "cell 0 0,grow");
		}
		return panel_1;
	}

	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			panel_2.setLayout(new BorderLayout(0, 0));
			panel_2.add(getPanel_1(), BorderLayout.CENTER);
		}
		return panel_2;
	}

	private JSlider getSliderLayer() {
		if (layersSlider == null) {
			layersSlider = new JSlider();
			layersSlider.setValue(0);
			layersSlider.setMaximum(0);
			layersSlider.setMajorTickSpacing(1);

			layersSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					
						app.setLayersToShow(layersSlider.getValue(), objToDisplay());
						updatePrintInfo();
					
					
				}
			});
			layersSlider.setSnapToTicks(true);
			layersSlider.setOrientation(SwingConstants.VERTICAL);
		}
		return layersSlider;
	}

	/**
	 * Instantiate, layout, and return the "topButtonsPanel" JPanel.
	 * @return topButtonsPanel The newly setup/created "topButtonsPanel" JPanel
	 */
	private JPanel getTopButtonsPanel() {
		if (topButtonsPanel == null) {
			topButtonsPanel = new JPanel();
			topButtonsPanel.setLayout(new MigLayout("", "[][][][9.00,center][grow][10.00][grow][10.00][grow][][grow][][grow][]", "[center]"));
			topButtonsPanel.add(getBtnOpen3DFile(), "cell 0 0,grow");
			topButtonsPanel.add(getJButtonRunJob(), "cell 1 0,grow");
			topButtonsPanel.add(getBtnPausePrint(), "cell 2 0");
		}
		return topButtonsPanel;
	}

	private JPanel getPanel_4() {
		if (panel_4 == null) {
			panel_4 = new JPanel();
			panel_4.setLayout(new MigLayout("", "[][][][][]", "[]"));
			panel_4.add(getChckbxShowGoodLines(), "flowy,cell 0 0");
			panel_4.add(getChckbxShowTroubledLines(), "flowy,cell 1 0");
			panel_4.add(getChckbxShowDangerousLines(), "flowy,cell 2 0");
			panel_4.add(getChckbxShowNonextrudeLines(), "flowy,cell 3 0");
			panel_4.add(getLblNumgood(), "cell 0 0,growx");
			panel_4.add(getLblNumtroubled(), "cell 1 0,growx");
			panel_4.add(getLblNumdanger(), "cell 2 0,growx");
			panel_4.add(getLblNumnonextrude(), "cell 3 0,growx");
		}
		return panel_4;
	}

	private JCheckBox getChckbxShowGoodLines() {
		if (chckbxShowGoodLines == null) {
			chckbxShowGoodLines = new JCheckBox("Show Good Lines");
			chckbxShowGoodLines.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					app.setShowGood(chckbxShowGoodLines.isSelected());
				}
			});
			chckbxShowGoodLines.setSelected(true);
		}
		return chckbxShowGoodLines;
	}

	private JCheckBox getChckbxShowTroubledLines() {
		if (chckbxShowTroubledLines == null) {
			chckbxShowTroubledLines = new JCheckBox("Show Troubled Lines");
			chckbxShowTroubledLines.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					app.setShowTroubled(chckbxShowTroubledLines.isSelected());
				}
			});
			chckbxShowTroubledLines.setSelected(true);
		}
		return chckbxShowTroubledLines;
	}

	private JCheckBox getChckbxShowDangerousLines() {
		if (chckbxShowDangerousLines == null) {
			chckbxShowDangerousLines = new JCheckBox("Show Dangerous Lines");
			chckbxShowDangerousLines.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					app.setShowDangerous(chckbxShowDangerousLines.isSelected());
				}
			});
			chckbxShowDangerousLines.setSelected(true);
		}
		return chckbxShowDangerousLines;
	}

	private JCheckBox getChckbxShowNonextrudeLines() {
		if (chckbxShowNonextrudeLines == null) {
			chckbxShowNonextrudeLines = new JCheckBox("Show Non-Extrude Lines");
			chckbxShowNonextrudeLines.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					app.setShowNonExtrude(chckbxShowNonextrudeLines
							.isSelected());
				}
			});
			chckbxShowNonextrudeLines.setSelected(true);
		}
		return chckbxShowNonextrudeLines;
	}

	private JTextField getTfLayerShown() {
		if (tfLayerShown == null) {
			tfLayerShown = new JTextField();
			tfLayerShown.setHorizontalAlignment(SwingConstants.RIGHT);
			tfLayerShown.setEditable(false);
			tfLayerShown.setColumns(10);
		}
		return tfLayerShown;
	}

	private JPanel getPanel_5_1() {
		if (panel_5 == null) {
			panel_5 = new JPanel();
			panel_5.setLayout(new BorderLayout(0, 0));
			panel_5.add(getTfLayerShown());
			panel_5.add(getChckbxShowAxes(), BorderLayout.WEST);
		}
		return panel_5;
	}

	private JCheckBox getChckbxShowAxes() {
		if (chckbxShowAxes == null) {
			chckbxShowAxes = new JCheckBox("Show Axes");
			chckbxShowAxes.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					app.setShowAxes(chckbxShowAxes.isSelected());
				}
			});
			chckbxShowAxes.setSelected(true);
		}
		return chckbxShowAxes;
	}

	private JButton getBtnEmergencyStop() {
		if (btnEmergencyStop == null) {
			btnEmergencyStop = new JButton("Emergency Stop");
			btnEmergencyStop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					printer.emergencyStop();
				}
			});
			btnEmergencyStop.setBackground(Color.RED);
			btnEmergencyStop.setForeground(Color.BLACK);
			btnEmergencyStop.setFont(new Font("Tahoma", Font.PLAIN, 40));
		}
		return btnEmergencyStop;
	}

	private JPanel getPanel_6() {
		if (panel_6 == null) {
			panel_6 = new JPanel();
			panel_6.setLayout(new MigLayout("", "[][][][grow][]", "[]"));
			panel_6.add(getBtnHomePrinter(), "cell 0 0,growy");
			panel_6.add(getLblTempSetpoint(), "cell 1 0");
			panel_6.add(getSpinnerTemp(), "cell 2 0");
		}
		return panel_6;
	}

	private JButton getBtnHomePrinter() {
		if (btnHomePrinter == null) {
			btnHomePrinter = new JButton("Home Printer");
			btnHomePrinter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					printer.homeAllLinks();
					loadSetpoint(25);
					
				}
			});
		}
		return btnHomePrinter;
	}

	@Override
	public void sliceStatus(SliceStatusData ssd) {
		// TODO Auto-generated method stub
		textPaneLog.setText(textPaneLog.getText() + "\n" + ssd.toString());
		switch (ssd.getCurrentSlicerState()) {
		case ERROR:
			break;
		case SLICING:
			Log.warning(ssd.toString());
			break;
		case SUCCESS:
			Log.warning(ssd.toString());
			System.out.println(gCodes.getAbsolutePath());
			// Once the slicing is done, load the gcode file from the slice
			if (gCodes != null && gCodes.isFile() && gCodes.canRead()) {
				try {
					
					loadGcodeFile(gCodes);
					
					
					
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case WARNING_DONE:
			break;
		case WARNING_SLICING:
			break;
		default:
			break;

		}
	}

	@Override
	public void printStatus(PrinterStatus psl) {
		// Ignore the moving packets, too much junk data
		if(psl.getDriverState() != PrinterState.MOVING)
			textPaneLog.setText(textPaneLog.getText() + "\n" + psl.toString());
		switch (psl.getDriverState()) {
		case ERROR:
			break;
		case NOT_READY:
			break;
		case PRINTING:
			// This is the currently loaded gcode
			//Log.warning(psl.toString());
			break;
		case READY:
			break;
		case SUCCESS:
			// Print complete status
			Log.warning(psl.toString());
			printComplete();
			break;
		case WARNING_DONE:
			break;
		case WARNING_PRINTING:
			// Warn about unhandled gcodes
			Log.warning(psl.toString());
			break;
		case MOVING:
			getProgressBar().setValue((int) psl.getTempreture());
			//TODO: need to get temperature setpoint   getSpinnerTemp().setValue((int) printer.)
			getProgressBar().setToolTipText("Current Temp: " + psl.getTempreture());
			//If the temperature is close to the setpoint, make the color of the bar green to indicate good temperature at a glance
			if (Math.abs(getProgressBar().getValue() - (int) getSpinnerTemp().getValue())< 3){
				getProgressBar().setForeground(Color.GREEN);
			}
			else{
				getProgressBar().setForeground(Color.ORANGE);
			}
			
			//Log.warning(psl.toString());
			break;
		default:
			break;

		}
	}
	
	private JSplitPane getSplitPane_1() {
		if (splitPane_1 == null) {
			splitPane_1 = new JSplitPane();
			splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane_1.setRightComponent(getPanel_7());
			splitPane_1.setLeftComponent(getPanel_8());
		}
		return splitPane_1;
	}
	private JPanel getPanel_7() {
		if (panel_7 == null) {
			panel_7 = new JPanel();
			panel_7.setLayout(new BorderLayout(0, 0));
			panel_7.add(getLblPrintLog(), BorderLayout.NORTH);
			panel_7.add(getScrollPane(), BorderLayout.CENTER);
		}
		return panel_7;
	}
	private JPanel getPanel_8() {
		if (panel_8 == null) {
			panel_8 = new JPanel();
			panel_8.setLayout(new BorderLayout(0, 0));
			panel_8.add(getLblPrintQueue(), BorderLayout.NORTH);
			panel_8.add(getList(), BorderLayout.CENTER);
		}
		return panel_8;
	}
	private JLabel getLblPrintQueue() {
		if (lblPrintQueue == null) {
			lblPrintQueue = new JLabel("Print Queue:");
		}
		return lblPrintQueue;
	}
	private JLabel getLblPrintLog() {
		if (lblPrintLog == null) {
			lblPrintLog = new JLabel("Print Log:");
		}
		return lblPrintLog;
	}
	private JTextPane getTextPaneLog() {
		if (textPaneLog == null) {
			textPaneLog = new JTextPane();			
			textPaneLog.setAutoscrolls(true);
			
		}
		return textPaneLog;
	}
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setOrientation(SwingConstants.VERTICAL);
			progressBar.setFont(new Font("Tahoma", Font.PLAIN, 16));			
			progressBar.setMaximum(300);
			progressBar.setStringPainted(true);
			progressBar.setValue(25);
			progressBar.setString("Hot End Temp: ");
			progressBar.setForeground(Color.ORANGE);
		}
		return progressBar;
	}
	private boolean isInternalUpdate = false;
	private JLabel lblNumgood;
	private JLabel lblNumtroubled;
	private JLabel lblNumdanger;
	private JLabel lblNumnonextrude;
	
	
	
	/**
	 * Use this method to set the setpoint without causing infinity forever updates.
	 * @param _setpoint
	 */
	private void loadSetpoint(int _setpoint){
		isInternalUpdate = true;
		getSpinnerTemp().setValue(_setpoint);
		isInternalUpdate =false;
	}
	
	private JSpinner getSpinnerTemp() {
		if (spinnerTemp == null) {
			spinnerTemp = new JSpinner();
			spinnerTemp.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					//TODO: if this value is manually changed by the user (not automatically changed) the print temperature should be changed
					if (!isInternalUpdate){
						printer.setExtrusionTempreture((double) spinnerTemp.getValue());
					}		
				}
			});
			spinnerTemp.setModel(new SpinnerNumberModel(0, 0, 275, 1));
		}
		return spinnerTemp;
	}
	private JLabel getLblTempSetpoint() {
		if (lblTempSetpoint == null) {
			lblTempSetpoint = new JLabel("Temp Setpoint:");
		}
		return lblTempSetpoint;
	}
	private JButton getBtnPausePrint() {
		if (btnPausePrint == null) {
			btnPausePrint = new JButton("Pause Print");
			btnPausePrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					flipPauseState();
				}
			});
		}
		return btnPausePrint;
	}
	
	
	private void flipPauseState(){
		if (isPaused){
			resumePrint();
		}
		else{
			pausePrint();
		}
	}
	
	/**
	 * This starts the current print job and configures the GUI
	 */
	private void startPrint(){
		
		new Thread() {
			public void run() {
				getBtnOpen3DFile().setEnabled(false);
				//jButtonRunJob.setEnabled(false);
				isPaused = false;
				getBtnPausePrint().setText("Pause Job");
				getBtnPausePrint().setEnabled(true);
				getJButtonRunJob().setText("Cancel Job");
				try {
					printer.cancelPrint();
					printer.print(new FileInputStream(gCodes));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		
		}
	
	/**
	 * This method pauses a print and configures the GUI
	 */
	private void pausePrint(){
		//TODO: Need to be able to get the paused state of the printer
		isPaused = true;
		getBtnPausePrint().setText("Resume Job");
		printer.setPausePrintState(true);
		
	}
	
	
	/**
	 * This method resumes a paused print and configures the GUI
	 */
	private void resumePrint(){
		//TODO: Need to be able to get the paused state of the printer
		isPaused = false;
		getBtnPausePrint().setText("Pause Job");
		printer.setPausePrintState(false);
	}
	/**
	 * This method is called at the completion of a print job, it cleans up and resets the GUI for the next print
	 */
	private void printComplete(){
		isPaused = false;
		getBtnPausePrint().setText("Pause Job");
		getBtnPausePrint().setEnabled(false);
		getJButtonRunJob().setText("Run Job");
	}
	private JList<String> getList() {
		if (list == null) {
			list = new JList();
			list.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getClickCount() == 2){						
						loadGcodeFile(objects.get(list.getSelectedIndex()).getCodeFile());
					}
				}
			});
			list.setModel(new AbstractListModel() {
				String[] values = new String[] {"Click \"Open 3D File\"", "to load a file."};
				public int getSize() {
					return values.length;
				}
				public Object getElementAt(int index) {
					return values[index];
				}
			});
			list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					if (arg0.getValueIsAdjusting() == false){
						if (list.isSelectionEmpty()){
							list.setSelectedIndex(0);
						}
						//gCodes = files.get(list.getSelectedIndex());
						//loadGcodeFile();
						switchPrintObject(list.getSelectedIndex());
					}
					
				}
			});
		}
		return list;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextPaneLog());
		}
		return scrollPane;
	}
	private JLabel getLblNumgood() {
		if (lblNumgood == null) {
			lblNumgood = new JLabel("0");
			lblNumgood.setHorizontalAlignment(SwingConstants.CENTER);
			lblNumgood.setToolTipText("This is the number of good g-codes.");
		}
		return lblNumgood;
	}
	private JLabel getLblNumtroubled() {
		if (lblNumtroubled == null) {
			lblNumtroubled = new JLabel("0");
			lblNumtroubled.setHorizontalAlignment(SwingConstants.CENTER);
			lblNumtroubled.setToolTipText("This is the number of troubled g-codes.");
		}
		return lblNumtroubled;
	}
	private JLabel getLblNumdanger() {
		if (lblNumdanger == null) {
			lblNumdanger = new JLabel("0");
			lblNumdanger.setHorizontalAlignment(SwingConstants.CENTER);
			lblNumdanger.setToolTipText("This is the number of dangerous/unprintable g-codes.");
		}
		return lblNumdanger;
	}
	private JLabel getLblNumnonextrude() {
		if (lblNumnonextrude == null) {
			lblNumnonextrude = new JLabel("0");
			lblNumnonextrude.setHorizontalAlignment(SwingConstants.CENTER);
			lblNumnonextrude.setToolTipText("This is the number of  non-extrusion g-codes.");
		}
		return lblNumnonextrude;
	}
}
