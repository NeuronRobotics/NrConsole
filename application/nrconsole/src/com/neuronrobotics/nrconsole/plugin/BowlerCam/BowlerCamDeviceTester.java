package com.neuronrobotics.nrconsole.plugin.BowlerCam;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.network.UDPBowlerConnection;
//import com.neuronrobotics.sdk.BowlerImaging.ImageProcessingFactory;

@SuppressWarnings("unused")
public class BowlerCamDeviceTester{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7385372910345405369L;
	private JFrame frame = new JFrame();
	private  BowlerCamPanel bcp;

	public BowlerCamDeviceTester() throws IOException, InterruptedException{
		initGui();
//		if (!ConnectionDialog.getBowlerDevice(cam)){
//			System.exit(1);
//		}
		bcp.setConnection(new UDPBowlerConnection());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new BowlerCamDeviceTester();
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private void initGui() throws IOException, InterruptedException{
		frame.setLayout(new MigLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.black);
		//frame.setLocationRelativeTo(null);
		frame.setSize(new Dimension(640,480));
		frame.setVisible(true);
		
		bcp =new BowlerCamPanel();
		frame.add(bcp);
	}


}
