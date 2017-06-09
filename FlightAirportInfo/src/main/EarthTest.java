package main;

import java.awt.Frame;
import java.awt.Point;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

import main.CubesTest;

public class EarthTest extends SimpleApplication {

	private static final float TEXTURE_LAT_OFFSET = -0.2f;
	private static final float TEXTURE_LON_OFFSET = 2.8f;
	Node earth_node;
	Node LinesNode;
	Node SpheresNode;
	private static Plane plane;

	@Override
	public void simpleInitApp() 
	{
		LinesNode = new Node("LinesNode");
		SpheresNode = new Node("SpheresNode");
		assetManager.registerLocator("earth.zip", ZipLocator.class);
		Spatial earth_geom = assetManager.loadModel("earth/Sphere.mesh.xml");
		earth_node = new Node("earth");
		earth_node.attachChild(earth_geom);
		//earth_node.setLocalScale(5.0f);
		rootNode.attachChild(earth_node);
		for (Airport value : MainSystem.getListAirports().values()) 
		{
			displayTown(value.getLatitude(),value.getLongitude());  
		}
		for(Flight f : MainSystem.getListFlight().values())
		{
			Airport airportDepart = f.getAirportDepart();
			float chLat = airportDepart.getLatitude();
			float chLong = airportDepart.getLongitude();
			float chAlt = 3f;
			//Point depart = new Point(chLat,chLong);
			Spatial planeSpatial = assetManager.loadModel("earth/plane.obj");
			
			Material matPlane = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
			matPlane.getAdditionalRenderState().setLineWidth(4.0f);
			matPlane.setColor("Color", ColorRGBA.Red);
			planeSpatial.setMaterial(matPlane);
			
			Vector3f v = new Vector3f(1,0,0);
			Vector3f oldVect = new Vector3f(chLat,chLong,chAlt);
			for(int i=0;i<100;i++)
			{
				float t=i/5.0f;
				Vector3f newVect = new Vector3f(FastMath.cos(t),t/5.0f,FastMath.sin(t));
				Line line = new Line(oldVect, newVect);
				Geometry lineGeo = new Geometry("lineGeo", line);
				Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
				mat.getAdditionalRenderState().setLineWidth(2.0f);
				mat.setColor("Color", ColorRGBA.Red);
				lineGeo.setMaterial(mat);
				lineGeo.setLocalTranslation(0.0f,0.0f,8.0f);
				LinesNode.setMaterial(mat);
				LinesNode.attachChild(lineGeo);
				rootNode.attachChild(LinesNode);
				oldVect = newVect;
			}
			planeSpatial.setLocalTranslation(v);
			
		}
		
		
		final JMenuBar menubar = new JMenuBar();
		final JMenu objectsMenu = new JMenu("File");
		final JMenu helpMenu = new JMenu("Help");
		menubar.add(objectsMenu);
		menubar.add(helpMenu);
		//Frame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
		//frame.setJMenuBar(menubar);
		
		
		/*
		Vector3f oldVect = new Vector3f(5,0,0);
		Vector3f newVect = new Vector3f(-1,1,0);
		Line line = new Line(oldVect, newVect);
		Geometry lineGeo = new Geometry("lineGeo", line);
		Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(4.0f);
		mat.setColor("Color", ColorRGBA.Red);
		lineGeo.setMaterial(mat);
		lineGeo.setLocalTranslation(0.0f,0.0f,6.0f);
		LinesNode.setMaterial(mat);
		LinesNode.attachChild(lineGeo);*/
		rootNode.attachChild(LinesNode);
		rootNode.attachChild(SpheresNode);
		
		
		
		/*
		Node LinesNode = new Node("LinesNode");
		Vector3f oldVect = new Vector3f(1,0,0);
		for(int i=0;i<100;i++)
		{// ...
			float t=i/5.0f;
			Vector3f newVect = new Vector3f(FastMath.cos(t),t/5.0f,FastMath.sin(t));
			Line line = new Line(oldVect, newVect);
			Geometry lineGeo = new Geometry("lineGeo", line);
			Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
			mat.getAdditionalRenderState().setLineWidth(4.0f);
			mat.setColor("Color", ColorRGBA.Red);
			lineGeo.setMaterial(mat);
			lineGeo.setLocalTranslation(0.0f,0.0f,8.0f);
			LinesNode.setMaterial(mat);
			LinesNode.attachChild(lineGeo);
			rootNode.attachChild(LinesNode);
			oldVect = newVect;
		}*/
		
		
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-2,-10,1));
		directionalLight.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(directionalLight);
		viewPort.setBackgroundColor(new ColorRGBA(0.1f,0.1f,0.1f,1.0f));
		flyCam.setEnabled(false);
		ChaseCamera chaseCam = new ChaseCamera(cam,earth_geom,inputManager);
		chaseCam.setDragToRotate(true);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setRotationSpeed(10.0f);;
		chaseCam.setMinVerticalRotation((float)-(Math.PI/2 - 0.0001f));
		chaseCam.setMaxVerticalRotation((float)-Math.PI/2);
		chaseCam.setMaxDistance(30.0f);

	}
	private static Vector3f geoCoordTo3dCoord(float lat, float lon)
	{
		float lat_cor = lat + TEXTURE_LAT_OFFSET;
		float lon_cor = lon + TEXTURE_LON_OFFSET;
		return new Vector3f(- FastMath.sin(lon_cor * FastMath.DEG_TO_RAD)
							* FastMath.cos(lat_cor * FastMath.DEG_TO_RAD),
							  FastMath.sin(lat_cor * FastMath.DEG_TO_RAD),
							- FastMath.cos(lon_cor * FastMath.DEG_TO_RAD)
							* FastMath.cos(lat_cor * FastMath.DEG_TO_RAD));
	}
	private void displayTown(float latitude, float longitude)
	{
		Sphere sphere = new Sphere(16,8,0.002f);
		//Geometry town = new Geometry("Town",sphere);
		Geometry sphereGeo = new Geometry("lineGeo", sphere);
		Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(4.0f);
		mat.setColor("Color", ColorRGBA.Red);
		sphereGeo.setMaterial(mat);
		Vector3f v = geoCoordTo3dCoord(latitude,longitude);
		sphereGeo.setLocalTranslation(v);
		SpheresNode.setMaterial(mat);
		SpheresNode.attachChild(sphereGeo);
		
		
	}
/*
	public static void main(String[] args) 
	{
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1200, 800);
		settings.setSamples(8);
		settings.setFrameRate(60);
		settings.setVSync(true);
		
		earthTest app = new earthTest();
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setDisplayStatView(false);
		app.setDisplayFps(false);
		app.start();
	}*/

}
