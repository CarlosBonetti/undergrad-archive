package com.bonaguiar.formais2.gui;

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
import javax.swing.JDialog;
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

import com.bonaguiar.formais2.core.GLC;
import com.bonaguiar.formais2.persistence.GLCBase;

public class App extends JFrame {
	@Getter
	private HashMap<String, GLC> gramHash = new HashMap<String, GLC>();
	private DefaultListModel<String> modeloGLC = new DefaultListModel<String>();
	private JList<String> listagemGlc;
	private String glcSelecionado = "";
	private GLCBase GLCBase = new GLCBase();

	private void setGlcSelecionado(String selecao) {
		glcSelecionado = selecao;
	}

	private boolean ehChaveGlcNova(String chave) {
		return gramHash.containsKey(chave);
	}

	private void tratarException(Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, e.getMessage() != null | e.getMessage().isEmpty() ? e.getMessage(): "\nTente novamente.");
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

	private void adicionarGramatica() {
		String gramatica;
		JTextArea area = new JTextArea("", 20, 15);
		int botaoOk = JOptionPane.showConfirmDialog(this, new JScrollPane(area), "Gramática", JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			gramatica = area.getText();
			System.out.println(gramatica);

			if (gramatica.trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Campo texto não pode estar vazio.\nTente novamente.");
			} else {
				try {
					GLC glc = new GLC(gramatica);

					String nomeGram = JOptionPane.showInputDialog("Digite um nome para a gramática:");
					while (nomeGram.trim().isEmpty() | ehChaveGlcNova(nomeGram)) {
						nomeGram = JOptionPane.showInputDialog("Campo obrigatório e único.!\nDigite um nome para a gramática:");
					}
					adicionaNaListaGR(nomeGram, glc);

				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, e.getMessage() != null? e.getMessage(): "\nTente novamente.");
				}
			}
		}
	}

	private void editarGramatica(String chave) {
		String gramatica;
		JTextArea area = new JTextArea(gramHash.get(chave).getTodaGramatica(), 20, 15);
		int botaoOk = JOptionPane.showConfirmDialog(null, new JScrollPane(area), "Gramática", JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			gramatica = area.getText();

			if (gramatica.trim().isEmpty()) {
				JOptionPane pane = new JOptionPane("Campo texto não pode estar vazio.\nTente novamente.");
				JDialog d =	pane.createDialog((JFrame)null, "Comparação")	;
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
		setBounds(100, 100, 326, 512);

		JButton btAddGramatica = new JButton("Nova Gramática");
		btAddGramatica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adicionarGramatica();
			}
		});

		listagemGlc = new JList<String>(modeloGLC);
		listagemGlc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList list = (JList) e.getSource();
				String valorSelecao = list.getSelectedValue().toString();
				setGlcSelecionado(valorSelecao);
				if (e.getClickCount() == 2) {
//					try {
//						ViewAF view = new ViewAF(gramHash.get(valorSelecao).getAutomatoFinito(), frame);
//						view.setVisible(true);
//						view.setTitle(valorSelecao);
//					} catch (FormaisException e1) {
//						e1.printStackTrace();
//					}
				}
			}
		});
		listagemGlc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblGramar = new JLabel("Gramáticas:");

		JButton btnEditarGramtica = new JButton("Editar Gramática");
		btnEditarGramtica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editarGramatica(glcSelecionado);
				} catch (Exception e2) {
				}
				
			}
		});

		JButton btnExcluirGramtica = new JButton("Excluir Gramática");
		btnExcluirGramtica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!modeloGLC.isEmpty() && !glcSelecionado.isEmpty()) {
					int opcao = JOptionPane.showConfirmDialog(null, "Confirmar exclusão de " + glcSelecionado, null, JOptionPane.YES_NO_OPTION);
					if (JOptionPane.YES_OPTION == opcao) {
						removeDaLista(glcSelecionado);
					}
				}
			}
		});


		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(49)
							.addComponent(listagemGlc, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(110)
									.addComponent(lblGramar, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(98)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(btAddGramatica, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnEditarGramtica, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnExcluirGramtica))))
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addContainerGap(333, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addComponent(btAddGramatica)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnEditarGramtica)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnExcluirGramtica)
					.addGap(23)
					.addComponent(lblGramar)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(listagemGlc, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(30, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);

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
				int opcao = JOptionPane.showConfirmDialog(null, "Esta ação irá apagar dados não salvos.\nDeseja continuar?", null, JOptionPane.YES_NO_OPTION);
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
		
	//	JMenuItem mntmTeste = new JMenuItem("teste");
//		mntmTeste.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				JOptionPane pane = new JOptionPane("Busca por padrões:");
//				pane.add(new JEditorPane());
//				
//				pane.add(new JTextField());
//				JDialog d =	pane.createDialog(null, "Comparação")	;
//				d.setLocation(getLocation());
//				d.setBounds(getBounds());
//				d.setVisible(true);
//			}
//		});
//		menuBar.add(mntmTeste);
	}

	private void loadPersistence() {
		this.gramHash = this.GLCBase.get();

		for (String nome : gramHash.keySet()) {
			this.modeloGLC.addElement(nome);
		}
	}

}
