package org.ine5426.lava.utils;

import java.util.ArrayList;
import java.util.List;

public class SourceBuilder {
	protected List<String> lines = new ArrayList<>();
	protected int indentLevel = 0;

	public SourceBuilder line(String line) {
		this.lines.add(this.produceIndents() + line + System.lineSeparator());
		return this;
	}

	public SourceBuilder indent() {
		this.indentLevel++;
		return this;
	}

	public SourceBuilder dedent() {
		this.indentLevel--;

		if (this.indentLevel < 0) {
			this.indentLevel = 0;
		}

		return this;
	}

	@Override
	public String toString() {
		String s = "";
		for (String line : this.lines) {
			s += line;
		}
		return s;
	}

	protected String produceIndents() {
		String ind = "";
		for (int i = 0; i < this.indentLevel; i++) {
			ind += "\t";
		}
		return ind;
	}
}
