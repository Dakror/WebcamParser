package de.dakror.webcamparser;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.util.data.audio.AudioPlayer;
import net.sourceforge.tess4j.Tesseract;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.sarxos.webcam.Webcam;
import com.sun.glass.events.KeyEvent;

/**
 * @author Dakror
 */
public class WebcamParser {
	static BufferedImage image;
	
	static MaryInterface marytts;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		new File("img").mkdir();
		
		for (Logger l : Collections.<Logger> list(LogManager.getCurrentLoggers())) {
			l.setLevel(Level.OFF);
		}
		
		LogManager.getRootLogger().setLevel(Level.OFF);
		
		marytts = new LocalMaryInterface();
		
		Set<String> voices = marytts.getAvailableVoices();
		System.out.println(voices);
		marytts.setVoice("bits3-hsmm");
		
		JFrame f = new JFrame();
		
		JButton button = new JButton(new AbstractAction("Read aloud!") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					try {
						parse(image);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		button.setMnemonic(KeyEvent.VK_SPACE);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension size = new Dimension(1280, 720);
		
		f.add(button);
		f.setSize(size.width, size.height);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		
		Webcam webcam = Webcam.getDefault();
		webcam.setCustomViewSizes(new Dimension[] { size });
		webcam.setViewSize(webcam.getCustomViewSizes()[0]);
		
		webcam.open();
		
		while (true) {
			image = webcam.getImage();
			button.setIcon(new ImageIcon(image));
			button.setOpaque(true);
			f.pack();
		}
	}
	
	public static void parse(BufferedImage image) throws Exception {
		Tesseract tesseract = Tesseract.getInstance();
		tesseract.setLanguage("deu");
		
		// ImageIO.write(image, "PNG", new File("img/" + System.currentTimeMillis() + ".png"));
		
		String text = tesseract.doOCR(image).trim();
		
		text = text.replaceAll("[^\\w\n.,;!?\'\":»«„”\\(\\) ]", "");
		
		System.out.println(text);
		
		AudioInputStream audio = marytts.generateAudio(text.length() > 0 ? text : "Ich kann das leider nicht lesen.");
		
		AudioPlayer player = new AudioPlayer(audio);
		player.start();
		player.join();
	}
	
	public static BufferedImage horizontalflip(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
		g.dispose();
		return dimg;
	}
}
