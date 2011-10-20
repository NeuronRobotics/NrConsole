package com.neuronrobotics.nrconsole.plugin.DyIO.hexapod;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIORegestry;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class HexapodTestStandAlone {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			BowlerAbstractConnection con=null;
			try{
				con = ConnectionDialog.promptConnection();
				//con = new SerialConnection("COM33");
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (con == null){
				System.exit(1);
			}
			JFrame frame = new JFrame();
			frame.setTitle("Hexapod Configuration");
			HexapodConfigPanel panel = new HexapodConfigPanel();
			DyIORegestry.setConnection(con);
			//DyIORegestry.get().enableDebug();
			panel.setConnection(con);
			frame.add(panel);
			frame.setLocationRelativeTo(null); 
			frame.pack();
			frame.setVisible(true);
			while(con.isConnected()){
				if(!frame.isShowing()) {
					//System.out.println("Window closed");
					con.disconnect();
					System.exit(0);
				}
				ThreadUtil.wait(100);
			}
			//System.out.println("Connection disconnected");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.exit(0);
	}
}
