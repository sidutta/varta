package multimedia;

import com.github.sarxos.webcam.WebcamResolution;

import multimedia.MediaControl;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class EmbeddedMediaPlayer extends JFrame {

	private static final String MEDIA_URL = "http://localhost:5080/vod/streams/abc.mp4";

	public EmbeddedMediaPlayer(String str) {
		System.out.println("Called main of embedded media");
		setTitle("Varta Video Message");
		setLayout(new BorderLayout());
		System.out.println(str);

		Stage chatStage = new Stage();
		System.out.println(str);

		StackPane sp = new StackPane();

		System.out.println(str);
		// create media player
		Group root = new Group();
		Dimension size = WebcamResolution.VGA.getSize();

		Media media = new Media("file:///Users/aditya/Downloads/" + str
				+ ".mp4");
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);
		MediaControl mediaControl = new MediaControl(mediaPlayer);

		// Scene scene = new Scene(root, size.width, size.height);
		// scene.setRoot(mediaControl);
		sp.getChildren().add(mediaControl);
		Scene scene = new Scene(sp, size.getWidth(), size.getHeight());
		chatStage.setScene(scene);
		chatStage.show();
		// add(mediaControl);
		// chatStage.setScene(scene);
		// chatStage.show();

	}

	// @Override
	// public void start(Stage stage) {
	// stage.setTitle("Varta Video Message");
	// Group root = new Group();
	// Dimension size = WebcamResolution.VGA.getSize();
	// Scene scene = new Scene(root, size.width, size.height);
	//
	// // create media player
	// Media media = new Media(MEDIA_URL);
	// MediaPlayer mediaPlayer = new MediaPlayer(media);
	// mediaPlayer.setAutoPlay(true);
	// MediaControl mediaControl = new MediaControl(mediaPlayer);
	// scene.setRoot(mediaControl);
	//
	// // create mediaView and add media player to the viewer
	// // MediaView mediaView = new MediaView(mediaPlayer);
	// // ((Group)scene.getRoot()).getChildren().add(mediaView);
	// stage.setScene(scene);
	// stage.show();
	// }

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		new EmbeddedMediaPlayer("output3");
		// launch(args);
	}
}
