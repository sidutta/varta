package varta.view;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import varta.Client;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private TextField userid;

    @FXML
    private TextField password;

    @FXML
    private Label invalid;

    @FXML
    private Button signup;

    public static Client client;

    public LoginController() {
    }

    @FXML
    private void initialize() {

        loginButton.setOnAction((event) -> {
            String username = userid.getText();
            String passwd = password.getText();
            System.out.println(username);
            System.out.println(passwd);
            client = new Client("localhost", username, 5002);
            //Sh:Set the login controller object for the client
            client.setLoginController(this);
            System.out.println("Login controller set");

//	        try {
            client.connMessage(0, LoginController.client.getUsername(), null, passwd);
//	        	Stage loginStage = (Stage) loginButton.getScene().getWindow();	        	
//	        	Stage chatStage=new Stage();
//	        	chatStage.setTitle("Varta. Welcome "+client.getUsername()+".");
//	            FXMLLoader loader = new FXMLLoader();
//	            loader.setLocation(getClass().getResource("ChatWindow.fxml"));
//	            AnchorPane chatpage = (AnchorPane) loader.load();
//	            Scene scene = new Scene(chatpage);
//	            chatStage.setScene(scene);
//	            loginStage.close();
//	            chatStage.show();

//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
        });

        signup.setOnAction((event) -> {
            try {

                Stage loginStage = (Stage) loginButton.getScene().getWindow();
                Stage signupStage = new Stage();
                signupStage.setTitle("SignUp for MASS CHAT");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("Signup.fxml"));
                AnchorPane signupPage = (AnchorPane) loader.load();
                Scene scene = new Scene(signupPage);
                signupStage.setScene(scene);
                loginStage.close();
                signupStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //Sh: If valid login credenentials, open the chat window
    public void loginValid() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage loginStage = (Stage) loginButton.getScene().getWindow();
                    Stage chatStage = new Stage();
                    chatStage.setTitle("Varta. Welcome " + client.getUsername() + ".");
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("ChatWindow.fxml"));
                    AnchorPane chatpage = (AnchorPane) loader.load();
                    Scene scene = new Scene(chatpage);
                    chatStage.setScene(scene);
                    loginStage.close();
                    chatStage.show();

                    EventHandler<WindowEvent> handler = new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent e) {
                            Platform.exit();
                            System.exit(0);
                        }
                    };
                    chatStage.setOnCloseRequest(handler);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //Sh: If invalid login credentials, show error
    public void loginInValid() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                invalid.setVisible(true);
                System.out.println("Invalid login");
            }
        });

    }

}
