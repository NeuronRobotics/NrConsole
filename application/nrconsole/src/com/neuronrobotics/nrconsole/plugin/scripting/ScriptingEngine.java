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
		System.setOut(orig);
		running = false;
		SwingUtilities.invokeLater(() -> {
			run.setText("Run");
			
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
		code.setText("println(dyio)\n"
				+ "while(true){\n"
				+ "\tThreadUtil.wait(100)\n"
				+ "int value = dyio.getValue(15)\n"
				+ "println(value)\n"
				+ "int scaled = value/4\n"
				+ "print(scaled)\n"
				+ "\tdyio.setValue(0,scaled);\n"
				+ "}");
		
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

	private void start() {
		// TODO Auto-generated method stub
		running = true;
		run.setText("Stop");
		scriptRunner = new Thread(){
			public void run() {
				try{
					output.setText("");
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
	        			output.append("\n"+sw+"\n");
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
				output.append(out.toString());
				out.reset();
			}
		});
		SwingUtilities.invokeLater(() -> {
			// TODO Auto-generated method stub
			handlePrintUpdate();
		});
	}

}
