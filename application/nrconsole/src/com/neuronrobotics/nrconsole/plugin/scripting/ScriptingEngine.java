package com.neuronrobotics.nrconsole.plugin.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;
import org.kohsuke.github.GitHub;

import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.GroovyFilter;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.util.FileChangeWatcher;
import com.neuronrobotics.sdk.util.IFileChangeListener;
import com.neuronrobotics.sdk.util.ThreadUtil;

import net.miginfocom.swing.MigLayout;

public class ScriptingEngine extends JFXPanel implements IFileChangeListener{
	

	static ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	private static ArrayList<ScriptingEngine> engines = new ArrayList<ScriptingEngine>();
	static{
        System.setOut(new PrintStream(out));
		SwingUtilities.invokeLater(() -> {
			handlePrintUpdate();
		});
	}
	
	static void handlePrintUpdate() {

		ThreadUtil.wait(20);
		SwingUtilities.invokeLater(() -> {
			if(out.size()>0){
				for(int i=0;i<engines.size();i++){
					// If the script is running update its display
					if(engines.get(i).running){
						String current = engines.get(i).output.getText();
						current +=out.toString();
						out.reset();
						if(current.getBytes().length>2000)
							current=new String(current.substring(current.getBytes().length-1500));
						engines.get(i).output.setText(current);
						engines.get(i).output.setCaretPosition(engines.get(i).output.getDocument().getLength());
					}
				}

			}
		});
		SwingUtilities.invokeLater(() -> {
			// TODO Auto-generated method stub
			handlePrintUpdate();
		});
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private JTextArea code;
	private JButton run;

	private JLabel fileLabel =new JLabel("No File Loaded");
	private File currentFile = null;
	
	private boolean running = false;
	private JTextArea output;
	private Thread scriptRunner=null;
	private FileChangeWatcher watcher;
	//private String currentGist = "40fadfa5804eee848e62";
	private DyIO dyio;
	private PluginManager pm;
	//private GithubGistBrowser browser;
	
//	private enum interfaceType {
//		/** The STATUS. */
//		Native,
//		/** The GET. */
//		WebGist;
//		/* (non-Javadoc)
//		 * @see java.lang.Enum#toString()
//		 */
//		public String toString(){
//			switch (this){
//			case Native:
//				return "Native";
//			case WebGist:
//				return "Web Gist";
//			default:
//				return "hrrmm";
//			}
//		}
//	};
//
//	
//	interfaceType toDisplay = interfaceType.WebGist;
//	private JMenuItem nativeIdisplay;
//	private JMenuItem webgist;
	private Dimension codeDimentions = new Dimension(1168, 768);
	private JPanel controls;
	private JScrollPane outputPane;
	private String codeText="println(dyio)\n"
			+ "while(true){\n"
			+ "\tThreadUtil.wait(100)                     // Spcae out the loop\n\n"
			+ "\tlong start = System.currentTimeMillis()  //capture the starting value \n\n"
			+ "\tint value = dyio.getValue(15)            //grab the value of pin 15\n"
			+ "\tint scaled = value/4                     //scale the analog voltage to match the range of the servos\n"
			+ "\tdyio.setValue(0,scaled)                  // set the new value to the servo\n\n"
			+ "\t//Print out this loops values\n"
			+ "\tprint(\" Loop took = \"+(System.currentTimeMillis()-start))\n"
			+ "\tprint(\"ms Value= \"+value)\n"
			+ "\tprintln(\" Scaled= \"+scaled)\n"
			+ "}";
	
	private ArrayList<IScriptEventListener> listeners = new ArrayList<IScriptEventListener>();
	
	public ScriptingEngine(DyIO dyio, PluginManager pm){
		this.dyio = dyio;
		this.pm = pm;
		setName("Bowler Scripting");
		setLayout(new MigLayout());
		output = new JTextArea(20, 100);
		outputPane = new JScrollPane(output);
//        browser = new GithubGistBrowser(codeDimentions);
//        browser.setVisible(true);
//        browser.loadURL("http://neuronrobotics.github.io/Java-Code-Library/Digital-Input-Example-Simple/");
        //browser.loadURL("https://gist.github.com/madhephaestus/"+currentGist);
        //browser.setPreferredSize(new Dimension(1400,600));
        //browser.loadHTML( getHTMLFromGist(currentGist));
		
		run = new JButton("Run");
		controls = new JPanel(new MigLayout());
		controls.add(run);
		controls.add(fileLabel);
	
//		add(browser,"wrap");
		add(controls,"wrap");
		add(outputPane,"wrap");

		
		//getCode();
		setCode(codeText);
//		setCode("<script src=\"https://gist.github.com/madhephaestus/9de9e45c75a5588c4a81.js\"></script>");

		run.addActionListener(e -> {
			if(running)
				stop();
			else
				start();			
		});

	    //String ctrlSave = "CTRL Save";
	    engines.add(this);
	}
	
	private void reset(){
		running = false;
		SwingUtilities.invokeLater(() -> {
			run.setText("Run");
		});

	}
	
//	private String getHTMLFromGist(String gist){
//		return "<script src=\"https://gist.github.com/madhephaestus/"+gist+".js\"></script>";
//	}
	
	public void addIScriptEventListener(IScriptEventListener l){
		if(!listeners.contains(l))
			listeners.add(l);
	}
	
	public void removeIScriptEventListener(IScriptEventListener l){
		if(listeners.contains(l))
			listeners.remove(l);
	}

	private void stop() {
		// TODO Auto-generated method stub
		reset();
		while(scriptRunner.isAlive()){
			System.out.println("Interrupting");
			ThreadUtil.wait(10);
			try {
				scriptRunner.interrupt();
				scriptRunner.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void loadCodeFromCurrentGist(String currentGist) throws IOException, InterruptedException{
		GitHub github = GitHub.connectAnonymously();
		System.out.println("Loading Gist: "+currentGist);
		GHGist gist = github.getGist(currentGist);
		Map<String, GHGistFile> files = gist.getFiles();
		for (Entry<String, GHGistFile> entry : files.entrySet()) { 
			if(entry.getKey().endsWith(".java") || entry.getKey().endsWith(".groovy")){
				GHGistFile ghfile = entry.getValue();	
				System.out.println("Key = " + entry.getKey());
				//SwingUtilities.invokeLater(() -> {
					setCode(ghfile.getContent());
            		fileLabel.setText(entry.getKey().toString());
            		if(currentFile==null)
            			currentFile = new File(fileLabel.getText());
            		else
            			currentFile = new File(currentFile.getPath()+File.pathSeparator+fileLabel.getText());
        		//});
				break;
			}
		}
		
	}

	private void start() {

		running = true;
		run.setText("Stop");
		scriptRunner = new Thread(){
			public void run() {
				setName("Bowler Script Runner "+fileLabel.getText());
				//try{
					output.setText("");
					CompilerConfiguration cc = new CompilerConfiguration();
	
		            cc.addCompilationCustomizers(
		                    new ImportCustomizer().
		                    addStarImports("com.neuronrobotics",
		                    		"com.neuronrobotics.sdk.dyio.peripherals",
		                    		"com.neuronrobotics.sdk.dyio",
		                    		"com.neuronrobotics.sdk.common",
		                    		"com.neuronrobotics.sdk.ui",
		                    		"com.neuronrobotics.sdk.util"
		                    		).addStaticStars("com.neuronrobotics.sdk.util.ThreadUtil")
		                    );
		        	
		            Binding binding = new Binding();

	
		            binding.setVariable("dyio", dyio);
		            binding.setVariable("PluginManager", pm);
		            GroovyShell shell = new GroovyShell(getClass().getClassLoader(),
		            		binding, cc);
		            System.out.println(getCode()+"\n\nStart\n\n");
		            Script script = shell.parse(getCode());
		            try{
			            Object obj = script.run();
			            for(IScriptEventListener l:listeners){
			            	l.onGroovyScriptFinished(shell, script, obj);
			            }
			            SwingUtilities.invokeLater(() -> {
		            		output.append("\nScript Completed\n");
		            		output.setCaretPosition(output.getDocument().getLength());
		        		});
			            reset();
			            
		            }catch(Exception ex){
		            	SwingUtilities.invokeLater(() -> {
		            		if(!ex.getMessage().contains("sleep interrupted")){
				            	StringWriter sw = new StringWriter();
				            	PrintWriter pw = new PrintWriter(sw);
				            	ex.printStackTrace(pw);
			        			output.append("\n"+sw+"\n");
			        			
		            		}else{
		            			output.append("\nScript Interupted\n");
		            		}
		            		output.setCaretPosition(output.getDocument().getLength());
		            		reset();
		        		});
		            	for(IScriptEventListener l:listeners){
			            	l.onGroovyScriptError(shell, script, ex);
			            }
		            	throw new RuntimeException(ex);
		            }
          
				
			}
		};

		scriptRunner.start();
	}


	
	private void updateFile(){

		 File last=FileSelectionFactory.GetFile(currentFile, new GroovyFilter());
		 if(last != null){
			 currentFile = last;
	            if(watcher!=null){
	            	watcher.close();
	            }
	            try {
					watcher = new FileChangeWatcher(currentFile);
		            watcher.addIFileChangeListener(this);
		            watcher.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
		
	}

	public void open() {
		
		updateFile();
		try {
			setCode(new String(Files.readAllBytes(currentFile.toPath())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	public void save() {
		// TODO Auto-generated method stub
		try
		{
		    BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
		    writer.write (getCode());
		    writer.close();
		} catch(Exception ex)
		{
		   //ex.printStackTrace();
		}
	}

	@Override
	public void onFileChange(File fileThatChanged, @SuppressWarnings("rawtypes") WatchEvent event) {
		// TODO Auto-generated method stub
		if(fileThatChanged.getAbsolutePath().contains(currentFile.getAbsolutePath())){
			System.out.println("Code in "+fileThatChanged.getAbsolutePath()+" changed");
			Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	            	try {
						setCode(new String(Files.readAllBytes(Paths.get(fileThatChanged.getAbsolutePath())), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	       });

		}else{
			//System.out.println("Othr Code in "+fileThatChanged.getAbsolutePath()+" changed");
		}
	}
	
	protected String getCode(){
		return codeText;
	}

	protected void setCode(String string) {
		codeText=string;
	}

}
