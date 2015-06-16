package com.bonaguiar.formais1.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.bonaguiar.formais1.core.automata.AF;
import com.bonaguiar.formais1.core.exception.FormaisException;
import com.bonaguiar.formais1.core.expr.ExprRegular;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;

public class ViewAF extends JFrame {
	private static final long serialVersionUID = 8673596743628693904L;
	private JTable table;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExprRegular exp = new ExprRegular("(aa|(bb)*)cc");
					ViewAF frame = new ViewAF(exp.getAFD());
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
		setTitle("Autômato Finito");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 519, 422);
		
		table = new JTable();
		table.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent arg0) {
			}
			public void inputMethodTextChanged(InputMethodEvent arg0) {
				
			}
		});
		
		try {
			fillTable(af);
		} catch (FormaisException e) {
			e.printStackTrace();
		}	
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		this.add(scrollPane);
	}
	
	/**
	 * Preenche a table com o conteúdo do AF
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
				row.add(af.transicao(q, c).toString());
			}
			
			model.addRow(row.toArray());
		}
		
		table.setModel(model);	
	}
}
