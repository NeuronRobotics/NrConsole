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
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

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
import com.neuronrobotics.nrconsole.util.PrefsLoader;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.util.FileChangeWatcher;
import com.neuronrobotics.sdk.util.IFileChangeListener;
import com.neuronrobotics.sdk.util.ThreadUtil;

import net.miginfocom.swing.MigLayout;

public class ScriptingEngine extends BorderPane implements IFileChangeListener{
	

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
						final int myIndex = i;
						SwingUtilities.invokeLater(() -> {
							String current = engines.get(myIndex).output.getText();
							current +=out.toString();
							out.reset();
							if(current.getBytes().length>2000)
								current=new String(current.substring(current.getBytes().length-1500));
							final String toSet=current;
							engines.get(myIndex).output.setText(toSet);
							//engines.get(myIndex).setCaretPosition(engines.get(myIndex).getDocument().getLength());
						});
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
	
	private File currentFile = null;
	
	private boolean running = false;
	private TextArea output = new TextArea() ;
	private Thread scriptRunner=null;
	private FileChangeWatcher watcher;
	//private String currentGist = "40fadfa5804eee848e62";
	private DyIO dyio;
	private PluginManager pm;
	//private GithubGistBrowser browser;
	Label fileLabel = new Label();
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

	private Button runfx;
	
	public ScriptingEngine(DyIO dyio, PluginManager pm, File currentFile,String currentGist ) throws IOException, InterruptedException{
		this(dyio,pm);
		this.currentFile = currentFile;
		loadCodeFromGist(currentGist);
	}
	
	public ScriptingEngine(DyIO dyio, PluginManager pm, File currentFile){
		this(dyio,pm);
		loadCodeFromFile(currentFile);
	}
		
	private ScriptingEngine(DyIO dyio, PluginManager pm){
		this.dyio = dyio;
		this.pm = pm;
		runfx = new Button("Run");
		runfx.setOnAction(e -> {
			if(running)
				stop();
			else
				start();
		});



	    //String ctrlSave = "CTRL Save";
	    engines.add(this);
	    fileLabel.setOnMouseEntered(e->{
	    	SwingUtilities.invokeLater(() -> {
				ThreadUtil.wait(10);
				fileLabel.setText(currentFile.getAbsolutePath());
			});
	    });

	    fileLabel.setOnMouseExited(e->{
	    	SwingUtilities.invokeLater(() -> {
				ThreadUtil.wait(10);
				fileLabel.setText(currentFile.getName());
			});
	    });
	    fileLabel.setTextFill(Color.GREEN);
	    
	    //Set up the run controls and the code area
		// The BorderPane has the same areas laid out as the
		// BorderLayout layout manager
		setPadding(new Insets(20, 0, 20, 20));
		final FlowPane controlPane = new FlowPane();
		controlPane.setHgap(100);
		controlPane.getChildren().add(runfx);
		controlPane.getChildren().add(fileLabel);
		// put the flowpane in the top area of the BorderPane
		setTop(controlPane);
		setBottom(output);
	}
	
	private void reset(){
		running = false;
		SwingUtilities.invokeLater(() -> {
			runfx.setText("Run");
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
	public void loadCodeFromFile(File currentFile){
		setUpFile(currentFile);
		
	}
	public void loadCodeFromGist(String currentGist) throws IOException, InterruptedException{
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
            		if(currentFile==null){
            			//PrefsLoader prefs = new PrefsLoader();
            			currentFile = new File(fileLabel.getText());
            		}
            		else
            			currentFile = new File(currentFile.getPath()+File.pathSeparator+fileLabel.getText());
        		//});
				break;
			}
		}
		
	}

	private void start() {

		running = true;
		runfx.setText("Stop");
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
		            		append("\n"+currentFile+" Completed\n");
		            		//output.setCaretPosition(output.getDocument().getLength());
		        		});
			            reset();
			            
		            }catch(Exception ex){
		            	SwingUtilities.invokeLater(() -> {
		            		if(!ex.getMessage().contains("sleep interrupted")){
				            	StringWriter sw = new StringWriter();
				            	PrintWriter pw = new PrintWriter(sw);
				            	ex.printStackTrace(pw);
			        			append("\n"+currentFile+" \n"+sw+"\n");
			        			
		            		}else{
		            			append("\n"+currentFile+" Interupted\n");
		            		}
		            		//output.setCaretPosition(output.getDocument().getLength());
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
	
	private void append(String s){
		SwingUtilities.invokeLater(() -> {
			output.setText(output.getText()+s);
		});
	}

	private void setUpFile(File f){
		currentFile = f;
		SwingUtilities.invokeLater(() -> {
			fileLabel.setText(f.getName());
		});
		if (watcher != null) {
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
	
	private void updateFile(){
		 File last=FileSelectionFactory.GetFile(currentFile, new GroovyFilter());
		 if(last != null){
			 setUpFile(last);
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
						fileLabel.setTextFill(Color.RED);
						SwingUtilities.invokeLater(() -> {
							ThreadUtil.wait(750);
							fileLabel.setTextFill(Color.GREEN);
						});
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
	
	public String getCode(){
		return codeText;
	}

	private void setCode(String string) {
		String pervious = codeText;
		codeText=string;
        for(IScriptEventListener l:listeners){
        	l.onGroovyScriptChanged(pervious, string);
        } 
	}

}
