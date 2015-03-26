package com.neuronrobotics.nrconsole.plugin.scripting;



import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author Alok Omkar
 */
public class FXBrowser extends Application {
   //The default URL.
	public static final String DEFAULT_URL = "http://365programperday.blogspot.in/";

	//For initialization of Browser front end stage.
	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		primaryStage.show();
	}

	//Custom function for creation of New Tabs.
	private Tab createAndSelectNewTab(final TabPane tabPane, final String title) {

		Tab tab = new Tab(title);
		Label aboutLabel = new Label();
		aboutLabel.setText("\n\n\t\t365: A Program for a day.\n\n\t\t\tWelcome to JavaFX Custom Browser. " +
				"\n\t\t\tThis is a custom browser created for demo purpose only." +
				"\n\t\t\tTo start browsing, click on New Tab.");
		aboutLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		tab.setContent(aboutLabel);
		
		final ObservableList<Tab> tabs = tabPane.getTabs();
		tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
		tabs.add(tabs.size() - 1, tab);
		tabPane.getSelectionModel().select(tab);
		return tab;
	}

	//Initialization function of the program.
	private void init(Stage primaryStage) {

		primaryStage.setTitle("365: APPD - Custom Browser");
		Group root = new Group();
		primaryStage.setScene(new Scene(root));
		//To enable Full Screen mode - By Default.
		primaryStage.setFullScreen(true);

		BorderPane borderPane = new BorderPane();
		final TabPane tabPane = new TabPane();
		
		//Preferred Size of TabPane.
		tabPane.setPrefSize(1365, 768);
		
		//Placement of TabPane.
		tabPane.setSide(Side.TOP);

		/* To disable closing of tabs.
		 * tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);*/

		final Tab newtab = new Tab();
		newtab.setText("New Tab");
		newtab.setClosable(false);

		//Addition of New Tab to the tabpane.
		tabPane.getTabs().addAll(newtab);
		
		createAndSelectNewTab(tabPane, "About the Browser");

		//Function to add and display new tabs with default URL display.
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable,
					Tab oldSelectedTab, Tab newSelectedTab) {
				if (newSelectedTab == newtab) {

					Tab tab = new Tab();
					tab.setText("               ");

					//WebView - to display, browse web pages.
					WebView webView = new WebView();
					final WebEngine webEngine = webView.getEngine();
					webEngine.load(DEFAULT_URL);

					final TextField urlField = new TextField(DEFAULT_URL);
					webEngine.locationProperty().addListener(new ChangeListener<String>() {

						@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
							urlField.setText(newValue);
						}
					});

					//Action definition for the Button Go.
					EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {

						@Override public void handle(ActionEvent event) {
							webEngine.load(urlField.getText().startsWith("http://") 
									? urlField.getText() 
											: "http://" + urlField.getText());
						}
					};

					urlField.setOnAction(goAction);

					Button goButton = new Button("Go");
					goButton.setDefaultButton(true);
					goButton.setOnAction(goAction);

					// Layout logic
					HBox hBox = new HBox(5);
					hBox.getChildren().setAll(urlField, goButton);
					HBox.setHgrow(urlField, Priority.ALWAYS);

					final VBox vBox = new VBox(5);
					vBox.getChildren().setAll(hBox, webView);
					VBox.setVgrow(webView, Priority.ALWAYS);


					tab.setContent(vBox);
					final ObservableList<Tab> tabs = tabPane.getTabs();
					tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
					tabs.add(tabs.size() - 1, tab);
					tabPane.getSelectionModel().select(tab);

				}
			}
		});

		borderPane.setCenter(tabPane);
		root.getChildren().add(borderPane);


	}

	public static void main(String args[]){
		launch(args);
	}
 
   
}
