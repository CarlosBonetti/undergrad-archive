package com.bonaguiar.formais1.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.JFormattedTextField;

import java.awt.Label;

import javax.swing.JPanel;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import javax.swing.JTextArea;

public class UiGramatica extends JFrame {

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					UiGramatica frame = new UiGramatica();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public UiGramatica() {
		setTitle("Adiconar Gramatica");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 511, 413);
		toFront();
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 483, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(24, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
					.addGap(24))
		);
		panel.setLayout(null);
		
		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.setBounds(204, 312, 78, 25);
		panel.add(btnSalvar);
		
		final JTextArea textArea = new JTextArea();
		textArea.setBounds(12, 12, 459, 277);
		panel.add(textArea);
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nomeGram = JOptionPane.showInputDialog("Digite um nome para a gramatica:");
				while (nomeGram.trim().isEmpty()) {
					nomeGram = JOptionPane.showInputDialog("Campo obrigatorio.!\nDigite um nome para a gramatica:");
				}
				textArea.getLineCount();
				System.out.println(textArea.getLineCount() + " -> nome");
					System.out.println("---");
				dispose();
			}
		});
		getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("New menu");
		menuBar.add(mnNewMenu);
	}
}
