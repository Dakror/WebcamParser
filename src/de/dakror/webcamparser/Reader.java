/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.webcamparser;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;
import net.sourceforge.tess4j.Tesseract;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author Maximilian Stark | Dakror
 */
public class Reader {
	static String language = "deu";
	static String voice = "bits3-hsmm";
	static MaryInterface marytts;
	
	static AudioPlayer player;
	
	@SuppressWarnings("unchecked")
	public static void init() {
		for (Logger l : Collections.<Logger> list(LogManager.getCurrentLoggers())) {
			l.setLevel(Level.OFF);
		}
		
		LogManager.getRootLogger().setLevel(Level.OFF);
		
		try {
			marytts = new LocalMaryInterface();
		} catch (MaryConfigurationException e) {
			e.printStackTrace();
		}
		
		Set<String> voices = marytts.getAvailableVoices();
		System.out.println(voices);
		marytts.setVoice(voice);
	}
	
	public static void parse(BufferedImage image) throws Exception {
		Tesseract tesseract = Tesseract.getInstance();
		tesseract.setLanguage(language);
		
		// ImageIO.write(image, "PNG", new File("img/" + System.currentTimeMillis() + ".png"));
		
		String text = tesseract.doOCR(image).trim();
		
		text = text.replaceAll("[^\\w\n.,;!?\'\":»«„”\\(\\) ]", "");
		
		System.out.println(text);
		
		read(text);
	}
	
	public static void read(String s) throws SynthesisException, InterruptedException {
		AudioInputStream audio = marytts.generateAudio(s.length() > 0 ? s : "Ich kann das leider nicht lesen.");
		
		player = new AudioPlayer(audio);
		player.start();
		player.join();
	}
}
