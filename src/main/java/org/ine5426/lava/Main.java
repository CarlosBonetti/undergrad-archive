package org.ine5426.lava;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.ine5426.lava.compiler.Compilation;

public class Main {

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("k", false, "Print tokens");
		options.addOption("t", false, "Print tree");
		options.addOption("j", false, "Print jasmin code");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.getArgList().size() == 0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("lava [OPTION] [FILE]", options);
			return;
		}

		String file = cmd.getArgList().get(0);

		Compilation compilation = new Compilation(new ANTLRFileStream(file));

		if (cmd.hasOption("k"))
			System.out.println(compilation.tokenStreamToString());

		if (cmd.hasOption("t"))
			System.out.println(compilation.treeToString());
		// compilation.treeInspect();

		if (cmd.hasOption("j"))
			System.out.println(compilation.jasminCode);

		Runner.createTempDir();
		System.out.println(Runner.run(compilation));
		Runner.deleteTempDir();
	}
}
