package com.bonaguiar.formais1.gui;

import java.awt.EventQueue;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.bonaguiar.formais1.core.expr.ExprRegular;
import com.bonaguiar.formais1.core.grammar.GRParser;
import com.bonaguiar.formais1.core.grammar.GramaticaRegular;

import javax.swing.ListModel;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JMenuItem;

public class App extends JFrame {
	private HashMap<String, ExprRegular> expRegHash = new HashMap<String, ExprRegular>();
	private HashMap<String, GramaticaRegular> gramHash = new HashMap<String, GramaticaRegular>();
	private DefaultListModel<String> modeloER = new DefaultListModel<String>();
	private DefaultListModel<String> modeloGR = new DefaultListModel<String>();
	private JList<String> listagem;

	private void adicionaNaListaER(String nome) {
		modeloER.addElement(nome);
	}

	private void adicionarGramatica() {
		String gramatica;
		JTextArea area = new JTextArea("", 20, 15);
		int botaoOk = JOptionPane.showConfirmDialog(null,
				new JScrollPane(area), "Gramática",
				JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			gramatica = area.getText();
			System.out.println(gramatica);

			if (gramatica.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null,
						"Campo texto não pode estar vazio.\nTente novamente.");
			} else {
				try {
					GramaticaRegular gr = GRParser.parse(gramatica);

					String nomeGram = JOptionPane
							.showInputDialog("Digite um nome para a gramática:");
					while (nomeGram.trim().isEmpty()) {
						nomeGram = JOptionPane
								.showInputDialog("Campo obrigatório.!\nDigite um nome para a gramática:");
					}

					gramHash.put(nomeGram, gr);
					modeloGR.addElement(nomeGram);

				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, e2.getMessage()
							+ "\nTente novamente.");
				}
			}
		}
	}

	public void adicionarER(){
		String nomeExpReg;
		String expReg = JOptionPane.showInputDialog(null,
				"Entre com a Expressão Regular: ");
		try {
			while (expReg.trim().isEmpty()) {
				expReg = JOptionPane
						.showInputDialog(null,
								"Campo obrigatorio.!\nEntre com a Expressão Regular: ");
			}
			nomeExpReg = JOptionPane.showInputDialog(null,
					"Entre com um nome para a Expressão Regular: ");
			while (nomeExpReg.trim().isEmpty()) {
				nomeExpReg = JOptionPane
						.showInputDialog(null,
								"Campo obrigatorio.!\nEntre com um nome para a Expressão Regular: ");
			}
			expRegHash.put(nomeExpReg, new ExprRegular(expReg));
			adicionaNaListaER(nomeExpReg);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Entrada inválida. Tente novamente");
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
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
	public App() {
		listagem = new JList<String>(modeloER);

		setTitle("Programa Tela Inicial");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 606, 512);

		JButton btAddGramatica = new JButton("Nova Gramática");
		btAddGramatica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adicionarGramatica();
			}
		});

		JButton btAddEr = new JButton("Nova E.R.");
		btAddEr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				adicionarER();
			}
		});

		listagem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList list = (JList) e.getSource();
				if (e.getClickCount() == 2) {
					System.out.println("Clicked twice - "
							+ expRegHash
									.get(list.getSelectedValue().toString())
									.getExpr());
				}
			}
		});
		listagem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JList<String> list = new JList<String>(modeloGR);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblGramar = new JLabel("Gramaticas:");

		JLabel lblEr = new JLabel("ER:");

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(49)
					.addComponent(list, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
					.addComponent(listagem, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
					.addGap(62))
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(110)
							.addComponent(lblGramar, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
							.addComponent(lblEr, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(98)
							.addComponent(btAddGramatica)
							.addPreferredGap(ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
							.addComponent(btAddEr)))
					.addGap(168))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btAddGramatica)
						.addComponent(btAddEr))
					.addGap(91)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGramar)
						.addComponent(lblEr))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(listagem, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE)
						.addComponent(list, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(52, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuArquivo = new JMenu("Arquivo");
		menuBar.add(menuArquivo);
		
		JMenuItem mntmSair = new JMenuItem("Sair");
		mntmSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(EXIT_ON_CLOSE);
			}
		});
		menuArquivo.add(mntmSair);
	}
}
