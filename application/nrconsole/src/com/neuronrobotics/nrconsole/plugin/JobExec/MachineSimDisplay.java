package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;
import com.jogamp.newt.Display;

public class MachineSimDisplay extends SimpleApplication{
	JmeCanvasContext ctx;
	JPanel panel;
	private ArrayList<Geometry> shapes = new ArrayList<Geometry>();
	private boolean hasChanged = false;
	GCodes codes;
	private ChaseCamera chaseCam;
	Node obj;
	Matrix3f rotateUp;
	private int layersToShow;
	private int lastShownIndex;
	public MachineSimDisplay(JPanel _panel){
		
		panel = _panel;
	}
	public int getLayersToShow(){
		return layersToShow;
	}
	public void setLayersToShow(int numLayers){
		
		layersToShow= numLayers;
		updateDisplay();
		hasChanged = true;
	}
	public void updateDisplay(){
		for (GCodePosition code : codes) {
			
			if (codes.getLayer(code) > layersToShow){
				lastShownIndex = codes.indexOf(code);
				return;
			}
			lastShownIndex = shapes.size();
			
		}
		
	}
	public int loadGCode(GCodes _codes){
		shapes.clear();
		codes = _codes;
		
		for (GCodePosition code : _codes) {
			if (_codes.isPrintMove(code)){
				boxBuilder(code);
			}
			else{
				lineBuilder(code);
			}
			
		}
		System.out.println("Num of Codes: " + _codes.size());
		System.out.println("Num of Shapes: " + shapes.size());
		lastShownIndex = shapes.size();
		hasChanged = true;
		return codes.numLayers();
	}
	
	public void boxBuilder(GCodePosition _code){
		GCodePosition prevCode =codes.getPrevCode(_code);
		if (prevCode != null){
			
			double x1 =  prevCode.getX();
			double y1 =  prevCode.getY();
			double z1 =  prevCode.getZ();
			
			double x2 =  _code.getX();
			double y2 =  _code.getY();
			double z2 =  _code.getZ();
			if ((x1 > 200) || (y1 > 200) || (z1 > 200)){
				System.out.println("The Ones: (" + x1 + ","+ y1 + "," + z1 + ")");
			}
			if ((x2 > 200) || (y2 > 200) || (z2 > 200)){
				System.out.println("The Twos: (" + x2 + ","+ y2 + "," + z2 + ")");
			}
			
		float centerX = (float) ((x1 + x2)/2);
		float centerY = (float) ((y1 + y2)/2);
		float centerZ = (float) ((z1 +z2)/2);
		
		float extentX = (float) codes.getMoveLength(_code);
		
		extentX = extentX/2;
		float extentY = (float) ((codes.getMoveVolExtWidth(_code))/2);
		float extentZ = (float) ((codes.getLayerHeight(_code))/2);
	
		
		
		Vector3f zeroCenter = new Vector3f(0,0,0);
		
		Vector3f newCenter = new Vector3f(centerX, centerY, centerZ);
		Vector3f start = new Vector3f((float)x1,(float)y1,(float)z1);
		Vector3f end = new Vector3f((float)x2,(float)y2,(float)z2);
		Vector3f dir = end.subtract(start).normalizeLocal();
		
		
		Vector3f init = new Vector3f(1,0,0);
		Matrix3f direction = new Matrix3f();
		direction.fromStartEndVectors(init, dir);		
		Box b = new Box();
		b.updateGeometry(zeroCenter, extentX, extentY, extentZ);
       
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape      
        
        geom.setLocalRotation(direction);
        geom.setLocalTranslation(newCenter);
        
        Material mat = new Material(assetManager,  // Create new material and...
        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
        	
        	            
        
        if (codes.isGoodExtrusion(_code)){
        	mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	mat.setColor("Ambient", ColorRGBA.Green);   // ... color of this object
        	mat.setColor("Diffuse", ColorRGBA.Green);   // ... color of light being reflected
        }
        else{
        	mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	mat.setColor("Ambient", ColorRGBA.Red);   // ... color of this object
        	mat.setColor("Diffuse", ColorRGBA.Red);   // ... color of light being reflected
        }
        if ((extentX > 100) || (extentY > 1) || (extentZ > 1)){
		//	System.out.println("The Extents: (" + x2 + ","+ y2 + "," + z2 + ")");
        	mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	mat.setColor("Ambient", ColorRGBA.Yellow);   // ... color of this object
        	mat.setColor("Diffuse", ColorRGBA.Yellow);   // ... color of light being reflected
		}
        geom.setMaterial(mat);                   // set the cube's material
        
			shapes.add(geom);
		
        
        //hasChanged = true;
		}
       
	}
	
	public void lineBuilder(GCodePosition _code){
		GCodePosition prevCode =codes.getPrevCode(_code);
		if (prevCode != null){
			
			double x1 =  prevCode.getX();
			double y1 =  prevCode.getY();
			double z1 =  prevCode.getZ();
			
			double x2 =  _code.getX();
			double y2 =  _code.getY();
			double z2 =  _code.getZ();
			
			
		
		Vector3f start = new Vector3f((float)x1,(float)y1,(float)z1);
		Vector3f end = new Vector3f((float)x2,(float)y2,(float)z2);
		
       Line l = new Line(start, end);
        Geometry geom = new Geometry("Line", l);  // create line geometry from the shape      
        
        
        
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material        \
        
       mat.setColor("Color", ColorRGBA.Blue);
       
       /* if ((extentX > 100) || (extentY > 1) || (extentZ > 1)){
			System.out.println("The Extents: (" + x2 + ","+ y2 + "," + z2 + ")");
			mat.setColor("Color", ColorRGBA.Green);
		}
		*/
        geom.setMaterial(mat);                   // set the cube's material
        try {
			shapes.set(codes.indexOf(_code), geom);
		} catch (Exception e) {
			shapes.add(geom);
		}
        //hasChanged = true;
		}
       
	}
	
	@Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        /*if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }*/
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        panel.setMinimumSize(new Dimension(100,100));
        createCanvas(); // create canvas!
		ctx = (JmeCanvasContext) getContext();
		ctx.setSystemListener(this);
		panel.add(ctx.getCanvas());
		//SwingUtilities.invokeLater(new SwingApplicationStarter ());
    }
	
