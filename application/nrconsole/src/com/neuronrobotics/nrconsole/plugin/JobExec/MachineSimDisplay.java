package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jme3test.games.CubeField;
import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
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
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
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
	private ArrayList<Geometry> shapesCombined = new ArrayList<Geometry>();
	private boolean hasChanged = false;
	GCodes codes;
	private ChaseCamera chaseCam;
	Node obj;
	Matrix3f rotateUp;
	private int layersToShow;
	private int lastShownIndex;
	private Material matGood;
	private Material matBad;
	private Material matLine;
	private boolean loading= false;
	private BitmapText loadingText = null;
	private Vector3f scaleRate = new Vector3f(.5f,.5f,.5f);
	private Vector3f printVolume = new Vector3f(200,200,200);
	private Mesh pVol;
	
	private BoundingBox pbb;
	public MachineSimDisplay(JPanel _panel){		
		panel = _panel;
	}
	
	public void configure(float _printX, float _printY, float _printZ){
		printVolume.set(_printX, _printY, _printZ);
	}
	
	
	public void loadingGCode(){
		loading = true;
	}
	
	
	private Mesh getPrintVol(){
		if (pVol == null){
		
		if (printVolume.getY()==0){//It's a cylinder
			pVol = new Cylinder(100,100,printVolume.getX(),printVolume.getZ(), true, false);
		}
		else{//It's a cube
			Box b = new Box();
			Vector3f printCenter = new Vector3f(printVolume.getX()/2,printVolume.getY()/2,0);//TODO: This should be made a member variable for adjustable print centers
			b.updateGeometry(printCenter, printVolume.getX()/2, printVolume.getY()/2, printVolume.getZ()/2);
			pVol = b;
		}
		}
		
		return pVol;
		
	}
	private Mesh getPrintBase(){
		Mesh base;
		
		if (printVolume.getY()==0){//It's a cylinder
			base = new Cylinder(100,100,printVolume.getX(),0,true,false);
		}
		else{//It's a cube
			Box b = new Box();
			Vector3f printCenter = new Vector3f(printVolume.getX()/2,printVolume.getY()/2,0);//TODO: This should be made a member variable for adjustable print centers
			b.updateGeometry(printCenter, printVolume.getX()/2, printVolume.getY()/2, 0);
			base = b;
		}
		
		
		return base;
		
	}
	private BoundingBox getVolBB(){
		if (pbb == null){
			pbb = new BoundingBox((BoundingBox) getPrintVol().getBound());
		}
		
		
		return pbb;
	}
	
	
	
	private Material getMatGood(){
		if (matGood == null){
			matGood = new Material(assetManager,  // Create new material and...
	        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
			matGood.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	matGood.setColor("Ambient", ColorRGBA.Green);   // ... color of this object
        	matGood.setColor("Diffuse", ColorRGBA.Green);   // ... color of light being reflected
		}
		return matGood;
		
	}
	private Material getMatProblem(){
		if (matBad == null){
			matBad = new Material(assetManager,  // Create new material and...
	        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
			matBad.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	matBad.setColor("Ambient", ColorRGBA.Orange);   // ... color of this object
        	matBad.setColor("Diffuse", ColorRGBA.Orange);   // ... color of light being reflected
		}
		return matBad;
	}
	private Material getMatLine(){
		if (matLine == null){
			matLine = new Material(assetManager,
			          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material        \
	        
		       matLine.setColor("Color", ColorRGBA.Blue); 
		}
		return matLine;
	}
	private Material getMatFail(){
		if (matBad == null){
			matBad = new Material(assetManager,  // Create new material and...
	        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
			matBad.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        	matBad.setColor("Ambient", ColorRGBA.Red);   // ... color of this object
        	matBad.setColor("Diffuse", ColorRGBA.Red);   // ... color of light being reflected
		}
		return matBad;
	}
	
	public int getLayersToShow(){
		return layersToShow;
	}
	public void setLayersToShow(int numLayers){
		
		layersToShow= numLayers;
		for (int i = 0; i < shapes.size(); i++) {
			Geometry shape = shapes.get(i);
			
			if (codes.getLayer(codes.get(i)) > layersToShow){
				shape.setCullHint(CullHint.Always);
			}
			else{
				shape.setCullHint(CullHint.Inherit);
			}
		}
		shapesCombined = (ArrayList<Geometry>) GeometryBatchFactory.makeBatches(shapes);
		
		System.out.println("Items after Combination: " + shapesCombined.size());
		
		hasChanged = true;
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
		shapesCombined = (ArrayList<Geometry>) GeometryBatchFactory.makeBatches(shapes);
		System.out.println("Items after Combination: " + shapesCombined.size());
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
        
        
        	
        	            
        
        if (codes.isGoodExtrusion(_code)){
        	geom.setMaterial(getMatGood());
        }
        else{
        	geom.setMaterial(getMatProblem());
        }
        if ((extentX > 100) || (extentY > 1) || (extentZ > 1)){
		//	System.out.println("The Extents: (" + x2 + ","+ y2 + "," + z2 + ")");
        	geom.setMaterial(getMatProblem());
		}
        //TODO: Need to add some way to check if the GCodes are contained in the build area
        
        
        	  
        	  // set the cube's material
        
        
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
        
        
        
        
       
       /* if ((extentX > 100) || (extentY > 1) || (extentZ > 1)){
			System.out.println("The Extents: (" + x2 + ","+ y2 + "," + z2 + ")");
			mat.setColor("Color", ColorRGBA.Green);
		}
		*/
        geom.setMaterial(getMatLine());                   // set the cube's material
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
		
    }
	
	@Override
	public void simpleInitApp() {
		
		    
        flyCam.setEnabled(false);
        
       BoundingBox bb = getVolBB();
        
        
        chaseCam = new ChaseCamera(getCamera(),rootNode, getInputManager());
        chaseCam.setHideCursorOnRotate(true);
        chaseCam.setDefaultDistance(353);
        chaseCam.setMaxDistance(100000);
        chaseCam.setEnabled(true);
        
        Vector3f viewOff = new Vector3f(bb.getCenter().getX(),0,bb.getCenter().getY()*-1);
        
        chaseCam.setLookAtOffset(viewOff);
        chaseCam.setMaxVerticalRotation((float) (Math.PI/2));
        chaseCam.setMinVerticalRotation(0);
        chaseCam.setInvertVerticalAxis(true);
       
        chaseCam.setDefaultHorizontalRotation(2);
        chaseCam.setDefaultVerticalRotation(0.824f);
        //chaseCam.setUpVector(new Vector3f(0,0,-1));
        
        chaseCam.setDragToRotate(true);
        setPauseOnLostFocus(false);
              
              Vector3f max = bb.getMax(null);
              System.out.println(max.toString());
              Vector3f min = bb.getMin(null);
              System.out.println(min.toString());
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.White);
        lamp_light.setRadius(700f);
        lamp_light.setPosition(new Vector3f(min.getX(),max.getZ(),min.getY()));
        rootNode.addLight(lamp_light);
        
        PointLight lamp_light1 = new PointLight();
        lamp_light1.setColor(ColorRGBA.White);
        lamp_light1.setRadius(700f);
        lamp_light1.setPosition(new Vector3f(max.getX(), max.getZ(),min.getY()));
        rootNode.addLight(lamp_light1);
        
        PointLight lamp_light2 = new PointLight();
        lamp_light2.setColor(ColorRGBA.White);
        lamp_light2.setRadius(700f);
        lamp_light2.setPosition(new Vector3f(min.getX(),max.getZ(),max.getY()));
        rootNode.addLight(lamp_light2);
        
        PointLight lamp_light3 = new PointLight();
        lamp_light3.setColor(ColorRGBA.White);
        lamp_light3.setRadius(700f);
        lamp_light3.setPosition(new Vector3f(max.getX(),max.getZ(),max.getY()));
        
        rootNode.addLight(lamp_light3);
        
        obj = new Node();
        
        rootNode.attachChild(obj);
        
        
        
        //Quad base = new Quad(200, 200);
        
        Geometry geom = new Geometry("Base", getPrintBase());
        //geom.setLocalTranslation(new Vector3f(-250,-250,0));
        Material mat = new Material(assetManager,  // Create new material and...
        	    "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
        		
        mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
    	mat.setColor("Ambient", ColorRGBA.DarkGray);   // ... color of this object
    	mat.setColor("Diffuse", ColorRGBA.DarkGray);   // ... color of light being reflected
        		
     
    	geom.setMaterial(mat);
    	rootNode.attachChild(geom);
    	
        Vector3f init = new Vector3f(0,1,0);
        Vector3f end = new Vector3f(0,0,-1);
        rotateUp = new Matrix3f();
        rotateUp.fromStartEndVectors(init, end);
        rootNode.setLocalRotation(rotateUp);
       
        
       
        loadAxes(0,0,0);
        
	}
	public void loadAxes(int xOff, int yOff, int zOff){
		Vector3f zero = new Vector3f(xOff,yOff,zOff);
		Vector3f xV = new Vector3f(25 + xOff,yOff,zOff);
		Vector3f yV = new Vector3f(xOff,25 + yOff,zOff);
		Vector3f zV = new Vector3f(xOff,yOff,25 + zOff);
		Line x = new Line(zero, xV);
		Line y = new Line(zero, yV);
		Line z = new Line(zero, zV);
		Geometry geomX = new Geometry("X Axis", x);
		Geometry geomY = new Geometry("Y Axis", y);
		Geometry geomZ = new Geometry("Z Axis", z);
		geomX.setMaterial(getUnshadedColor(ColorRGBA.Green));
		geomY.setMaterial(getUnshadedColor(ColorRGBA.Red));
		geomZ.setMaterial(getUnshadedColor(ColorRGBA.Blue));
		
		rootNode.attachChild(geomX);
		rootNode.attachChild(geomY);
		rootNode.attachChild(geomZ);
	}
	
	public Material getUnshadedColor(ColorRGBA _color){
		Material mat = new Material(assetManager,
		          "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", _color);
		return mat;
	}
	
	private float scaleFactor = 1.0f;
	
	public BitmapText getLoadingText(float tpf){
		if (loadingText == null){
			guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
	        loadingText = new BitmapText(guiFont, false);
	        
	        loadingText.setSize(guiFont.getCharSet().getRenderedSize());
	        loadingText.setText("Loading...");
	        
	        float offsetX = (panel.getWidth()/2) - ((loadingText.getLineWidth()*loadingText.getLocalScale().getX())/2);
			float offsetY = (panel.getHeight()/2) + ((loadingText.getHeight()*loadingText.getLocalScale().getX())/2);
			
			loadingText.setLocalTranslation(offsetX, offsetY, 0);
	        loadingText.setName("Loading Text");
		}
		else{
			if (loadingText.getLocalScale().getX() > 5.0f){
				scaleFactor = -2.0f;
				
			}
			if (loadingText.getLocalScale().getX() < 1.5f){
				scaleFactor = 2.0f;
				
			}
			float scaleInc = scaleFactor * tpf;
			scaleInc += loadingText.getLocalScale().getX();
			
			loadingText.setLocalScale(scaleInc);
			
			
			float offsetX = (panel.getWidth()/2) - ((loadingText.getLineWidth()*loadingText.getLocalScale().getX())/2);
			float offsetY = (panel.getHeight()/2) + ((loadingText.getHeight()*loadingText.getLocalScale().getX())/2);
			
			loadingText.setLocalTranslation(offsetX, offsetY, 0);
			
		}
			
		
		return loadingText;
	}
	
	@Override
	 public void simpleUpdate(float tpf){
		if (loading == true  && hasChanged == false){
			//Do something to let the user know things are happening			
	        guiNode.attachChild(getLoadingText(tpf));
		}
		else{
			
			guiNode.detachChildNamed("Loading Text");
		}
		
		if (hasChanged == true){
			loading =  false;
			hasChanged = false;
			System.out.println("Number of lines: " + shapes.size());
			
						
			
			obj.detachAllChildren();
			
			for (Geometry geom : shapesCombined) {
				obj.attachChild(geom);
			}
			System.out.println("Last Index Shown: " +lastShownIndex);
			System.out.println("How many children: " + obj.getChildren().size());
		}
		//System.out.println("Vertical: " + chaseCam.getVerticalRotation() + 
			//	"Horizontal: " + chaseCam.getHorizontalRotation()+
				//"Zoom: " + chaseCam.getDistanceToTarget());
	 }
	
	
}
