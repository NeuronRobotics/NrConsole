package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
import com.neuronrobotics.replicator.driver.ServoStockGCodeParser;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.replicator.driver.SliceStatusData;
import com.neuronrobotics.replicator.driver.SliceStatusData.SlicerState;
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JToolBar;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;

public class JobExecPanel extends JPanel implements PrinterStatusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 12345L;
	private BowlerBoardDevice delt;
	private NRPrinter printer;
	File gCodes = null;
	FileInputStream gCodeStream;
	double currpos = 0;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JTextField jTextField0;
	private JTextField jTextField1;
	private JTextField jTextField2;
	private JTextField jTextField3;
	private JTextField jTextField4;
	private JButton jButton0;
	private JButton jButtonOpenGCode;
	private JButton jButtonRunJob;
	private TempGraphs grfHotendTemp;
	private TempGraphs grfBedTemp;
	private int channelHotEnd = -1;
	private int channelBed = -1;
	private GCodeLoader codeOps;
	private JPanel panel;
	private JSplitPane splitPane;
	private JPanel panel_1;
	private JPanel panel_2;
	MachineSimDisplay app;
	private JSlider sliderLayer;
	private boolean isGoodFile;
	private boolean isWarn = false;
	private boolean isIllegal = false;
	private JPanel panel_3;
	private JPanel panel_4;
	private JCheckBox chckbxShowGoodLines;
	private JCheckBox chckbxShowTroubledLines;
	private JCheckBox chckbxShowDangerousLines;
	private JCheckBox chckbxShowNonextrudeLines;
	private JTextField tfLayerShown;
	private JPanel panel_5;
	private JCheckBox chckbxShowAxes;

	public JobExecPanel() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				initComponents();
			}
		});

	}

	public void setDevices(BowlerBoardDevice delt, NRPrinter printer) {
		this.delt = delt;
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
		Updater up = new Updater();
		up.start();
		printer.addPrinterStatusListener(this);
	}

	private void initComponents() {
		getPanel_1();// initialize the 3d engine
		setLayout(new MigLayout("", "[157px,grow]",
				"[grow][grow][][grow][grow][][]"));
		add(getPanel_3(), "cell 0 0,grow");
		add(getPanel_6(), "cell 0 1,grow");
		add(getSplitPane(), "cell 0 3,grow");

		setMinimumSize(new Dimension(693, 476));
		add(getPanel_4(), "cell 0 4,grow");
		add(getBtnEmergencyStop(), "cell 0 5,growx");

	}

	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("Update Robot");
		}
		return jButton0;
	}

	private TempGraphs getGrfHotendTemp() {
		if (grfHotendTemp == null) {
			grfHotendTemp = new TempGraphs(0, "Hotend Temp");

		}
		return grfHotendTemp;
	}

	private TempGraphs getGrfBedTemp() {
		if (grfBedTemp == null) {
			grfBedTemp = new TempGraphs(1, "Bed Temp");

		}
		return grfBedTemp;
	}

	private JButton getJButtonRunJob() {
		if (jButtonRunJob == null) {
			jButtonRunJob = new JButton();
			jButtonRunJob.addMouseListener(new MouseAdapter() {
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
			jButtonRunJob.setText("Run Job");
			jButtonRunJob.setEnabled(false);
			jButtonRunJob.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					jButtonRunJobActionActionPerformed(event);
				}
			});
		}
		return jButtonRunJob;
	}

	private JButton getJButtonOpenGCode() {
		if (jButtonOpenGCode == null) {
			jButtonOpenGCode = new JButton();
			jButtonOpenGCode.setText("Open 3d File");
			jButtonOpenGCode.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					jButtonOpenGCodeActionActionPerformed(event);
				}
			});
		}
		return jButtonOpenGCode;
	}

	public String fileName = "None";
	private JButton btnEmergencyStop;
	private JPanel panel_6;
	private JButton btnHomePrinter;

	public void updatePrintInfo() {
		getTfLayerShown().setText(
				"(File: " + fileName + ") (# Layers: "
						+ sliderLayer.getMaximum() + ") (# Layers Shown: "
						+ sliderLayer.getValue() + ")");
	}

	private void loadGcodeFile() {
		try {
			isIllegal = false;
			isWarn = false;
			fileName = gCodes.getName();
			updatePrintInfo();
			app.loadingGCode();
			gCodeStream = new FileInputStream(gCodes);
			codeOps = new GCodeLoader();

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
			getJButtonOpenGCode().setEnabled(true);
			int numLayers = app.loadGCode(codeOps.getCodes());
			sliderLayer.setMaximum(numLayers);
			sliderLayer.setValue(numLayers);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return;

		}

	}

	private JTextField getJTextField4() {
		if (jTextField4 == null) {
			jTextField4 = new JTextField();
			jTextField4.setText("0");
			jTextField4.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					jTextField4ActionActionPerformed(event);
				}
			});
		}
		return jTextField4;
	}

	private JTextField getJTextField3() {
		if (jTextField3 == null) {
			jTextField3 = new JTextField();
			jTextField3.setText("0");
			jTextField3.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					jTextField3ActionActionPerformed(event);
				}
			});
		}
		return jTextField3;
	}

	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField();
			jTextField2.setText("0");
			jTextField2.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					jTextField2ActionActionPerformed(event);
				}
			});
		}
		return jTextField2;
	}

	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setText("0");
			jTextField1.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					jTextField1ActionActionPerformed(event);
				}
			});
		}
		return jTextField1;
	}

	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
			jTextField0.setText("0");
			jTextField0.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					jTextField0ActionActionPerformed(event);
				}
			});
		}
		return jTextField0;
	}

	private JLabel getJLabel4() {
		if (jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Temp");
		}
		return jLabel4;
	}

	private JLabel getJLabel3() {
		if (jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Extrude");
		}
		return jLabel3;
	}

	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Z");
		}
		return jLabel2;
	}

	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Y");
		}
		return jLabel1;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("X");
		}
		return jLabel0;
	}

	private void jTextField4ActionActionPerformed(ActionEvent event) {
		// set temp

	}

	private void jTextField3ActionActionPerformed(ActionEvent event) {
		// set extrude

	}

	private void jTextField2ActionActionPerformed(ActionEvent event) {
		// set z
	}

	private void jTextField1ActionActionPerformed(ActionEvent event) {
		// set y
	}

	private void jTextField0ActionActionPerformed(ActionEvent event) {
		// ServoStockGCodeParser operator = new ServoStockGCodeParser(printer);

	}

	private void jButtonRunJobActionActionPerformed(ActionEvent event) {
		new Thread() {
			public void run() {
				jButtonOpenGCode.setEnabled(false);
				jButtonRunJob.setEnabled(false);
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

	private void jButtonOpenGCodeActionActionPerformed(ActionEvent event) {
		new Thread() {
			public void run() {
				ThreadUtil.wait(100);
				File rawObject = FileSelectionFactory.GetFile(null,
						new StlFilter(), new GCodeFilter());
				// No file selected
				if (rawObject == null) {
					getJButtonOpenGCode().setEnabled(true);
					return;
				}
				String gCodePath = rawObject.getAbsolutePath();
				gCodePath = gCodePath.replaceAll(".stl", ".gcode");
				gCodes = new File(gCodePath);
				// Only if it is a .stl file should we slice it
				if (new StlFilter().accept(rawObject)) {
					printer.slice(rawObject, gCodes);
				}
				// If this is a gcode file, load in the codes
				if (new GCodeFilter().accept(rawObject)) {
					loadGcodeFile();
				}

			}
		}.start();
		getJButtonOpenGCode().setEnabled(false);

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

	private class Updater extends Thread {

		public void run() {
			while (true) {
				try {
					Thread.sleep(500);

					getGrfHotendTemp().addEvent(getHotEndSetpoint(),
							getHotendTemp());
					getGrfBedTemp().addEvent(getBedSetpoint(), getBedTemp());

				} catch (InterruptedException e) {
				}
				// graphVals();

			}
		}
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

	private int getHotendTemp() {

		return delt.GetPIDPosition(channelHotEnd);

	}

	private int getHotEndSetpoint() {
		return delt.getPIDChannel(channelHotEnd).getCachedTargetValue();
	}

	private int getBedTemp() {
		return delt.GetPIDPosition(2);

	}

	private int getBedSetpoint() {
		return delt.getPIDChannel(2).getCachedTargetValue();
	}

	private JPanel getPanel_1() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.setLayout(new BorderLayout(0, 0));

			app = new MachineSimDisplay(panel);
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
			AppSettings settings = new AppSettings(true);

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
			panel_1.setLayout(new MigLayout("", "[grow]", "[grow][grow][]"));
			panel_1.add(getGrfHotendTemp(), "cell 0 0,grow");
			panel_1.add(getGrfBedTemp(), "cell 0 1,grow");
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
		if (sliderLayer == null) {
			sliderLayer = new JSlider();
			sliderLayer.setValue(0);
			sliderLayer.setMaximum(0);
			sliderLayer.setMajorTickSpacing(1);

			sliderLayer.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if (!sliderLayer.getValueIsAdjusting()) {
						app.setLayersToShow(sliderLayer.getValue());
					}
					updatePrintInfo();
				}
			});
			sliderLayer.setSnapToTicks(true);
			sliderLayer.setOrientation(SwingConstants.VERTICAL);
		}
		return sliderLayer;
	}

	private JPanel getPanel_3() {
		if (panel_3 == null) {
			panel_3 = new JPanel();
			panel_3.setLayout(new MigLayout(
					"",
					"[][][9.00,center][grow][10.00][grow][10.00][grow][][grow][][grow][]",
					"[center][]"));
			panel_3.add(getJButtonOpenGCode(), "cell 0 0,grow");
			panel_3.add(getJButtonRunJob(), "cell 1 0,grow");
			// panel_3.add(getJLabel0(), "cell 2 0,alignx right");
			// panel_3.add(getJTextField0(), "cell 3 0,growx");
			// panel_3.add(getJLabel1(), "cell 4 0,alignx right,growy");
			// panel_3.add(getJTextField1(), "cell 5 0,growx");
			// panel_3.add(getJLabel2(), "cell 6 0,alignx right");
			// panel_3.add(getJTextField2(), "cell 7 0,growx");
			// panel_3.add(getJLabel3(), "cell 8 0");
			// panel_3.add(getJTextField4(), "cell 9 0,growx");
			// panel_3.add(getJLabel4(), "cell 10 0");
			// panel_3.add(getJTextField3(), "cell 11 0,growx");
			// panel_3.add(getJButton0(), "cell 12 0,growy");
		}
		return panel_3;
	}

	private JPanel getPanel_4() {
		if (panel_4 == null) {
			panel_4 = new JPanel();
			panel_4.setLayout(new MigLayout("", "[][][][][]", "[][]"));
			panel_4.add(getChckbxShowGoodLines(), "cell 0 0");
			panel_4.add(getChckbxShowTroubledLines(), "cell 1 0");
			panel_4.add(getChckbxShowDangerousLines(), "cell 2 0");
			panel_4.add(getChckbxShowNonextrudeLines(), "cell 3 0");
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
			panel_6.setLayout(new MigLayout("", "[]", "[][]"));
			panel_6.add(getBtnHomePrinter(), "cell 0 0");
		}
		return panel_6;
	}

	private JButton getBtnHomePrinter() {
		if (btnHomePrinter == null) {
			btnHomePrinter = new JButton("Home Printer");
			btnHomePrinter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					printer.homeAllLinks();
				}
			});
		}
		return btnHomePrinter;
	}

	@Override
	public void sliceStatus(SliceStatusData ssd) {
		// TODO Auto-generated method stub
		
		switch (ssd.getCurrentSlicerState()) {
		case ERROR:
			break;
		case SLICING:
			Log.warning(ssd.toString());
			break;
		case SUCCESS:
			Log.warning(ssd.toString());
			// Once the slicing is done, load the gcode file from the slice
			if (gCodes != null && gCodes.isFile() && gCodes.canRead()) {
				try {
					loadGcodeFile();
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
		// TODO Auto-generated method stub
		Log.warning(psl.toString());
		switch (psl.getDriverState()) {
		case ERROR:
			break;
		case NOT_READY:
			break;
		case PRINTING:
			// The live plot should update here
			
			break;
		case READY:
			break;
		case SUCCESS:
			// Print complete status
			printer.cancelPrint();
			jButtonRunJob.setEnabled(true);
			jButtonOpenGCode.setEnabled(true);
			break;
		case WARNING_DONE:
			break;
		case WARNING_PRINTING:
			break;
		default:
			break;

		}
	}
}
