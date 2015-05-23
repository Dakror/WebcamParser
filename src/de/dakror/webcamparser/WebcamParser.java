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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.sun.glass.events.KeyEvent;

/**
 * @author Dakror
 */
public class WebcamParser {
	static BufferedImage image;
	
	public static void main(String[] args) throws Exception {
		new File("img").mkdir();
		
		Reader.init();
		
		JFrame f = new JFrame();
		
		JButton button = new JButton(new AbstractAction("Read aloud!") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					try {
						Reader.parse(image);
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
