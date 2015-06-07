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

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 * @author Maximilian Stark | Dakror
 */
public class EBookReader {
	static int start;
	
	static int page;
	
	public static void main(String[] args) throws Exception {
		Reader.language = "eng";
		Reader.voice = "cmu-rms-hsmm";
		
		Reader.init();
		
		PdfReader reader = new PdfReader("Midnight_Tides.pdf");
		
		start = 7;
		page = start;
		
		JFrame frame = new JFrame("Menu");
		
		Container panel = frame.getContentPane();
		panel.setLayout(new GridLayout(3, 1));
		
		panel.add(new JButton(new AbstractAction("Prev") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				page -= 2;
				Reader.player.cancel();
			}
		}));
		panel.add(new JButton(new AbstractAction("Next") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Reader.player != null) Reader.player.cancel();
			}
		}));
		panel.add(new JButton(new AbstractAction("Go to page") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				page = Integer.parseInt(JOptionPane.showInputDialog("page number (start at 1):")) - 1;
				if (Reader.player != null) Reader.player.cancel();
			}
		}));
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		for (; page <= reader.getNumberOfPages(); page++) {
			String s = PdfTextExtractor.getTextFromPage(reader, page);
			s = s.replace("—", ", ").replace(" - ", ", ").replace("Andü", "Andii");
			System.out.println();
			System.out.println("############## - Page " + page + " - ##############");
			System.out.println();
			System.out.println(s);
			
			Reader.read(s);
		}
	}
}
