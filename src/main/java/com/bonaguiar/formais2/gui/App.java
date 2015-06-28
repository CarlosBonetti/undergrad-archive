package com.bonaguiar.formais2.gui;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.HeadlessException;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;

import com.bonaguiar.formais2.core.GLC;
import com.bonaguiar.formais2.persistence.GLCBase;

public class App extends JFrame {

	private static final long serialVersionUID = 1L;
	private HashMap<String, GLC> gramHash = new HashMap<String, GLC>();
	private DefaultListModel<String> modeloGLC = new DefaultListModel<String>();
	private JList<String> listagemGlc = new JList<String>(modeloGLC);;
	private String glcSelecionado = "";
	private GLCBase GLCBase = new GLCBase();
	private JPanel painelSecundario = new JPanel();
	private JPanel principal = new JPanel();
	private JTextArea textAreaGrammar = new JTextArea();


	private void setGlcSelecionado(String selecao) {
		glcSelecionado = selecao;
	}

	private boolean ehChaveGlcNova(String chave) {
		return gramHash.containsKey(chave);
	}

	private void tratarException(Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, e.getMessage() != null
				| e.getMessage().isEmpty() ? e.getMessage()
				: "\nTente novamente.");
	}

	private void persistGlc() {
		try {
			GLCBase.save(gramHash);
		} catch (Exception e) {
			tratarException(e);
		}
	}

	private void adicionaNaListaGR(String nome, GLC glc) {
		gramHash.put(nome, glc);
		modeloGLC.addElement(nome);
		persistGlc();
	}

	private void editarNaLista(String nome, GLC glc) {
		gramHash.put(nome, glc);
		persistGlc();
	}

	private void removeDaLista(String nome) {
		gramHash.remove(nome);
		modeloGLC.removeElement(nome);
		glcSelecionado = "";
		persistGlc();
	}
	
	private void atualizaTextAreaGrammar(){
		textAreaGrammar.setText(gramHash.get(glcSelecionado).getRaw());
	}

	private void adicionarGramatica() {
		String gramatica;
		JTextArea area = new JTextArea("", 20, 15);
		int botaoOk = JOptionPane.showConfirmDialog(this,
				new JScrollPane(area), "Gramática",
				JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			gramatica = area.getText();
			System.out.println(gramatica);

			if (gramatica.trim().isEmpty()) {
				JOptionPane.showMessageDialog(this,
						"Campo texto não pode estar vazio.\nTente novamente.");
			} else {
				try {
					GLC glc = new GLC(gramatica);

					String nomeGram = JOptionPane.showInputDialog(this,
							"Digite um nome para a gramática:");
					while (nomeGram.trim().isEmpty() | ehChaveGlcNova(nomeGram)) {
						nomeGram = JOptionPane
								.showInputDialog("Campo obrigatório e único.!\nDigite um nome para a gramática:");
					}
					adicionaNaListaGR(nomeGram, glc);

				} catch (Exception e) {
					JOptionPane.showMessageDialog(this,
							e.getMessage() != null ? e.getMessage()
									: "\nTente novamente.");
				}
			}
		}
	}

	private void editarGramatica(String chave) {
		String gramatica;
		JTextArea area = new JTextArea(gramHash.get(chave).getRaw(), 20, 15);
		int botaoOk = JOptionPane.showConfirmDialog(frame,
				new JScrollPane(area), "Gramática",
				JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			gramatica = area.getText();

			if (gramatica.trim().isEmpty()) {
				JOptionPane pane = new JOptionPane(
						"Campo texto não pode estar vazio.\nTente novamente.");
				JDialog d = pane.createDialog((JFrame) null, "Comparação");
				d.setLocation(getLocation());
				d.setVisible(true);
			} else {
				try {
					GLC glc = new GLC(gramatica);
					editarNaLista(chave, glc);
				} catch (Exception e2) {
					tratarException(e2);
				}
			}
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
		setResizable(false);
		loadPersistence();

		setTitle("Programa Tela Inicial");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 326, 546);
		getContentPane().setLayout(new CardLayout(0, 0));

		getContentPane().add(principal, "name_24394437385808");

		listagemGlc = new JList<String>(modeloGLC);
		listagemGlc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList list = (JList) e.getSource();
				String valorSelecao = list.getSelectedValue().toString();
				setGlcSelecionado(valorSelecao);
				if (e.getClickCount() == 2) {
					atualizaTextAreaGrammar();
					setContentPane(painelSecundario);
					painelSecundario.setVisible(true);
					
				}
			}
		});
		listagemGlc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblGramar = new JLabel("Gramáticas:");

		JButton btAddGramatica = new JButton("Nova Gramática");

		JButton btnEditarGramtica = new JButton("Editar Gramática");

		JButton btnExcluirGramtica = new JButton("Excluir Gramática");

		GroupLayout gl_principal = new GroupLayout(principal);
		gl_principal
				.setHorizontalGroup(gl_principal
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_principal
										.createSequentialGroup()
										.addGap(66)
										.addGroup(
												gl_principal
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_principal
																		.createSequentialGroup()
																		.addGap(20)
																		.addGroup(
																				gl_principal
																						.createParallelGroup(
																								Alignment.LEADING,
																								false)
																						.addComponent(
																								btAddGramatica,
																								Alignment.TRAILING,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(
																								btnEditarGramtica,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addGroup(
																								gl_principal
																										.createSequentialGroup()
																										.addGap(12)
																										.addComponent(
																												lblGramar))
																						.addComponent(
																								btnExcluirGramtica,
																								Alignment.TRAILING)))
														.addComponent(
																listagemGlc,
																GroupLayout.PREFERRED_SIZE,
																190,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(70, Short.MAX_VALUE)));
		gl_principal.setVerticalGroup(gl_principal.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_principal
						.createSequentialGroup()
						.addGap(40)
						.addComponent(btAddGramatica)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnEditarGramtica)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnExcluirGramtica)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(lblGramar)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(listagemGlc, GroupLayout.PREFERRED_SIZE,
								255, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(98, Short.MAX_VALUE)));
		principal.setLayout(gl_principal);

		getContentPane().add(painelSecundario, "name_24616259299173");

		JButton btnIntersec = new JButton("Intersec é vazio ?");
		btnIntersec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		JButton btnFatorada = new JButton("Esta fatorada ?");
		btnFatorada.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!possuiRE()) {

						if (!gramHash.get(glcSelecionado).getFatoracaoDireta()
								.isEmpty()) {
							String simbolos = "";
							for (String s : gramHash.get(glcSelecionado)
									.getFatoracaoDireta()) {
								simbolos += s + "\n";
							}
							JOptionPane.showMessageDialog(frame,
									"Grammar não esta Fatorada\npossui Não-Determinismo direto\n"
											+ simbolos);
						} else if (!gramHash.get(glcSelecionado)
								.getFatoracaoIndireta().isEmpty()) {
							String simbolos = "";
							for (String s : gramHash.get(glcSelecionado)
									.getFatoracaoIndireta()) {
								simbolos += s + "\n";
							}
							JOptionPane.showMessageDialog(frame,
									"Grammar não esta Fatorada\npossui Não-Determinismo indireta\n"
											+ simbolos);
						} else {
							JOptionPane.showMessageDialog(frame,
									"Grammar esta Fatorada");
						}
					} else {
						JOptionPane.showMessageDialog(frame,
								"Aterta!\nGramática possui R.E.!");
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		JButton btnFollow = new JButton("Follow ?");
		btnFollow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!possuiRE()) {

						if (!gramHash.get(glcSelecionado).getFollowSet()
								.toString().isEmpty()) {
							String simbolos = "";
							for (String chave : gramHash.get(glcSelecionado)
									.getFollowSet().keySet()) {
								simbolos += chave + " - ";
								for (String elemento : gramHash
										.get(glcSelecionado).getFollowSet()
										.get(chave)) {
									simbolos += elemento + " , ";
								}
								simbolos = simbolos.substring(0,
										simbolos.length() - 2)
										+ "\n";
							}
							JOptionPane.showMessageDialog(frame,
									"Grammar FOLLOW\n" + simbolos);
						} else {
							JOptionPane.showMessageDialog(frame, "Try again!");
						}
					} else {
						JOptionPane.showMessageDialog(frame,
								"Aterta!\nGramática possui R.E.!");
					}

				} catch (Exception e) {
					tratarException(e);
				}
			}
		});
		JButton btnParser = new JButton("Parser ?");
		btnParser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(glcSelecionado + " glcSelect");
				// for (String iterable_element : gramHash.keySet()) {
				// System.out.println(gramHash.get(glcSelecionado).getFirstSet()
				// + " - hash");
				// }
			}
		});
		JButton btnFirst = new JButton("First ?");
		btnFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!possuiRE()) {
						if (!gramHash.get(glcSelecionado).getFirstSet()
								.toString().isEmpty()) {
							String simbolos = "";
							for (String chave : gramHash.get(glcSelecionado)
									.getFirstSet().keySet()) {
								simbolos += chave + " - ";
								for (String elemento : gramHash
										.get(glcSelecionado).getFirstSet()
										.get(chave)) {
									simbolos += elemento + " , ";
								}
								simbolos = simbolos.substring(0,
										simbolos.length() - 2)
										+ "\n";
							}
							JOptionPane.showMessageDialog(frame,
									"Grammar FIRST\n" + simbolos);
						} else {
							JOptionPane.showMessageDialog(frame, "Try again!");
						}
					} else
						JOptionPane.showMessageDialog(frame,
								"Aterta!\nGramática possui R.E.!");
				} catch (Exception e) {
					tratarException(e);
				}
			}
		});

		JButton btnRecEsq = new JButton("Possui R.E. ?");
		btnRecEsq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!gramHash.get(glcSelecionado)
							.getRecursaoEsquerdaDireta().isEmpty()) {
						String simbolos = "";
						for (String s : gramHash.get(glcSelecionado)
								.getRecursaoEsquerdaDireta()) {
							simbolos += s + "\n";
						}
						JOptionPane.showMessageDialog(frame,
								"Grammar possui R.E. direta\n" + simbolos);
					} else if (!gramHash.get(glcSelecionado)
							.getRecursaoEsquerdaIndireta().isEmpty()) {
						String simbolos = "";
						for (String s : gramHash.get(glcSelecionado)
								.getRecursaoEsquerdaIndireta()) {
							simbolos += s + "\n";
						}
						JOptionPane.showMessageDialog(frame,
								"Grammar possui R.E. indireta\n" + simbolos);
					} else {
						JOptionPane.showMessageDialog(frame,
								"Grammar NÂO possui R.E direta ou indireta");
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		JButton btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setContentPane(principal);
			}
		});
		
		textAreaGrammar = new JTextArea();
		JScrollPane js = new JScrollPane(textAreaGrammar);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textAreaGrammar.setEditable(false);
