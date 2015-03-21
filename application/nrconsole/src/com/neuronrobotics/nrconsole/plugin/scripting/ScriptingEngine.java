package com.neuronrobotics.nrconsole.plugin.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;


import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;


import com.neuronrobotics.sdk.dyio.DyIORegestry;

import com.neuronrobotics.sdk.util.ThreadUtil;

import net.miginfocom.swing.MigLayout;

public class ScriptingEngine extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea output;
	private JTextArea code;
	private JButton run;
	private boolean running = false;
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	private PrintStream orig= System.out;
	private Thread scriptRunner=null;
	
	private void reset(){
		SwingUtilities.invokeLater(() -> {
			running = false;
			run.setText("Run");
			System.setOut(orig);
		});

	}
	
	public ScriptingEngine(){
		setName("Bowler Scripting");
		setLayout(new MigLayout());
		code = new JTextArea(4000, 20);
		output = new JTextArea(4000, 20);
		run = new JButton("Run");
		add(run,"wrap");
		add(code,"wrap");
		add(output,"wrap");
		ThreadUtil.wait(1);
		code.setText("println(dyio)\nwhile(true){\n\tThreadUtil.wait(1)\n}");
		
		run.addActionListener(e -> {
			if(running)
				stop();
			else
				start();			
		});
	}

	private void stop() {
		// TODO Auto-generated method stub
		reset();

		scriptRunner.interrupt();
		try {
			scriptRunner.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	private void start() {
		// TODO Auto-generated method stub
		running = true;
		run.setText("Stop");
		scriptRunner = new Thread(){
			public void run() {
				try{
					CompilerConfiguration cc = new CompilerConfiguration();
	
		            cc.addCompilationCustomizers(
		                    new ImportCustomizer().
		                    addStarImports("com.neuronrobotics",
		                    		"com.neuronrobotics.sdk.util"
		                    		).addStaticStars("com.neuronrobotics.sdk.util.ThreadUtil")
		                    );
		        	
		            Binding binding = new Binding();
		            System.setOut(new PrintStream(out));
	
		            binding.setVariable("dyio", DyIORegestry.get());
		            GroovyShell shell = new GroovyShell(getClass().getClassLoader(),
		            		binding, cc);
	
		            Script script = shell.parse(code.getText());
		 
		            Object obj = script.run();
		            reset();
	            }catch(Exception e){
	            	//e.printStackTrace();
	            	SwingUtilities.invokeLater(() -> {
		            	StringWriter sw = new StringWriter();
		            	PrintWriter pw = new PrintWriter(sw);
		            	e.printStackTrace(pw);
	        			output.setText(output.getText()+"\n"+sw+"\n");
	        			running = false;
	        			run.setText("Run");
	        			System.setOut(orig);
	        		});
	            	throw e;
	            }
				
			}
		};
		SwingUtilities.invokeLater(() -> {
			handlePrintUpdate();
		});
		scriptRunner.start();
	}

	private void handlePrintUpdate() {
		// TODO Auto-generated method stub
		ThreadUtil.wait(100);
		SwingUtilities.invokeLater(() -> {
			if(out.size()>0){
				output.setText(out+output.getText());
				out.reset();
			}
		});
		SwingUtilities.invokeLater(() -> {
			// TODO Auto-generated method stub
			handlePrintUpdate();
		});
	}

}
