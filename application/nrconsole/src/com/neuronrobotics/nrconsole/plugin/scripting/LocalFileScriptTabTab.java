package com.neuronrobotics.nrconsole.plugin.scripting;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.reactfx.Change;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.javaxusb.UsbCDCSerialConnection;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.serial.SerialConnection;
import com.neuronrobotics.sdk.ui.ConnectionDialog;
import com.neuronrobotics.sdk.util.FileChangeWatcher;
import com.neuronrobotics.sdk.util.IFileChangeListener;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.dyio.DyIO;

import javafx.scene.control.Tab;

public class LocalFileScriptTabTab extends Tab implements IScriptEventListener{
	
	private ScriptingEngine scripting;
	private PluginManager pm;
	private DyIO dyio;
	private File file;
	
    private static final String[] KEYWORDS = new String[]{
        "def", "in", "as", "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };

    private static final Pattern KEYWORD_PATTERN
            = Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b");
    private final CodeArea codeArea = new CodeArea();
	private VBox vBox;

    
	public LocalFileScriptTabTab(DyIO dyio, PluginManager pm,File file) throws IOException {
		this.dyio = dyio;
		this.pm = pm;
		this.file = file;
		scripting = new ScriptingEngine(dyio, pm,file );
		setText(file.getName());
        codeArea.textProperty().addListener(
                (ov, oldText, newText) -> {
                    Matcher matcher = KEYWORD_PATTERN.matcher(newText);
                    int lastKwEnd = 0;
                    StyleSpansBuilder<Collection<String>> spansBuilder
                    = new StyleSpansBuilder<>();
                    while (matcher.find()) {
                        spansBuilder.add(Collections.emptyList(),
                                matcher.start() - lastKwEnd);
                        spansBuilder.add(Collections.singleton("keyword"),
                                matcher.end() - matcher.start());
                        lastKwEnd = matcher.end();
                    }
                    spansBuilder.add(Collections.emptyList(),
                            newText.length() - lastKwEnd);
                    codeArea.setStyleSpans(0, spansBuilder.create());
                });

        EventStream<Change<String>> textEvents
                = EventStreams.changesOf(codeArea.textProperty());

        textEvents.reduceSuccessions((a, b) -> b, Duration.ofMillis(1000)).
                subscribe(code -> {
                    //code in text box changed
                	scripting.setCode(codeArea.getText());
                	//scripting.save();
                });
    	Platform.runLater(()->{
            codeArea.replaceText(scripting.getCode());
		});

        
        scripting.addIScriptEventListener(this);
		
        
		// Layout logic
		HBox hBox = new HBox(5);
		hBox.getChildren().setAll(codeArea);
		HBox.setHgrow(codeArea, Priority.ALWAYS);

		vBox = new VBox(5);
		vBox.getChildren().setAll(hBox, scripting);
		VBox.setVgrow(codeArea, Priority.ALWAYS);
		
		codeArea.setPrefSize(1000, 1000);
		setContent(vBox);
	}


	@Override
	public void onGroovyScriptFinished(GroovyShell shell, Script script,
			Object result) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onGroovyScriptChanged(String previous, String current) {
		 Cursor place = codeArea.getCursor();
		 codeArea.replaceText(current);
		 codeArea.setCursor(place);
	}


	@Override
	public void onGroovyScriptError(GroovyShell shell, Script script,
			Exception except) {
		// TODO Auto-generated method stub
		
	}
}
