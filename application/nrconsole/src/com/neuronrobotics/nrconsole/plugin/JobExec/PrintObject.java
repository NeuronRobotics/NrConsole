package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.io.File;
import java.util.ArrayList;

import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;

public class PrintObject{
	
	
	private ArrayList<LayerGeoms> layers = new ArrayList<LayerGeoms>();
	private GCodes codes;
	private MachineSimDisplay msd;
	private DisplayConfigs displayConfigs;	
	private String name;
	private File codeFile;

	
	public static enum PrintStatus{
		GOOD, PROBLEM, FAIL
	}
	
	
	private int numFailLines = 0;
	private int numProblemLines =0;
	private int numGoodLines = 0;
	private int numMoveLines = 0;
	
	public PrintStatus getPrintStatus(){
		if (getNumFailLines() > 0){
			return PrintStatus.FAIL;
		}
		else if (getNumProblemLines() > 0){
			return PrintStatus.PROBLEM;
		}
		else{
			return PrintStatus.GOOD;
		}
	}
	
	public int getNumMoveLines(){
		return numMoveLines;
	}
	public int getNumFailLines() {
		return numFailLines;
	}

	public int getNumProblemLines() {
		return numProblemLines;
	}

	public int getNumGoodLines() {
		return numGoodLines;
	}
	
	
	
	public int numLines(){
		return numFailLines + numProblemLines + numGoodLines;
	}
	
	public PrintObject ( MachineSimDisplay _msd){
		codes = null;
		msd = _msd;
	}
	public PrintObject (GCodes _codes, MachineSimDisplay _msd){
		codes = _codes;
		msd = _msd;
		
	}
	public PrintObject (GCodes _codes, MachineSimDisplay _msd, File _codeFile){
		codes = _codes;
		msd = _msd;
		name = _codeFile.getName();
		codeFile = _codeFile;
		displayConfigs = msd.getDisplayConfigs();
		msd.loadPrintObject(this);
	}
	
	
	
	private Geometry buildGeom(GCodePosition _code){
		if (codes.isPrintMove(_code)){
			return boxBuilder(_code);
		}
		else{
			return lineBuilder(_code);
		}
	}
	private Geometry boxBuilder(GCodePosition _code){
		//prevTime = System.currentTimeMillis();
		GCodePosition prevCode =codes.getPrevCode(_code);
		if (prevCode != null){
			
			float extentX = (float) codes.getMoveLength(_code);
			
			extentX = extentX/2;
			float extentY = (float) ((codes.getMoveVolExtWidth(_code))/2);
			float extentZ = (float) ((codes.getLayerHeight(_code))/2);
			if (Float.isInfinite(extentX) || Float.isInfinite(extentY) || Float.isInfinite(extentZ)){
				return null;
			}
			
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
        	geom.setMaterial(msd.getMatGood());
        	geom.setName("Good Extrude");
        	numGoodLines++;
        }
        else{
        	geom.setMaterial(msd.getMatProblem());
        	geom.setName("Problem Extrude");
        	numProblemLines++;
        }
        if ((extentX > 100) || (extentY > 1) || (extentZ > 1)){
		//	System.out.println("The Extents: (" + x2 + ","+ y2 + "," + z2 + ")");
        	geom.setMaterial(msd.getMatProblem());
        	geom.setName("Problem Extrude");
        	numProblemLines++;
		}
               
        /*TODO: this is bad...
         * we should be able to find a way to check for this regardless of build volume shape
         */
        if (displayConfigs.isCubeVol()){
        	if (!displayConfigs.getVolBB().contains(end) || !displayConfigs.getVolBB().contains(start)){
        		geom.setMaterial(msd.getMatFail());
        		geom.setName("Fail Extrude");
        		numFailLines++;
        	}
        }
        else{
        	Vector3f layerCenterStart = new Vector3f(displayConfigs.getVolBB().getCenter().getX(), displayConfigs.getVolBB().getCenter().getY(), start.getZ());
        	Vector3f layerCenterEnd = new Vector3f(displayConfigs.getVolBB().getCenter().getX(), displayConfigs.getVolBB().getCenter().getY(), end.getZ());
        	if (layerCenterEnd.distance(end) > (displayConfigs.getPrintVolume().getX()/2)){
        		geom.setMaterial(msd.getMatFail());
        		geom.setName("Fail Extrude");
        		numFailLines++;
        	}
        	if (layerCenterStart.distance(start) > (displayConfigs.getPrintVolume().getX()/2)){
        		geom.setMaterial(msd.getMatFail());
        		geom.setName("Fail Extrude");
        		numFailLines++;
        	}
        }
        
        	  
        	  // set the cube's material
        return(geom);
        
			//shapes.add(geom);
		
        
        //hasChanged = true;
		}
		return null;
       
	}
	private Geometry lineBuilder(GCodePosition _code){
		//prevTime = System.currentTimeMillis();
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
        geom.setMaterial(msd.getMatLine());                   // set the cube's material
        numMoveLines++;
        return geom;
		}
		return null;
	}
	
	
	
	
	public int getNumLayers(){
		return layers.size();
	}
	
	/**
	 * This method batches all of the raw geometries in a layer
	 * @param _layer The layer in which to batch all geometries
	 */
	private void batchLayer(int _layer){
		if (_layer < getNumLayers()){
			ArrayList<Geometry> x =  (ArrayList<Geometry>) GeometryBatchFactory.makeBatches(layers.get(_layer));
			layers.get(_layer).clear();
			layers.get(_layer).addAll(x);
		}
	}
	
	/**
	 * This method batches the geometries in all of the layers
	 */
	private void batchAllLayers(){
		for (LayerGeoms layer : layers) {
			ArrayList<Geometry> x =  (ArrayList<Geometry>) GeometryBatchFactory.makeBatches(layer);
			layer.clear();
			layer.addAll(x);
		}
	}
	
/**
 * After all GCodes have be loaded, process them to create geometries representing each one
 */
	public void processGCodes(){
		int layer = 0;
		layers.clear();
		layers.add(new LayerGeoms());
		System.out.println("Begin Processing GCodes!");
		for (GCodePosition code : codes) {
			if (codes.getLayer(code) != layer){
				layers.add(new LayerGeoms());
				batchLayer(layer);
				layer++;
				System.out.println("New Layer: " + layer);
			}
			Geometry geom = buildGeom(code);
			if (geom != null){
				layers.get(getNumLayers()-1).add(geom);
			}
		}
		batchAllLayers();
	}
	
	
	/**
	 * This method returns an ArrayList of Geometry (one for each material) which contains the batched 
	 * geometries of all layers up to _upToLayer.
	 * @param _upToLayer The number of the first layer which should not be displayed
	 * @return The ArrayList of Geometry which represents the object up to the layer specified
	 */
	public ArrayList<Geometry> getBatchedLayers(int _upToLayer){
		ArrayList<Geometry> longList = new ArrayList<Geometry>();
		for (int i = 0; i < _upToLayer; i++) {
			for (Geometry subGeom : layers.get(i)) {
				longList.add(subGeom);
			}
		}		
		return (ArrayList<Geometry>) GeometryBatchFactory.makeBatches(longList);
		
	}
	
	/**
	 This method returns an ArrayList of Geometry (one for each material) which contains the batched 
	 * geometries of the entire object. 
	 * @return The ArrayList of Geometry which represents the object
	 */
	public ArrayList<Geometry> getBatchedObject(){
		return getBatchedLayers(getNumLayers());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public File getCodeFile() {
		return codeFile;
	}
	public void setCodeFile(File codeFile) {
		this.codeFile = codeFile;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
