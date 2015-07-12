package com.bonaguiar.formais2.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.bonaguiar.formais2.core.GLC;
import com.bonaguiar.formais2.core.ParserGenerator;
import java.awt.Color;
import java.awt.Font;

public class AppParse extends JFrame {

	private JPanel contentPane;
	private App frame;
	private GLC glc;
	public ParserGenerator parser;
	private JTextField textField;
	private JTextArea textArea_Analisado ;
	
	/**
	 * Create the frame.
	 * @param frame 
	 * @param parserFrame 
	 * @throws Exception 
	 */
	public AppParse(App frame, ParserGenerator parserFrame) throws Exception {
		setResizable(false);
		setBackground(new Color(169, 169, 169));
		this.frame = frame;
		this.glc = frame.getGramHash().get(frame.getGlcSelecionado());
		this.parser = parserFrame;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 884, 574);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(220, 220, 220));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JMenuBar menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		JLabel lblSentenca = new JLabel("Entre com a Sentença:");
		
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Dialog", Font.BOLD, 12));
		textArea.setBackground(new Color(255, 235, 205));
		JScrollPane jp = new JScrollPane(textArea);
		textArea.setEditable(false);
		textArea.setText(parserFrame.getParser().toString());
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Analisar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String parse = "";
				try {
					parse = parser.getParser().run(textField.getText().trim()).replace(" ", "\n");
					
					textArea_Analisado.setText(parse);
					
				} catch (Throwable e) {
					textArea_Analisado.setText(parse+"\nNão deu...");
					e.printStackTrace();
				}
			}
		});
		
		textArea_Analisado = new JTextArea();
		JScrollPane jp2 = new JScrollPane(textArea_Analisado);
		textArea_Analisado.setEditable(false);
		
		JLabel lblNewLabel = new JLabel("Sequência:");
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(21)
					.addComponent(jp, GroupLayout.PREFERRED_SIZE, 667, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
									.addComponent(textField, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
									.addComponent(lblSentenca)))
							.addGroup(gl_panel.createSequentialGroup()
								.addGap(30)
								.addComponent(btnNewButton)))
						.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(jp2, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(jp, GroupLayout.PREFERRED_SIZE, 490, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(13, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(34)
									.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblSentenca))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton)
							.addPreferredGap(ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
							.addComponent(lblNewLabel)
							.addGap(14)
							.addComponent(jp2, GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
							.addGap(38))))
		);
		panel.setLayout(gl_panel);
	}
}
