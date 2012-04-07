package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.neuronrobotics.sdk.config.SDKBuildInfo;

import net.miginfocom.swing.MigLayout;

public class GettingStartedPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7822822213729180361L;
	private JButton onLineDocs = new JButton("Open");
	private JButton javaDocs = new JButton("Open");
	
	private String wiki = "http://wiki.neuronrobotics.com/Getting_started_main_page";
	private String jDoc1 = "http://downloads.neuronrobotics.com/nrdk/";
	private String jDoc2 = "/java/docs/api/index.html";
	private String jDoc;
	private Desktop desktop = null;
	public GettingStartedPanel (){
		setName("Getting Started");
		setLayout(new MigLayout());
		
		jDoc = jDoc1+SDKBuildInfo.getVersion()+jDoc2;
		if (Desktop.isDesktopSupported()) {
	        setDesktop(Desktop.getDesktop());
		}else{
			System.err.println("Desktop not supported");
		}
		
		add(new JLabel("Getting Started Documents:"),"wrap");
		add(new DocWidget("Online Getting Started Overview", wiki),"wrap");
		add(new DocWidget("Online DyIO Details", "http://wiki.neuronrobotics.com/DyIO"),"wrap");
		add(new DocWidget("Online 'JavaDoc' Programming guide", jDoc),"wrap");
		
	}
	public Desktop getDesktop() {
		return desktop;
	}
	public void setDesktop(Desktop desktop) {
		this.desktop = desktop;
	}
	private class DocWidget extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = -174323483559948677L;
		URI uri;
		private JButton button = new JButton("Open");
		public DocWidget(String description, String location){
			setLayout(new MigLayout());
			try {
				uri = new URI(location);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			add(button );
			add(new JLabel(description),"wrap");
			
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
			           try {
						getDesktop().browse(uri);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}
	}
}