	@Override
	public void simpleInitApp() {
		
		    
        flyCam.setEnabled(false);
        
       
        
        
       
        chaseCam = new ChaseCamera(getCamera(),rootNode, getInputManager());
        chaseCam.setDefaultDistance(200);
        chaseCam.setMaxDistance(100000);
        chaseCam.setEnabled(true);
        Vector3f viewOff = new Vector3f(100,100,0);
        chaseCam.setLookAtOffset(viewOff);
        chaseCam.setMaxVerticalRotation((float) Math.PI);
        chaseCam.setMinVerticalRotation(((float) Math.PI)*-1);
        chaseCam.setInvertVerticalAxis(true);
       
        chaseCam.setDefaultHorizontalRotation((float) ((Math.PI/2)));
        chaseCam.setDefaultVerticalRotation((float) (-1*(Math.PI/4)));
        //chaseCam.setUpVector(new Vector3f(0,0,-1));
        
       // chaseCam.setDragToRotate(true);
        setPauseOnLostFocus(false);
        
        /*
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(0,0,-1f).normalizeLocal());
        rootNode.addLight(sun);
        */
       
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.White);
        lamp_light.setRadius(700f);
        lamp_light.setPosition(new Vector3f(0,0,200));
        rootNode.addLight(lamp_light);
        
        PointLight lamp_light1 = new PointLight();
        lamp_light1.setColor(ColorRGBA.White);
        lamp_light1.setRadius(700f);
        lamp_light1.setPosition(new Vector3f(0,200,200));
        rootNode.addLight(lamp_light1);
        
        PointLight lamp_light2 = new PointLight();
        lamp_light2.setColor(ColorRGBA.White);
        lamp_light2.setRadius(700f);
        lamp_light2.setPosition(new Vector3f(200,200,200));
        rootNode.addLight(lamp_light2);
        
        PointLight lamp_light3 = new PointLight();
        lamp_light3.setColor(ColorRGBA.White);
        lamp_light3.setRadius(700f);
        lamp_light3.setPosition(new Vector3f(200,0,200));
        rootNode.addLight(lamp_light3);
        
        obj = new Node();
        Vector3f init = new Vector3f(0,1,0);
        Vector3f end = new Vector3f(0,0,1);
        rotateUp = new Matrix3f();
        rotateUp.fromStartEndVectors(init, end);
        //obj.setLocalRotation(rotateUp);
        rootNode.attachChild(obj);
        //getFlyByCamera().setDragToRotate(true);
        Quad base = new Quad(200, 200);
        Geometry geom = new Geometry("Base", base);
        //geom.setLocalTranslation(new Vector3f(-250,-250,0));
        Material mat = new Material(assetManager,  // Create new material and...
        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
        mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
    	mat.setColor("Ambient", ColorRGBA.Gray);   // ... color of this object
    	mat.setColor("Diffuse", ColorRGBA.Gray);   // ... color of light being reflected
    	geom.setMaterial(mat);
    	rootNode.attachChild(geom);
	}
	
	@Override
	 public void simpleUpdate(float tpf){
		if (hasChanged == true){
			hasChanged = false;
			System.out.println("Number of lines: " + shapes.size());
			
			obj.detachAllChildren();
			for (int i = 0; i < lastShownIndex; i++) {				
				obj.attachChild(shapes.get(i));
			} 
			System.out.println("How many children: " + obj.getChildren().size());
		}
		//System.out.println("Vertical: " + chaseCam.getVerticalRotation() + "Horizontal: " + chaseCam.getHorizontalRotation());
	 }
	
	
}
