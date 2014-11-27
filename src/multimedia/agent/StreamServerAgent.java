package multimedia.agent;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import multimedia.channel.StreamServerChannelPipelineFactory;
import multimedia.handler.H264StreamEncoder;
import multimedia.handler.StreamServerListener;

import com.github.sarxos.webcam.Webcam;

public class StreamServerAgent implements IStreamServerAgent {

    protected final static Logger logger = LoggerFactory.getLogger(StreamServer.class);
    protected final Webcam webcam;
    protected final Dimension dimension;
    protected final static ChannelGroup channelGroup = new DefaultChannelGroup();
    protected final ServerBootstrap serverBootstrap;
    //I just move the stream encoder out of the channel pipeline for the performance
    protected final H264StreamEncoder h264StreamEncoder;
    protected final H264StreamEncoder secondEncoder;
    protected volatile boolean isStreaming;
    protected ScheduledExecutorService timeWorker;
    protected ExecutorService encodeWorker;
    protected int FPS = 25;
    protected ScheduledFuture<?> imageGrabTaskFuture;
    protected TargetDataLine line;

    public StreamServerAgent(Webcam webcam, Dimension dimension) {
        super();
        this.webcam = webcam;
        this.dimension = dimension;
        //this.h264StreamEncoder = new H264StreamEncoder(dimension,false);
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.setFactory(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        this.serverBootstrap.setPipelineFactory(new StreamServerChannelPipelineFactory(
                new StreamServerListenerIMPL(),
                dimension));
        this.timeWorker = new ScheduledThreadPoolExecutor(1);
        this.encodeWorker = Executors.newSingleThreadExecutor();
        this.h264StreamEncoder = new H264StreamEncoder(dimension, false);
        this.secondEncoder = new H264StreamEncoder(dimension, false);

        AudioFormat format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                44100.0F, 16, 2, 4, 44100, false);

        this.line = null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                format); // format is an AudioFormat object
        if (!AudioSystem.isLineSupported(info)) {
            // Handle the error ... 
        }
        // Obtain and open the line.
        try {

            this.line = (TargetDataLine) AudioSystem.getLine(info);
            this.line.open(format);
        } catch (LineUnavailableException ex) {
            // Handle the error ... 
        }

        this.line.start();
    }

    public int getFPS() {
        return FPS;
    }

    public void setFPS(int fPS) {
        FPS = fPS;
    }

    @Override
    public void start(SocketAddress streamAddress) {
        logger.info("Server started :{}", streamAddress);
        Channel channel = serverBootstrap.bind(streamAddress);
        channelGroup.add(channel);
    }

    @Override
    public void stop() {
        logger.info("server is stoping");
        channelGroup.close();
        timeWorker.shutdown();
        encodeWorker.shutdown();
        serverBootstrap.releaseExternalResources();
    }

    private static void writeData(Object data) {
        channelGroup.write(data);
        /*
         try {
         Thread.sleep(10);
         } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }
         */
    }

    private class StreamServerListenerIMPL implements StreamServerListener {

        @Override
        public void onClientConnectedIn(Channel channel) {
			//here we just start to stream when the first client connected in
            //
            channelGroup.add(channel);
            if (!isStreaming) {
                //do some thing
                Runnable imageGrabTask = new ImageGrabTask();
                ScheduledFuture<?> imageGrabFuture
                        = timeWorker.scheduleWithFixedDelay(imageGrabTask,
                                0,
                                1000 / FPS,
                                TimeUnit.MILLISECONDS);
                imageGrabTaskFuture = imageGrabFuture;
                AudioGrabTask at = new AudioGrabTask();
                at.start();
                isStreaming = true;
            }
            logger.info("current connected clients :{}", channelGroup.size());
        }

        @Override
        public void onClientDisconnected(Channel channel) {
            channelGroup.remove(channel);
            int size = channelGroup.size();
            logger.info("current connected clients :{}", size);
            if (size == 1) {
                //cancel the task
                imageGrabTaskFuture.cancel(false);
                webcam.close();
                isStreaming = false;
            }
        }

        @Override
        public void onExcaption(Channel channel, Throwable t) {
            channelGroup.remove(channel);
            channel.close();
            int size = channelGroup.size();
            logger.info("current connected clients :{}", size);
            if (size == 1) {
                //cancel the task
                imageGrabTaskFuture.cancel(false);
                webcam.close();
                isStreaming = false;

            }

        }

        protected volatile long frameCount = 0;

        private class ImageGrabTask implements Runnable {

            @Override
            public void run() {
                logger.info("image grabed ,count :{}", frameCount++);
                BufferedImage bufferedImage = webcam.getImage();
                /**
                 * using this when the h264 encoder is added to the pipeline
			 *
                 */
                //channelGroup.write(bufferedImage);
                /**
                 * using this when the h264 encoder is inside this class
			 *
                 */
                encodeWorker.execute(new EncodeTask(bufferedImage));
            }

        }

        public class AudioGrabTask extends Thread {

            public void run() {
                int numBytesRead;
                byte[] data = new byte[4096];
                while (true) {
                    if (data.length > 0) {
                        System.out.println("start read: " + System.currentTimeMillis());
                        numBytesRead = line.read(data, 0, 4096);
                        System.out.println("end read: " + System.currentTimeMillis());
                        Object msg2 = null;
                        try {
                            if (numBytesRead > 0) {
                                msg2 = secondEncoder.encode(data, numBytesRead);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (msg2 != null) {
                            System.out.println("writing audio");
                            writeData(msg2);
                        }
                        Thread.yield();
                    }
                }
            }
        }

        private class EncodeTask implements Runnable {

            private final BufferedImage image;

            public EncodeTask(BufferedImage image) {
                super();
                this.image = image;
            }

            @Override
            public void run() {
                try {
                    Object msg = h264StreamEncoder.encode(image);
                    if (msg != null) {
                        writeData(msg);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

}
