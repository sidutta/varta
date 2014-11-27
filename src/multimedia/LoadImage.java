/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multimedia;

/**
 *
 *
 */
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import varta.view.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoadImage extends JFrame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	System.out.println("Called main of loadimage");
        Application.launch(args);
    }
    
    String rand_str = null;
    
   public LoadImage(String str) {
    	System.out.println("Called main of loadimage");
    	setTitle("Varta Snap");
    	setLayout(new BorderLayout());
    	Stage chatStage=new Stage();
    	StackPane sp = new StackPane();
    	System.out.println(str);
        Image img = new Image("file:///Users/Siddhartha/NetBeansProjects/"+str+".png");
        ImageView imgView = new ImageView(img);
        sp.getChildren().add(imgView);
        Scene scene = new Scene(sp);

        chatStage.setScene(scene);
        chatStage.show();
        
      	rand_str = str;
    }

//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Varta Snap");
//
//        StackPane sp = new StackPane();
//        Image img = new Image("/Users/aditya/Documents/NetBeansProjects/NetBeansProjects/PlayWithImages/src/"+LoginController.client.imp+".png");
//        ImageView imgView = new ImageView(img);
//        sp.getChildren().add(imgView);
//
//        //Adding HBox to the scene
//        Scene scene = new Scene(sp);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
}
