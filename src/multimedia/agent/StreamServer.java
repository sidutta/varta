package multimedia.agent;

import java.awt.Dimension;
import java.net.InetSocketAddress;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import javax.swing.JFrame;
import varta.view.ChatController;

public class StreamServer  implements Runnable {
    
    public StreamServer() {
        
    }

    public static void main(String[] args) {
        Webcam.setAutoOpenMode(true);
        Webcam webcam = Webcam.getDefault();
        Dimension dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);
//webcam.open();
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(false);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);
        JFrame window = new JFrame("Varta: My Webcam");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);

        StreamServerAgent serverAgent = new StreamServerAgent(webcam, dimension);
        serverAgent.start(new InetSocketAddress("localhost", 20000));
    }

    @Override
    public void run() {
        Webcam.setAutoOpenMode(true);
        Webcam webcam = Webcam.getDefault();
        Dimension dimension = new Dimension(320, 240);
        webcam.setViewSize(dimension);
//webcam.open();
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(false);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);
        JFrame window = new JFrame("Varta: My Webcam");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);

        StreamServerAgent serverAgent = new StreamServerAgent(webcam, dimension);
        serverAgent.start(new InetSocketAddress(ChatController.ip, ChatController.port));

    }

}
