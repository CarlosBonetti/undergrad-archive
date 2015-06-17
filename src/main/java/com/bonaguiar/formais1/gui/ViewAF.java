package com.bonaguiar.formais1.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.bonaguiar.formais1.core.automata.AF;
import com.bonaguiar.formais1.core.automata.AFD;
import com.bonaguiar.formais1.core.exception.FormaisException;
import com.bonaguiar.formais1.core.grammar.GRParser;
import com.bonaguiar.formais1.core.grammar.GramaticaRegular;

public class ViewAF extends JFrame {
	private static final long serialVersionUID = 8673596743628693904L;
	private JTable table;
	private AF af;
	private App frame;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GramaticaRegular exp = GRParser.parse("S->bS|b | & \n");
					ViewAF frame = new ViewAF(exp.getAutomatoFinito(), null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param frame2 
	 */
	public ViewAF(AF af, App frame2) {
		this.frame = frame2;
		this.af = af;
		setTitle("Autômato Finito");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(80, 80, 640, 480);

		table = new JTable();
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);

		try {
			fillTable(af);
		} catch (FormaisException e) {
			e.printStackTrace();
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		getContentPane().add(scrollPane);

		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);

		JMenuItem determinizar = new JMenuItem("Determinizar");
		determinizar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					abrirOutro(ViewAF.this.af.determinizar(), "Determinização de " + ViewAF.this.getTitle());
				} catch (Exception e) {
					tratarException(e);
				}
			}
		});
		menuBar.add(determinizar);

		JMenuItem minimizar = new JMenuItem("Minimizar");
		minimizar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					if (ViewAF.this.af instanceof AFD) {
						abrirOutro(((AFD) ViewAF.this.af).getAFMin(), "Minimização de " + ViewAF.this.getTitle());
					} else {
						abrirOutro(ViewAF.this.af.determinizar().getAFMin(), "Minimização de " + ViewAF.this.getTitle());
					}
				} catch (Exception e) {
					tratarException(e);
				}
			}
		});
		menuBar.add(minimizar);

		JMenuItem complementar = new JMenuItem("Complementar");
		complementar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					if (ViewAF.this.af instanceof AFD) {
						abrirOutro(((AFD) ViewAF.this.af).getComplemento(), "Complementação de " + ViewAF.this.getTitle());
					} else {
						abrirOutro(ViewAF.this.af.determinizar().getComplemento(), "Complementação de " + ViewAF.this.getTitle());
					}
				} catch (Exception e) {
					tratarException(e);
				}
			}
		});
		menuBar.add(complementar);

		JMenuItem comparar = new JMenuItem("Comparar");
		comparar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				System.out.println("Comparar");
				System.out.println(selecionaOpcao());
			}
		});
		menuBar.add(comparar);

		JMenuItem interseccionar = new JMenuItem("Interseccionar");
		interseccionar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				System.out.println("Interseccionar");
				System.out.println(selecionaOpcao());
			}
		});
		menuBar.add(interseccionar);

		JMenuItem busca = new JMenuItem("Busca");
		busca.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				System.out.println("Busca");
			}
		});
		menuBar.add(busca);
	}

	protected void tratarException(Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, e.getMessage());
	}

	protected void abrirOutro(AF af, String title) {
		ViewAF view = new ViewAF(af, frame);
		view.setVisible(true);
		Rectangle bounds = this.getBounds();
		bounds.setLocation(bounds.x + 30, bounds.y + 15);
		view.setBounds(bounds);
		view.setTitle(title);
	}

	/**
	 * Preenche a table com o conteúdo do AF
	 *
	 * @param af
	 * @throws FormaisException
	 */
	protected void fillTable(AF af) throws FormaisException {
		DefaultTableModel model = new DefaultTableModel();

		// Cria nomes das colunas
		model.addColumn("δ");
		for (Character c : af.getAlfabeto().sorted()) {
			model.addColumn(c.toString());
		}

		// Cria linhas
		for (String q : af.getEstados()) {
			ArrayList<String> row = new ArrayList<String>();
			String estado = q;

			if (af.getEstadoInicial().equals(q)) {
				estado = "->" + estado;
			}

			if (af.getEstadosFinais().contains(q)) {
				estado = "*" + estado;
			}

			row.add(estado);

			for (Character c : af.getAlfabeto().sorted()) {
				List<String> t = af.transicao(q, c);
				if (t.isEmpty()) {
					row.add("-");
				} else if (t.size() == 1) {
					row.add(t.get(0).toString());
				} else {
					row.add(t.toString());
				}
			}

			model.addRow(row.toArray());
		}

		table.setModel(model);
	}
	
	/**
	 * chama uma caixa de dialogo com uma comboBox com todas as opcoes de ER e GR que estao presentes em App
	 * @return String no <b> ER - chave<\b> ou <b>GR - chave<\b>
	 * 
	 */
	
	private String selecionaOpcao() {
		String itemSelecionado;
		JComboBox<String> comboBox  = new JComboBox<String>();
		for (String chaveEr : frame.getExpRegHash().keySet()) {
			comboBox.addItem("ER - "+ chaveEr);
		}
		for (String chaveGr : frame.getGramHash().keySet()) {
			comboBox.addItem("GR - " + chaveGr);
		}
		int botaoOk = JOptionPane.showConfirmDialog(null, comboBox, "Compare com:", JOptionPane.OK_CANCEL_OPTION);

		if (botaoOk == JOptionPane.OK_OPTION) {
			itemSelecionado = comboBox.getSelectedItem().toString();
			System.out.println(itemSelecionado);

			if (itemSelecionado.trim().isEmpty()) 
				return itemSelecionado.trim();
		}
		return null;
	}

}