//		textAreaGrammar.add(js);
		GroupLayout gl_painelSecundario = new GroupLayout(painelSecundario);
		gl_painelSecundario.setHorizontalGroup(
			gl_painelSecundario.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(45)
					.addComponent(btnVoltar, GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
					.addGap(53))
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(91)
					.addComponent(btnRecEsq)
					.addContainerGap(112, Short.MAX_VALUE))
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(91)
					.addComponent(btnFatorada)
					.addContainerGap(93, Short.MAX_VALUE))
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(91)
					.addComponent(btnIntersec)
					.addContainerGap(78, Short.MAX_VALUE))
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(91)
					.addComponent(btnFirst)
					.addContainerGap(158, Short.MAX_VALUE))
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(91)
					.addComponent(btnFollow)
					.addContainerGap(144, Short.MAX_VALUE))
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(91)
					.addComponent(btnParser)
					.addContainerGap(143, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_painelSecundario.createSequentialGroup()
					.addContainerGap(64, Short.MAX_VALUE)
					.addComponent(js, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
					.addGap(40))
		);
		gl_painelSecundario.setVerticalGroup(
			gl_painelSecundario.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_painelSecundario.createSequentialGroup()
					.addGap(33)
					.addComponent(btnRecEsq)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnFatorada)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnIntersec)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnFirst)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnFollow)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnParser)
					.addGap(26)
					.addComponent(js, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
					.addComponent(btnVoltar)
					.addGap(21))
		);
		painelSecundario.setLayout(gl_painelSecundario);

		btnExcluirGramtica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!modeloGLC.isEmpty() && !glcSelecionado.isEmpty()) {
					int opcao = JOptionPane.showConfirmDialog(frame,
							"Confirmar exclusão de " + glcSelecionado, null,
							JOptionPane.YES_NO_OPTION);
					if (JOptionPane.YES_OPTION == opcao) {
						removeDaLista(glcSelecionado);
					}
				} else
					JOptionPane.showMessageDialog(frame,
							"É necessário selecionar uma gramática");

			}
		});
		btnEditarGramtica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editarGramatica(glcSelecionado);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(frame,
							"É necessário selecionar uma gramática");
				}

			}
		});
		btAddGramatica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adicionarGramatica();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuArquivo = new JMenu("Arquivo");
		menuBar.add(menuArquivo);

		JMenuItem mntmSalvarGramticas = new JMenuItem("Salvar Gramáticas");
		mntmSalvarGramticas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GLCBase rgbBase = new GLCBase();
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
				int opcao = JOptionPane
						.showConfirmDialog(
								null,
								"Esta ação irá apagar dados não salvos.\nDeseja continuar?",
								null, JOptionPane.YES_NO_OPTION);
				if (JOptionPane.YES_OPTION == opcao) {
					GLCBase glcBase = new GLCBase();
					gramHash = glcBase.get();
					modeloGLC.clear();
					for (String chave : gramHash.keySet()) {
						modeloGLC.addElement(chave);
					}
				}
			}
		});
		menuArquivo.add(mntmAbrirGramticas);
		menuArquivo.add(mntmSalvarGramticas);

		JMenuItem mntmSair = new JMenuItem("Sair");
		mntmSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(EXIT_ON_CLOSE);
			}
		});
		menuArquivo.add(mntmSair);
	}

	private void loadPersistence() {
		this.gramHash = this.GLCBase.get();

		for (String nome : gramHash.keySet()) {
			this.modeloGLC.addElement(nome);
		}
	}

	private boolean possuiRE() throws Exception {
		if (gramHash.get(glcSelecionado).getRecursaoEsquerdaDireta().isEmpty()
				&& gramHash.get(glcSelecionado).getRecursaoEsquerdaIndireta()
						.isEmpty()) {
			return false;
		}
		return true;
	}
}
