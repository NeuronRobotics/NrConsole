package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.io.InputStream;

import javax.vecmath.Point3f;

import com.neuronrobotics.replicator.driver.PrinterStatus;
import com.neuronrobotics.replicator.driver.PrinterStatusListener;
import com.neuronrobotics.replicator.driver.PrinterStatus.PrinterState;
import com.neuronrobotics.replicator.driver.interpreter.CodeHandler;
import com.neuronrobotics.replicator.driver.interpreter.GCodeInterpreter;
import com.neuronrobotics.replicator.driver.interpreter.GCodeLineData;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class GCodeLoader {
	private GCodeInterpreter interp;
	private GCodes codes = new GCodes();
	private double extOffset = 0;
	public double currlayer;
	public double prevLayer;
	public GCodes getCodes() {
		return codes;
	}

	public void setCodes(GCodes _codes) {
		codes = _codes;
	}

	public boolean loadCodes(InputStream gcode) {
		//this should be a thread that takes the gcode and sends it to the printer

		interp=new GCodeInterpreter(); // Could reuse.
		addHandlers(interp);
		extOffset = 0;
		System.out.println("Reached print.");
		try {
			interp.tryInterpretStream(gcode);
			System.out.println("End of print.");
			return true;
		} catch (Exception e) { 
			// um... this is bad. Ideally, the kinematics methods probably shouldn't through Exception, but we'll just catch it here for now.
			System.err.println(e);
			e.printStackTrace();
			System.out.println("Abnormal end of print");
			return false;
		}
	}

	void addHandlers(GCodeInterpreter interp) {
		// Temperature control
		interp.addMHandler(104, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.addMHandler(107, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.addMHandler(106, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.addMHandler(109, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.addMHandler(82, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.addMHandler(73, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		
		interp.addGHandler(92, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
			if (codes.size() > 1){
					System.out.println("Extruder Reset: " + codes.get(codes.size() - 2).getE());					
					extOffset = codes.get(codes.size() - 2).getE();
					codes.get(codes.size() - 1).setE(extOffset);
			}
			}
		});
		interp.addGHandler(6, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				
			}
		});
		interp.setGHandler(0, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				double x =0.0;
				double y =0.0;
				double z =0.0;
				double e =0.0;
				try {
					x = next.getWord('X');
				} catch (Exception e1) {
					
				}
				try {
					y = next.getWord('Y');
				} catch (Exception e2) {
					
				}
				try {
					z = next.getWord('Z');
				} catch (Exception e3) {
					
				}
				try {
					e = next.getWord('E');
				} catch (Exception e4) {
					
				}
				codes.add(x,y,z,e+ extOffset);
				
			}
		});
		interp.setGHandler(1, new CodeHandler() {
			public void execute(GCodeLineData prev, GCodeLineData next) throws Exception {
				double x =0.0;
				double y =0.0;
				double z =0.0;
				double e =0.0;
				try {
					x = next.getWord('X');
				} catch (Exception e1) {
					
				}
				try {
					y = next.getWord('Y');
				} catch (Exception e2) {
					
				}
				try {
					z = next.getWord('Z');
				} catch (Exception e3) {
					
				}
				try {
					e = next.getWord('E');
				} catch (Exception e4) {
					
				}
				codes.add(x,y,z,e+ extOffset);
				}			
		});
		
	}
}
