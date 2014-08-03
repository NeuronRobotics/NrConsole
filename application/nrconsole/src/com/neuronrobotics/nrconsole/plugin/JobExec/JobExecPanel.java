package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.jme3.system.AppSettings;
import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.GCodeFilter;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.GCodeParser;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
//import com.sun.deploy.uitoolkit.impl.fx.Utils;
import javax.swing.JSplitPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class JobExecPanel extends JPanel{

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
			if (link.getName().toLowerCase().contains("hotend")){
				channelHotEnd = link.getHardwareIndex();
			}
			if (link.getName().toLowerCase().contains("heat")){
				channelHotEnd = link.getHardwareIndex();
			}
			if (link.getName().toLowerCase().contains("bed")){
				channelBed = link.getHardwareIndex();
			}
		}
		Updater up = new Updater();
		up.start();
	}

	private void initComponents() {
		setLayout(new MigLayout("", "[157px,grow][][][grow][][grow][][grow][][grow][][grow][145.00,center]", "[][grow]"));
		add(getJButtonOpenGCode(), "flowx,cell 0 0,alignx center,aligny top");
		add(getJButtonRunJob(), "cell 1 0,alignx center,aligny top");
		
		add(getJLabel0(), "cell 2 0,alignx right,aligny center");
		add(getJTextField0(), "cell 3 0,growx,aligny center");
		add(getJLabel1(), "cell 4 0,alignx right,aligny center");
		add(getJTextField1(), "cell 5 0,growx,aligny center");
		add(getJLabel2(), "cell 6 0,alignx right,aligny center");
		add(getJTextField2(), "cell 7 0,growx,aligny center");
		add(getJLabel3(), "cell 8 0,alignx right,aligny center");
		add(getJTextField4(), "cell 9 0,growx");
		add(getJLabel4(), "cell 10 0,alignx right,aligny center");
		add(getJTextField3(), "cell 11 0,growx,aligny center");
		add(getJButton0(), "cell 12 0,alignx center,aligny top");
		add(getSplitPane(), "cell 0 1 13 1,grow");
		
		setMinimumSize(new Dimension(693, 476));
		
	}

	
	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("Update Robot");
		}
		return jButton0;
	}
	
	private TempGraphs getGrfHotendTemp(){
		if (grfHotendTemp == null){
			grfHotendTemp = new TempGraphs(0,"Hotend Temp");
			
			
		}
		return grfHotendTemp;
	}
	private TempGraphs getGrfBedTemp(){
		if (grfBedTemp == null){
			grfBedTemp = new TempGraphs(1,"Bed Temp");
			
		}
		return grfBedTemp;
	}
	private JButton getJButtonRunJob() {
		if (jButtonRunJob == null) {
			jButtonRunJob = new JButton();
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
			jButtonOpenGCode.setText("Open G-Code File");
			jButtonOpenGCode.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					jButtonOpenGCodeActionActionPerformed(event);
				}
			});
		}
		return jButtonOpenGCode;
	}
	private void loadGcodeFile(){
		try {
			gCodeStream = new FileInputStream(gCodes);
			codeOps = new GCodeLoader();
			try {
				System.out.println(gCodeStream.available());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			codeOps.loadCodes(gCodeStream);
			codeOps.getCodes().printOutput();
			int numLayers = app.loadGCode(codeOps.getCodes());
			sliderLayer.setMaximum(numLayers);
			sliderLayer.setValue(numLayers);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
			
		}
		getJButtonRunJob().setEnabled(true);
		
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
		//set temp
	}

	private void jTextField3ActionActionPerformed(ActionEvent event) {
		//set extrude
		
	}

	private void jTextField2ActionActionPerformed(ActionEvent event) {
		//set z
	}

	private void jTextField1ActionActionPerformed(ActionEvent event) {
		//set y
	}

	private void jTextField0ActionActionPerformed(ActionEvent event) {
		GCodeParser operator = new GCodeParser(printer);
		
	}
	private void jButtonRunJobActionActionPerformed(ActionEvent event){
		
	}
	
	private void jButtonOpenGCodeActionActionPerformed(ActionEvent event) {
		
		gCodes = FileSelectionFactory.GetFile(null, new GCodeFilter());
		
		if (gCodes != null && gCodes.isFile() && gCodes.canRead()){
			loadGcodeFile();
		}
		
		
	}
	
		private class Updater extends Thread{
			
			public void run() {
				while(true) {
					try {
						Thread.sleep(500);
						
						getGrfHotendTemp().addEvent(getHotEndSetpoint(), getHotendTemp());
						getGrfBedTemp().addEvent(getBedSetpoint(), getBedTemp());
					} catch (InterruptedException e) {
					}
						//graphVals();
					
				}
			}
		}

		
		
	private int getHotendTemp(){
	
		return delt.GetPIDPosition(channelHotEnd);
		
	}
	private int getHotEndSetpoint(){
		return delt.getPIDChannel(channelHotEnd).getCachedTargetValue();
	}
	private int getBedTemp(){
		return delt.GetPIDPosition(2);
	}
	private int getBedSetpoint(){
		return delt.getPIDChannel(2).getCachedTargetValue();
	}
	private JPanel getPanel_1() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.setLayout(new BorderLayout(0, 0));
			
			app = new MachineSimDisplay(panel);
			panel.add(getSliderLayer(), BorderLayout.EAST);
			app.start();
			AppSettings settings = new AppSettings(true);
			
			//settings.setWidth(640);
			//settings.setHeight(480);
			//app.setSettings(settings);
			
			//Dimension dim = new Dimension(640, 480);
			
			//ctx.getCanvas().setPreferredSize(dim);     
		      
		     
		     
		     
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
			sliderLayer.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
					app.setLayersToShow(sliderLayer.getValue());
					System.out.println("Mouse");
				}
			});
			sliderLayer.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					System.out.println("State");
				}
			});
			sliderLayer.setSnapToTicks(true);
			sliderLayer.setPaintTicks(true);
			sliderLayer.setOrientation(SwingConstants.VERTICAL);
		}
		return sliderLayer;
	}
}
