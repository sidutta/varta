package multimedia;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.sarxos.webcam.WebcamResolution;

import varta.view.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoadImage extends JFrame {

	/**
	 * @param args
	 *            the command line arguments
	 */
	Stage chatStage;
	ProgressBar pb;
	ProgressIndicator pins;

	public static void main(String[] args) {
		System.out.println("Called main of loadimage");
		Application.launch(args);
	}

	public void setProgress(double prog) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				pb.setProgress(prog);
				pins.setProgress(prog);
				System.out.println("setting progress");
			}
		});

	}

	String rand_str = null;

	public ProgressBar setNewStage(String str) {

		final Float value = 0.0f;
		final Label label = new Label();
		final ProgressBar pbs = new ProgressBar();
		pbs.setTranslateX(400);
		pbs.setScaleX(4);
		final ProgressIndicator pin = new ProgressIndicator();
		final HBox hbs[] = new HBox[2];
		chatStage = new Stage();
		System.out.println("Shiii " + chatStage);
		Group root = new Group();
		Dimension size = WebcamResolution.VGA.getSize();
		Scene scene = new Scene(root, size.getWidth(), size.getHeight() + 12);

		chatStage.setScene(scene);
		chatStage.setTitle("Image");

		// label.setText("progress:" + value);

		pbs.setProgress(value);

		// pin.setProgress(value);
		hbs[0] = new HBox();
		hbs[0].setSpacing(1);

		hbs[0].getChildren().addAll(pbs);

		hbs[1] = new HBox();
		hbs[1].setSpacing(1);
		Image img = new Image("file:///Users/aditya/Downloads/" + str + ".png");
		System.out.println("http:///10.2.10.34:5080/demos/" + str + ".png");
		ImageView imgView = new ImageView(img);
		hbs[1].getChildren().add(imgView);
		final VBox vb = new VBox(100);

		vb.getChildren().addAll(hbs);
		scene.setRoot(vb);
		chatStage.show();
		return pbs;
	}

	public LoadImage(String str) {
		System.out.println("Called main of loadimage");
		pb = setNewStage(str);

		rand_str = str;
	}

	public void CloseImage() {
		chatStage.close();
	}

	public ProgressBar getProg() {
		return pb;
	}
}