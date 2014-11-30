package varta;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import varta.view.LoginController;

public class MainApp extends Application {

	private Stage primaryStage;
	public static String our_server;
	public static String stream_server;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Login");
		showLoginPage();
	}

	public void showLoginPage() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Login.fxml"));
			AnchorPane loginpage = (AnchorPane) loader.load();
			Scene scene = new Scene(loginpage);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		our_server = args[0];
		stream_server = args[1];
		launch(args);

	}

}
