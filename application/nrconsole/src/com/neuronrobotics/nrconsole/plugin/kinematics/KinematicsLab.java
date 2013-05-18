package com.neuronrobotics.nrconsole.plugin.kinematics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.io.IOUtils;


import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.XmlFilter;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.gui.DHKinematicsViewer;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.IPIDControl;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

import net.miginfocom.swing.MigLayout;

public class KinematicsLab extends JPanel {
	
	private GenericPIDDevice pidDevice;
	private DyIO dyio=null;
	private JButton show3d = new JButton("Show Display");
	private JButton openXml = new JButton("Open...");
	private JButton newXml = new JButton("New");
	private File config = new File(".");
	
	private DHParameterKinematics model ;
	private DHKinematicsViewer viewer3d;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3389413583179284688L;
	public KinematicsLab(){
		setName("Kinematics Lab");
		setLayout(new MigLayout());
		
		show3d.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(viewer3d == null)
					viewer3d = new DHKinematicsViewer(model);
				viewer3d.getFrame().setVisible(true);
			}
		});
		
		openXml.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				config = getFile(config);
				if(config == null){
					config = new File(".");
					return;
				}
				startLab();
			}
		});
		
		newXml.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				config = getFile(config);
				if(config == null){
					config = new File(".");
					return;
				}
				try {
					OutputStream out = new FileOutputStream(config);
					InputStream sample = KinematicsLab.class.getResourceAsStream("SimpleDH.xml");
					byte[] buf = new byte[1000];
					int len = 0;
					while ((len = sample.read(buf)) >= 0)
					{
						out.write(buf, 0, len);
					}
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				startLab();
			}
		});
		
		add(openXml,"wrap");
		add(newXml,"wrap");
	}
	
	private File getFile(File file) {
		return FileSelectionFactory.GetFile(file, new XmlFilter());
	}
	
	private void startLab(){
		removeAll();
		if(dyio == null){
			try {
				model = new DHParameterKinematics(pidDevice,new FileInputStream(config),new FileInputStream(config));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				model = new DHParameterKinematics(dyio,new FileInputStream(config),new FileInputStream(config));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		add(show3d,"wrap");
		invalidate();
	}
	
	public void setDevice(GenericPIDDevice dev){
		pidDevice=dev;
		
	}
	
	public void setDevice(DyIO d){
		setDevice((GenericPIDDevice)d.getPIDChannel(0).getPid());
		dyio=d;
	}
	
	
	public static void main(String [] args){
		KinematicsLab lab = new KinematicsLab();
		lab.setDevice(new VirtualGenericPIDDevice(10000));
		
        JFrame jf = new JFrame();
        jf.add(lab);
        jf.pack();
        jf.setVisible(true);
		
	}
	
}
