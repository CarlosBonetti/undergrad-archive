package com.bonaguiar.formais1.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;

import lombok.Getter;

import com.bonaguiar.formais1.core.exception.FormaisException;
import com.bonaguiar.formais1.core.expr.ExprRegular;
import com.bonaguiar.formais1.core.grammar.GRParser;
import com.bonaguiar.formais1.core.grammar.GramaticaRegular;
import com.bonaguiar.formais1.persistence.ERBase;
import com.bonaguiar.formais1.persistence.GRBase;

public class App extends JFrame {
	@Getter
	private HashMap<String, ExprRegular> expRegHash = new HashMap<String, ExprRegular>();
	@Getter
	private HashMap<String, GramaticaRegular> gramHash = new HashMap<String, GramaticaRegular>();
	private DefaultListModel<String> modeloER = new DefaultListModel<String>();
	private DefaultListModel<String> modeloGR = new DefaultListModel<String>();
	private JList<String> listagemEr;
	private JList<String> listagemGr;
	private String grSelecionado;
	private String erSelecionado;
	private ERBase erBase = new ERBase();
	private GRBase grBase = new GRBase();

	private void setGrSelecionado(String selecao) {
		grSelecionado = selecao;
	}

	private void setErSelecionado(String selecao) {
		erSelecionado = selecao;
	}

	private boolean ehChaveERNova(String chave) {
		return expRegHash.containsKey(chave);
	}

	private boolean ehChaveGRNova(String chave) {
		return gramHash.containsKey(chave);
	}

