package com.neuronrobotics.nrconsole.plugin.DyIO.GoogleChat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jivesoftware.smack.XMPPException;

import com.neuronrobotics.application.xmpp.DyIOConversationFactory;
import com.neuronrobotics.application.xmpp.GoogleChat.GoogleChatEngine;
import com.neuronrobotics.application.xmpp.GoogleChat.IChatLog;
import com.neuronrobotics.nrconsole.plugin.DyIO.GettingStartedPanel;
import com.neuronrobotics.nrconsole.util.NRConsoleDocumentationFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;

public class GoogleChatLogin extends JPanel implements IChatLog {

	/**
	 * 
	 */
	private GoogleChatLogin self = this;//used for NRConsoleDocumentationFactory.getDocumentationURL input,DyIOConversationFactory input
	private static final long serialVersionUID = 3814334384450486363L;
	private JLabel imageIcon;
	private JPasswordField pass = new JPasswordField(15);
	private JTextField user = new JTextField (15);
	private JButton connect=new JButton("Connect");
	private JButton disconnect=new JButton("Disconnect");
	private JTextArea log = new JTextArea(30, 50);
	private GoogleChatEngine eng;

	public GoogleChatLogin(){
		setName("DyIO Cloud Connect");
		setLayout(new MigLayout());
		
		InputStream stream = GoogleChatLogin.class.getResourceAsStream( "google-logo-plus.png" );
		try {
			BufferedImage image = ImageIO.read( stream );
			imageIcon = new JLabel(new ImageIcon(image));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Button to launch info page for Digital Input panel
		JButton helpButton = new JButton("Help");
		//Label for Digital Input Panel
		JLabel helpLabel = new JLabel("DyIO Cloud Connect Info");
		add(helpLabel, "split 2, span 2, align left");
		add(helpButton, "gapleft 200, wrap, align right");
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					GettingStartedPanel.openPage(NRConsoleDocumentationFactory.getDocumentationURL(self));// ------------------------------------------
				} catch (Exception exceptE) {}
			}
		});
		//Help button formating
		helpButton.setFont((helpButton.getFont()).deriveFont(8f));
		helpButton.setBackground(Color.green);
		//Digital Input Panel label formating
		helpLabel.setHorizontalTextPosition(JLabel.LEFT);
		helpLabel.setForeground(Color.GRAY);
		
		add(imageIcon,"wrap");
		
		JPanel creds = new JPanel(new MigLayout());
		creds.add(new JLabel("G+ Username:"));
		creds.add(user);
		creds.add(new JLabel("@gmail.com"),"wrap");
		creds.add(new JLabel("Password:"));
		creds.add(pass,"wrap");
		
		add(creds,"wrap");
		add(connect,"wrap");
		add(disconnect,"wrap");
		add(new JLabel("Log:"),"wrap");
		add(log);
		disconnect.setEnabled(false);
		pass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onConnect();
			}
		});
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onConnect();
			}
		});
		
		disconnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				eng.disconnect();
				connect.setEnabled(true);
				user.setEnabled(true);
				pass.setEnabled(true);
				disconnect.setEnabled(false);
			}
		});
	}
	
	private void onConnect(){
		try {
			String u = user.getText()+"@gmail.com";
			String p = new String(pass.getPassword());
			//System.out.println("Login= "+u+":"+p);
			eng = new GoogleChatEngine(	new DyIOConversationFactory(self),
										u,
										p);
			connect.setEnabled(false);
			user.setEnabled(false);
			pass.setEnabled(false);
			disconnect.setEnabled(true);
			log.setText(u+" connected...Success!\n");
		} catch (XMPPException e1) {
			Log.error("Connection failed");
			e1.printStackTrace();
		}
	}

	public boolean setConnection(BowlerAbstractConnection connection) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onLogEvent(String newText) {
		log.setText(log.getText()+"\n"+new Date()+": "+newText);
	}

}
