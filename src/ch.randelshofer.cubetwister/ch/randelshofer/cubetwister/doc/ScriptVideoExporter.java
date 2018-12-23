/**
 * @(#)ScriptVideoExporter.java  2.1  2011-04-20
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.*;
import org.monte.media.av.MovieWriter;
import org.monte.media.player.SynchronousAnimator;
import org.monte.media.av.Format;
import static org.monte.media.av.codec.video.VideoFormatKeys.*;
import org.monte.media.avi.AVIWriter;
import org.monte.media.imgseq.ImageSequenceWriter;
import org.monte.media.quicktime.QuickTimeWriter;
import ch.randelshofer.rubik.*;
import ch.randelshofer.rubik.parser.*;
import idx3d.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import org.monte.media.av.Buffer;
import org.monte.media.math.Rational;

/**
 * ScriptVideoExporter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>2.0 2010-10-06 Uses QuickTimeWriter instead of QuickTimeOutputStream.
 * <br>1.0 Apr 28, 2008 Created.
 */
public class ScriptVideoExporter {

    public static enum WriterType {
        AVI,QUICKTIME,FILE
    }
    public static enum FileFormat {
        QUICKTIME_PNG(QuickTimeWriter.VIDEO_PNG,WriterType.QUICKTIME,".mov"),
        QUICKTIME_TSCC(AVIWriter.VIDEO_SCREEN_CAPTURE,WriterType.QUICKTIME,".mov"),
        QUICKTIME_JPG(QuickTimeWriter.VIDEO_JPEG,WriterType.QUICKTIME,".mov"),
        QUICKTIME_ANIMATION(QuickTimeWriter.VIDEO_ANIMATION,WriterType.QUICKTIME,".mov"),
        AVI_TSCC(AVIWriter.VIDEO_SCREEN_CAPTURE,WriterType.AVI,".avi"),
        AVI_JPG(AVIWriter.VIDEO_JPEG,WriterType.AVI,".avi"),
        PNG_STILLS(AVIWriter.VIDEO_PNG,WriterType.FILE,".png"),
        JPG_STILLS(AVIWriter.VIDEO_JPEG,WriterType.FILE,".jpg");
        //
        Format fmt;
        WriterType type;
        String extension;

        FileFormat(Format fmt, WriterType type, String ext) {
            this.fmt=fmt;
            this.type=type;
            this.extension=ext;
        }

    }
    private ProgressObserver progress;
    private ScriptModel model;
    private Dimension size;
    private int x;
    private int y;
    private double fps;
    private double quality;
    private FileFormat format;
    private File file;
    private String cameraName;
    private int firstFrame;
    private int lastFrame;
    private MovieWriter w;
    private Rational duration;
    /**
     * The 3d Scene.
     */
    private idx3d_Scene scene;
    /**
     * The 3d Render Pipeline.
     */
    private idx3d_RenderPipeline renderPipeline;

    public ScriptVideoExporter(ScriptModel model, File file, ProgressObserver progress) {
        this.progress = progress;
        this.model = model;
        this.size = new Dimension(1024, 768);
        this.fps = 30;
        this.file = file;
        this.cameraName = "Front";
        this.firstFrame = 1;
        this.lastFrame = Integer.MAX_VALUE;
        this.format = FileFormat.QUICKTIME_PNG;
    }

    public void setFramesPerSecond(double fps) {
        this.fps = fps;
    }

    public void setVideoWidth(int newValue) {
        size.width = newValue;
    }

    public int getVideoWidth() {
        return size.width;
    }

    public void setVideoHeight(int newValue) {
        size.height = newValue;
    }

    public int getVideoHeight() {
        return size.height;
    }

    public double getFramesPerSecond() {
        return fps;
    }

    public void setQuality(double newValue) {
        this.quality = newValue;
    }

    public double getQuality() {
        return quality;
    }

    public void setFPS(double newValue) {
        this.fps = newValue;
    }

    public double getFPS() {
        return fps;
    }

    public void setFormat(FileFormat newValue) {
        this.format = newValue;
    }

    public FileFormat getFormat() {
        return format;
    }

    public void setFirstFrame(int newValue) {
        firstFrame = newValue;
    }

    public void setLastFrame(int newValue) {
        lastFrame = newValue;
    }

