package multimedia;

/**
 *
 * @author Siddhartha
 */
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import varta.RandomStringGenerator;
import varta.view.LoginController;

public class Encoder extends JFrame implements ActionListener, Runnable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Webcam webcam = null;
    File file = null;
    IMediaWriter writer = null;
    
    public  Encoder() {
              

    }
    
    
    
    public static void main(String[] args) throws Throwable {
        new Thread(new Encoder()).start();
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("send".equals(e.getActionCommand())) {
            if (webcam != null) {
                writer.close();
                System.out.println("Video recorded in file: " + file.getAbsolutePath());
            }
        } 
    }

	@Override
	public void run() {
		setTitle("Varta: Send Video Message");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        String	rand_str="";
    	try {
			rand_str =RandomStringGenerator.generateRandomString(10,RandomStringGenerator.Mode.ALPHANUMERIC);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        file = new File("/Users/aditya/Downloads/"+rand_str+".mp4");
        writer = ToolFactory.makeWriter(file.getAbsolutePath());
        Dimension size = WebcamResolution.VGA.getSize();
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, size.width, size.height);
        webcam = Webcam.getDefault();
        webcam.setViewSize(size);
        webcam.open(true);
        
        JPanel controls = new JPanel(new BorderLayout());
        JButton send;
        send = new JButton("send");
        send.setVerticalTextPosition(AbstractButton.CENTER);
        send.setHorizontalTextPosition(AbstractButton.CENTER);
        send.setActionCommand("send");
        send.addActionListener(this);
        controls.add(send);
 

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(false);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);
        //JFrame window = new JFrame("Varta: Send Video Message");
        add(panel, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        
        Thread thisThread = Thread.currentThread();
         try {
             thisThread.sleep(5000);
         }
         catch (Throwable t)
             {
             throw new OutOfMemoryError("An Error has occured");
         }
        
        long start = System.currentTimeMillis();
        for (int i = 0; i < 60; i++) {
            System.out.println("Capture frame " + i);
            BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
            IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
            frame.setKeyFrame(i == 0);
            frame.setQuality(0);
            writer.encodeVideo(0, frame);
            try {
                // 10 FPS
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Encoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                LoginController.client.connMessage(11,LoginController.client.getUsername(),LoginController.client.getChatController().getRec(),rand_str);

        writer.close();
        System.out.println("Video recorded in file: " + file.getAbsolutePath()); 
		
	}

   
}
