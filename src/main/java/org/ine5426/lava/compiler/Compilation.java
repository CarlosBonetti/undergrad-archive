package org.ine5426.lava.compiler;

import java.util.Arrays;
import java.util.Collection;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ine5426.lava.generated.LavaLexer;
import org.ine5426.lava.generated.LavaParser;

public class Compilation {
	public final ANTLRInputStream inputStream;
	public final LavaLexer lexer;
	public final CommonTokenStream tokenStream;
	public final LavaParser parser;
	public final ParseTree tree;
	public final String jasminCode;
	public final Collection<String> jFiles;

	public Compilation(ANTLRInputStream input) {
		inputStream = input;
		lexer = new LavaLexer(input);
		tokenStream = new CommonTokenStream(lexer);
		parser = new LavaParser(tokenStream);
		parser.addErrorListener(new DiagnosticErrorListener());
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		tree = parser.program();
		PreVisitor finder = new PreVisitor();
		finder.visit(tree);
		jasminCode = new LavaVisitor(finder.getSymbolTable()).visit(tree);
		jFiles = Arrays.asList(jasminCode.split(JasminTemplate.CLASS_SEPARATOR));
	}

	/**
	 * Return the token stream of the compilation as String
	 */
	public String tokenStreamToString() {
		String tokens = "";
		tokenStream.fill();
		for (Token tok : tokenStream.getTokens()) {
			tokens += (tok + " -> " + LavaLexer.VOCABULARY.getDisplayName(tok.getType()));
			tokens += System.lineSeparator();
		}
		return tokens;
	}

	/**
	 * Return the Parse Tree as String
	 *
	 * @return
	 */
	public String treeToString() {
		return tree.toStringTree(parser);
	}

	/**
	 * Return GUI window with the parse tree
	 */
	public void treeInspect() {
		Trees.inspect(tree, parser);
	}
}