    public void start() throws IOException {
        progress.setNote("Processing...");
        switch (format.type) {
            case FILE:
                if (!file.getName().toLowerCase().endsWith(format.extension)) {
                    file = new File(file.getPath() + "%04d"+format.extension);
                } else {
                    file = new File(file.getPath().substring(0,file.getPath().length()-format.extension.length()) + "%04d"+format.extension);
                }
                break;
            default:
                if (!file.getName().toLowerCase().endsWith(format.extension)) {
                    file = new File(file.getPath() + format.extension);
                }
                break;
        }
        openExportStream();
        try {
            SequenceNode script = model.getParsedScript();
            ScriptPlayer player = new ScriptPlayer();

            Cube3D cube3D = null;
            try {
                cube3D = (Cube3D) model.getCubeModel().getCube3DClass().newInstance();
            } catch (InstantiationException ex) {
                throw new IOException("Couldn't instantiate cube 3D");
            } catch (IllegalAccessException ex) {
                throw new IOException("Couldn't instantiate cube 3D");
            }
            if (cube3D == null) {
                return;
            }

            cube3D.setAttributes(model.getCubeModel());
            player.setCube3D(cube3D);
            player.setCube(cube3D.getCube());
            if (model.isGenerator()) {
                player.setResetCube(null);
            } else {
                ch.randelshofer.rubik.Cube resetCube = (ch.randelshofer.rubik.Cube) model.getCube().clone();
                resetCube.reset();
                script.applyTo(resetCube, true);
                player.setResetCube(resetCube);
            }
            player.setScript(script);
            player.reset();

            progress.setMaximum(player.getTimeModel().getMaximum() + 1);
            SynchronousAnimator animator = new SynchronousAnimator();
            cube3D.setAnimator(animator);
            cube3D.setAnimated(true);

            BoundedRangeModel playerTime = player.getTimeModel();

            JPanel repaintDiscarder = new JPanel();
            scene = (idx3d_Scene) cube3D.getScene();
            Object lock = cube3D.getLock();
            renderPipeline = new idx3d_RenderPipeline(scene, size.width, size.height);
            renderPipeline.useIdBuffer(false);
            renderPipeline.setAntialias(true);

            int count = 0;
            long time = 0;
            double doubleTime = 0d;
            playerTime.setValue(0);
            BufferedImage buf = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();
            renderPipeline.render((cameraName != null) ? scene.camera(cameraName) : scene.getDefaultCamera());
            g.drawImage(renderPipeline.getImage(), x, y, repaintDiscarder);
            writeFrame(buf, count++);
            while (playerTime.getValue() < playerTime.getMaximum() && !progress.isCanceled()) {
                playerTime.setValue(playerTime.getValue() + 1);
                progress.setProgress(playerTime.getValue());

                while (animator.isActive() && !progress.isCanceled()) {
                    doubleTime += 1000d / fps;
                    time = (long) doubleTime;
                    animator.setTime(time);
                    animator.animateStep();

                    if (count >= firstFrame && count <= lastFrame) {
                        progress.setNote("Writing frame " + (count));
                        renderPipeline.render((cameraName != null) ? scene.camera(cameraName) : scene.getDefaultCamera());
                        g.drawImage(renderPipeline.getImage(), x, y, repaintDiscarder);
                        writeFrame(buf, count);
                    }
                    count++;
                }
            }
        } catch (Error t) {
            System.err.println("ScriptVideoExporter catch and rethrow");
            throw t;
        } finally {
            progress.setNote("Cleaning up");
            closeExportStream();
            progress.setProgress(progress.getMaximum());
        }
    }

    private void openExportStream() throws IOException {
        duration =  Rational.valueOf(fps).inverse();
        switch(format.type) {
            case AVI:
                w = new AVIWriter(file);
                w.addTrack(
                        new Format(FrameRateKey, Rational.valueOf(fps),
                                WidthKey,size.width,HeightKey,size.height,
                                DepthKey,24).append(format.fmt));
                /*
                ((AVIWriter) w).addVideoTrack(format.fmt,//
                        aviTimeScale, aviFrameRate, size.width, size.height);*/
                break;
            case QUICKTIME:
                w = new QuickTimeWriter(file);
                w.addTrack(
                        new Format(FrameRateKey, Rational.valueOf(fps),
                                WidthKey,size.width,HeightKey,size.height,
                                DepthKey,24).append(format.fmt));
                /*((QuickTimeWriter) w).addVideoTrack(format.fmt,//
                        timeScale, size.width, size.height);*/
                break;
            case FILE:
                w = new ImageSequenceWriter();
                ((ImageSequenceWriter) w).addVideoTrack(
                        file.getParentFile(), file.getName(), size.width, size.height);
                break;
        }
    }

    private void closeExportStream() throws IOException {
        w.close();
    }

    private void writeFrame(BufferedImage image, int count) throws IOException {
        Buffer buf=new Buffer();
        buf.format=new Format(EncodingKey,ENCODING_BUFFERED_IMAGE,DataClassKey,BufferedImage.class);
        buf.sampleDuration=duration;
        buf.data=image;
        // XXX - Specify duration
        //buf.sampleDuration=duration;
        w.write(0,buf);
        //        w.writeFrame(0, new Buffer(image, duration);
    }
/*
    private File getFrameFile(int count) {
        String prefix = Integer.toString(count);
        if (prefix.length() < 6) {
            prefix = ("000000" + prefix);
            prefix = prefix.substring(prefix.length() - 6, prefix.length());
        }

//        File dir = new File(file.getParent(), "seq" + Integer.toString(count / 10000));
        File dir = new File(file.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File ff = new File(dir, prefix + " " + file.getName());
        return ff;
    }
 *
 */
}
