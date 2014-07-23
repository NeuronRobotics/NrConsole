package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.GCodeFilter;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.GCodeParser;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.sun.deploy.uitoolkit.impl.fx.Utils;


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
	private TempGraphs hotendTemp;
	private TempGraphs bedTemp;
	
	public JobExecPanel() {
		setLayout(new MigLayout());
		initComponents();
	}
	
	
	public void setDevices(BowlerBoardDevice delt, NRPrinter printer) {
		this.delt = delt;
		this.printer = printer;
		
	}

	private void initComponents() {
		add(getJButtonOpenGCode());
		add(getJButtonRunJob());
		
		add(getJLabel0());
		add(getJTextField0());
		add(getJLabel1());
		add(getJTextField1());
		add(getJLabel2());
		add(getJTextField2());
		add(getJLabel3());
		add(getJTextField3());
		add(getJLabel4());
		add(getJTextField4());
		add(getJButton0(),"wrap");
		add(getHotendTemp(), "span");
		add(getBedTemp(), "span" );
		setSize(358, 291);
	}

	
	private JButton getJButton0() {
		if (jButton0 == null) {
			jButton0 = new JButton();
			jButton0.setText("Update Robot");
		}
		return jButton0;
	}
	
	private TempGraphs getHotendTemp(){
		if (hotendTemp == null){
			hotendTemp = new TempGraphs(0,"Hotend Temp");
			
		}
		return hotendTemp;
	}
	private TempGraphs getBedTemp(){
		if (bedTemp == null){
			bedTemp = new TempGraphs(1,"Bed Temp");
			
		}
		return bedTemp;
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
			
		}
		getJButtonRunJob().setEnabled(true);
		
	}
	

	private JTextField getJTextField4() {
		if (jTextField4 == null) {
			jTextField4 = new JTextField();
			jTextField4.setText("tempTarget");
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
			jTextField3.setText("extrudeTarget");
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
			jTextField2.setText("ztarget");
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
			jTextField1.setText("open G-code");
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
			jTextField0.setText("print");
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
			long lastSet;
			long lastPos;
			public void run() {
				while(true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
						//graphVals();
					
				}
			}
		}
	


}