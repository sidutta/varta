package multimedia;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import varta.RandomStringGenerator;
import varta.view.LoginController;

public class WebcamViewerExample extends JFrame implements ActionListener, Runnable, WebcamListener, WindowListener, UncaughtExceptionHandler, ItemListener, WebcamDiscoveryListener {

    private static final long serialVersionUID = 1L;
    private Webcam webcam = null;
    private WebcamPanel panel = null;
    private WebcamPicker picker = null;

    @Override
    public void run() {
        Webcam.addDiscoveryListener(this);
        setTitle("Varta Snap Taker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        addWindowListener(this);
       
        JPanel controls = new JPanel(new BorderLayout());
        JButton capture;
        capture = new JButton("Capture");
        capture.setVerticalTextPosition(AbstractButton.CENTER);
        capture.setHorizontalTextPosition(AbstractButton.CENTER);
        capture.setActionCommand("capture");
        capture.addActionListener(this);
        controls.add(capture);
        
        picker = new WebcamPicker();
        picker.addItemListener(this);
        webcam = picker.getSelectedWebcam();
        if (webcam == null) {
            System.out.println("No webcams found...");
            System.exit(1);
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.addWebcamListener(WebcamViewerExample.this);
        panel = new WebcamPanel(webcam, false);
        panel.setFPSDisplayed(true);
        add(picker, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
        pack();
        setVisible(true);
        Thread t = new Thread() {
            @Override
            public void run() {
                panel.start();
            }
        };
        t.setName("varta-snap-taker");
        t.setDaemon(true);
        t.setUncaughtExceptionHandler(this);
        t.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new WebcamViewerExample());
    }
    
    public void actionPerformed(ActionEvent e) {
        if ("capture".equals(e.getActionCommand())) {
            try {
                if (webcam != null) {
                String	rand_str="";
                	try {
    					rand_str =RandomStringGenerator.generateRandomString(10,RandomStringGenerator.Mode.ALPHANUMERIC);
    				} catch (Exception e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
                    ImageIO.write(webcam.getImage(), "PNG", new File("/Users/Siddhartha/NetBeansProjects/"+rand_str+".png"));
                    LoginController.client.connMessage(10,LoginController.client.getUsername(),LoginController.client.getChatController().getRec(),rand_str);
                }
            } catch (IOException ex) {
                Logger.getLogger(WebcamViewerExample.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }

    @Override
    public void webcamOpen(WebcamEvent we) {
        System.out.println("webcam open");
    }

    @Override
    public void webcamClosed(WebcamEvent we) {
        System.out.println("webcam closed");
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {
        System.out.println("webcam disposed");
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {
// do nothing
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        webcam.close();
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("webcam viewer resumed");
        panel.resume();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("webcam viewer paused");
        panel.pause();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.err.println(String.format("Exception in thread %s", t.getName()));
        e.printStackTrace();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() != webcam) {
            if (webcam != null) {
                panel.stop();
                remove(panel);
                webcam.removeWebcamListener(this);
                webcam.close();
                webcam = (Webcam) e.getItem();
                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.addWebcamListener(this);
                System.out.println("selected " + webcam.getName());
                panel = new WebcamPanel(webcam, false);
                panel.setFPSDisplayed(true);
                add(panel, BorderLayout.CENTER);
                pack();
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        panel.start();
                    }
                };
                t.setName("example-stoper");
                t.setDaemon(true);
                t.setUncaughtExceptionHandler(this);
                t.start();
            }
        }
    }

    @Override
    public void webcamFound(WebcamDiscoveryEvent event) {
        if (picker != null) {
            picker.addItem(event.getWebcam());
        }
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent event) {
        if (picker != null) {
            picker.removeItem(event.getWebcam());
        }
    }
}
