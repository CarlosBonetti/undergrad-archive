package com.bonaguiar.formais1.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GramaticaRegular exp = GRParser.parse("S->aS|a \n");
					ViewAF frame = new ViewAF(exp.getAutomatoFinito());
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
	public ViewAF(AF af) {
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
			}
		});
		menuBar.add(comparar);

		JMenuItem interseccionar = new JMenuItem("Interseccionar");
		interseccionar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				System.out.println("Interseccionar");
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
		ViewAF view = new ViewAF(af);
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
}