	private void tratarException(Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, e.getMessage());
	}

	private void persistER() {
		try {
			erBase.save(expRegHash);
		} catch (Exception e) {
			tratarException(e);
		}
	}

	private void persistGR() {
		try {
			grBase.save(gramHash);
		} catch (Exception e) {
			tratarException(e);
		}
	}

	private void adicionaNaListaER(String nome, ExprRegular er) {
		expRegHash.put(nome, er);
		modeloER.addElement(nome);
		persistER();
	}

	private void adicionaNaListaGR(String nome, GramaticaRegular gr) {
		gramHash.put(nome, gr);
		modeloGR.addElement(nome);
		persistGR();
	}

	private void editarNaListaER(String nome, ExprRegular er) {
		expRegHash.put(nome, er);
		persistER();
	}

	private void editarNaListaGR(String nome, GramaticaRegular gr) {
		gramHash.put(nome, gr);
		persistGR();
	}

	private void removeDaListaGR(String nome) {
		gramHash.remove(nome);
		modeloGR.removeElement(nome);
		persistGR();
	}

	private void removeDaListaER(String nome) {
		expRegHash.remove(nome);
		modeloER.removeElement(nome);
		persistER();
	}

	private void adicionarGramatica() {
		String gramatica;
		JTextArea area = new JTextArea("", 20, 15);
		int botaoOk = JOptionPane.showConfirmDialog(null, new JScrollPane(area), "Gramática", JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			gramatica = area.getText();
			System.out.println(gramatica);

			if (gramatica.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Campo texto não pode estar vazio.\nTente novamente.");
			} else {
				try {
					GramaticaRegular gr = GRParser.parse(gramatica);

					String nomeGram = JOptionPane.showInputDialog("Digite um nome para a gramática:");
					while (nomeGram.trim().isEmpty() | ehChaveGRNova(nomeGram)) {
						nomeGram = JOptionPane.showInputDialog("Campo obrigatório e único.!\nDigite um nome para a gramática:");
					}
					adicionaNaListaGR(nomeGram, gr);

				} catch (Exception e2) {
					tratarException(e2);
				}
			}
		}
	}

	public void adicionarER() {
		String nomeExpReg;
		String expReg = JOptionPane.showInputDialog(null, "Entre com a Expressão Regular: ");
		try {
			while (expReg.trim().isEmpty()) {
				expReg = JOptionPane.showInputDialog(null, "Campo obrigatorio.!\nEntre com a Expressão Regular: ");
			}
			ExprRegular er = new ExprRegular(expReg);

			nomeExpReg = JOptionPane.showInputDialog(null, "Entre com um nome para a Expressão Regular: ");
			while (nomeExpReg.trim().isEmpty() | ehChaveERNova(nomeExpReg)) {
				nomeExpReg = JOptionPane.showInputDialog(null, "Campo obrigatorio.!\nEntre com um nome para a Expressão Regular: ");
			}
			adicionaNaListaER(nomeExpReg, er);

		} catch (Exception e) {
			tratarException(e);
		}
	}

	private void editarGramatica(String chave) {
		String gramatica;
		JTextArea area = new JTextArea(gramHash.get(chave).getGramaticaPura(), 20, 15);
		int botaoOk = JOptionPane.showConfirmDialog(null, new JScrollPane(area), "Gramática", JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			gramatica = area.getText();

			if (gramatica.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Campo texto não pode estar vazio.\nTente novamente.");
			} else {
				try {
					GramaticaRegular gr = GRParser.parse(gramatica);
					editarNaListaGR(chave, gr);
				} catch (Exception e2) {
					tratarException(e2);
				}
			}
		}
	}

	public void editarER(String chave) {
		String expReg = JOptionPane.showInputDialog(null, "Editar a Expressão Regular: ", expRegHash.get(chave).getExpr());
		while (expReg.trim().isEmpty()) {
			expReg = JOptionPane.showInputDialog(null, "Campo obrigatorio.!\nEntre com a Expressão Regular: ");
		}
		try {
			ExprRegular er = new ExprRegular(expReg);
			editarNaListaER(chave, er);
		} catch (Exception e) {
			tratarException(e);
		}
	}

	static App frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new App();
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
		loadPersistence();
		listagemEr = new JList<String>(modeloER);

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

		listagemEr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList list = (JList) e.getSource();
				String valorSelecao = list.getSelectedValue().toString();
				setErSelecionado(valorSelecao);
				if (e.getClickCount() == 2) {
					try {
						ViewAF view = new ViewAF(expRegHash.get(valorSelecao).getAFD(), frame);
						view.setVisible(true);
						view.setTitle(valorSelecao);
					} catch (FormaisException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		listagemEr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listagemGr = new JList<String>(modeloGR);
		listagemGr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList list = (JList) e.getSource();
				String valorSelecao = list.getSelectedValue().toString();
				setGrSelecionado(valorSelecao);
				if (e.getClickCount() == 2) {
					try {
						ViewAF view = new ViewAF(gramHash.get(valorSelecao).getAutomatoFinito(), frame);
						view.setVisible(true);
						view.setTitle(valorSelecao);
					} catch (FormaisException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		listagemGr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblGramar = new JLabel("Gramaticas:");

		JLabel lblEr = new JLabel("ER:");

		JButton btnEditarGramtica = new JButton("Editar Gramática");
		btnEditarGramtica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editarGramatica(grSelecionado);
			}
		});

		JButton btnExcluirGramtica = new JButton("Excluir Gramática");
		btnExcluirGramtica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!modeloGR.isEmpty()) {
					int opcao = JOptionPane.showConfirmDialog(null, "Confirmar exclusão de " + grSelecionado, null, JOptionPane.YES_NO_OPTION);
					if (JOptionPane.YES_OPTION == opcao) {
						removeDaListaGR(grSelecionado);
					}
				}
			}
		});

		JButton btnEditarEr = new JButton("Editar E.R.");
		btnEditarEr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editarER(erSelecionado);
			}
		});

		JButton btnExcluirEr = new JButton("Excluir E.R.");
		btnExcluirEr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!modeloER.isEmpty()) {
					int opcao = JOptionPane.showConfirmDialog(null, "Confirmar exclusão de " + erSelecionado, null, JOptionPane.YES_NO_OPTION);
					if (JOptionPane.YES_OPTION == opcao) {
						removeDaListaER(erSelecionado);
					}
				}
			}
		});

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout
				.createParallelGroup(Alignment.TRAILING)
				.addGroup(
						groupLayout.createSequentialGroup().addGap(49).addComponent(listagemGr, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
						.addComponent(listagemEr, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE).addGap(62))
						.addGroup(
								groupLayout
								.createSequentialGroup()
								.addGroup(
										groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												groupLayout.createSequentialGroup().addGap(110)
												.addComponent(lblGramar, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
												.addComponent(lblEr, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
												.addGroup(
														groupLayout
														.createSequentialGroup()
														.addGap(98)
														.addGroup(
																groupLayout
																.createParallelGroup(Alignment.LEADING)
																.addGroup(
																		groupLayout
																		.createParallelGroup(Alignment.TRAILING, false)
																		.addComponent(btAddGramatica, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																				.addComponent(btnEditarGramtica, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
																						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
																						.addComponent(btnExcluirGramtica))
																						.addPreferredGap(ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
																						.addGroup(
																								groupLayout
																								.createParallelGroup(Alignment.TRAILING, false)
																								.addComponent(btAddEr, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
																										Short.MAX_VALUE)
																										.addComponent(btnEditarEr, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																										.addComponent(btnExcluirEr, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
																										.addPreferredGap(ComponentPlacement.RELATED))).addGap(168)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
				.createSequentialGroup()
				.addGap(24)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btAddGramatica).addComponent(btAddEr))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(btnEditarGramtica).addComponent(btnEditarEr))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(btnExcluirEr).addComponent(btnExcluirGramtica))
				.addGap(23)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblGramar).addComponent(lblEr))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(
						groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(listagemEr, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE)
						.addComponent(listagemGr, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE)).addContainerGap(30, Short.MAX_VALUE)));
		getContentPane().setLayout(groupLayout);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuArquivo = new JMenu("Arquivo");
		menuBar.add(menuArquivo);

		JMenuItem mntmSalvarGramticas = new JMenuItem("Salvar Gramáticas");
		mntmSalvarGramticas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GRBase rgbBase = new GRBase();
				try {
					rgbBase.save(gramHash);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		JMenuItem mntmAbrirGramticas = new JMenuItem("Abrir Gramáticas");
		mntmAbrirGramticas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opcao = JOptionPane.showConfirmDialog(null, "Esta ação irá apagar dados não salvos.\nDeseja continuar?", null, JOptionPane.YES_NO_OPTION);
				if (JOptionPane.YES_OPTION == opcao) {
					GRBase rgbBase = new GRBase();
					gramHash = rgbBase.get();
					modeloGR.clear();
					for (String chave : gramHash.keySet()) {
						modeloGR.addElement(chave);
					}
				}
			}
		});
		menuArquivo.add(mntmAbrirGramticas);

		JMenuItem mntmAbrirExpresses = new JMenuItem("Abrir Expressões");
		mntmAbrirExpresses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opcao = JOptionPane.showConfirmDialog(null, "Esta ação irá apagar dados não salvos.\nDeseja continuar?", null, JOptionPane.YES_NO_OPTION);
				if (JOptionPane.YES_OPTION == opcao) {
					ERBase erBase = new ERBase();
					expRegHash = erBase.get();
					modeloER.clear();
					for (String chave : expRegHash.keySet()) {
						modeloER.addElement(chave);
					}
				}
			}
		});
		menuArquivo.add(mntmAbrirExpresses);
		menuArquivo.add(mntmSalvarGramticas);

		JMenuItem mntmSalvarExpresses = new JMenuItem("Salvar Expressões");
		mntmSalvarExpresses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ERBase erBase = new ERBase();
				try {
					erBase.save(expRegHash);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		menuArquivo.add(mntmSalvarExpresses);

		JMenuItem mntmSair = new JMenuItem("Sair");
		mntmSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(EXIT_ON_CLOSE);
			}
		});
		menuArquivo.add(mntmSair);
	}

	private void loadPersistence() {
		this.expRegHash = this.erBase.get();
		this.gramHash = this.grBase.get();

		for (String nome : expRegHash.keySet()) {
			this.modeloER.addElement(nome);
		}

		for (String nome : gramHash.keySet()) {
			this.modeloGR.addElement(nome);
		}
	}

}
