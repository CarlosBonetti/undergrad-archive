package com.bonaguiar.formais1.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.CardLayout;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

public class TesteGUI extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TesteGUI frame = new TesteGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TesteGUI() {
		setTitle("Programa Tela Inicial");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 729, 645);
		
		JButton btAddGramatica = new JButton("Nova Gramática");
		btAddGramatica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new UiGramatica().setVisible(true);
				
			}
		});
		
		JButton btMinimizacao = new JButton("Minimização");
		
		JButton btAddEr = new JButton("Nova E.R.");
		btAddEr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showInputDialog(null, "Entre com a Expressão Regular: ");
			}
		});
		
		JButton btnDeterminizao = new JButton("Determinização");
		
		JButton btnComplementao = new JButton("Complementação");
		
		JButton btnInterseccao = new JButton("Intersecção");
		
		JTextPane textPane = new JTextPane();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(36)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(btMinimizacao)
								.addComponent(btnInterseccao, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnComplementao, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnDeterminizao, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(98)
							.addComponent(btAddGramatica)
							.addGap(43)
							.addComponent(btAddEr)))
					.addPreferredGap(ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
					.addComponent(textPane, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
					.addGap(26))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(textPane, GroupLayout.PREFERRED_SIZE, 545, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btAddGramatica)
								.addComponent(btAddEr))
							.addGap(88)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btMinimizacao)
								.addComponent(btnDeterminizao))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnComplementao)
								.addComponent(btnInterseccao))))
					.addContainerGap(27, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("New menu");
		menuBar.add(mnNewMenu);
	}
}
