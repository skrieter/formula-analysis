package org.spldev.formula.clause.cli;

import java.nio.file.*;
import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.configuration.twise.*;
import org.spldev.formula.clause.io.*;
import org.spldev.util.cli.*;
import org.spldev.util.io.*;

/**
 * Generates configurations for a given propositional formula such that t-wise
 * feature coverage is achieved.
 *
 * @author Sebastian Krieter
 */
public class TWiseAlgorithm extends AConfigurationGeneratorAlgorithm<TWiseConfigurationGenerator> {

	@Override
	protected TWiseConfigurationGenerator createConfigurationGenerator() {
		return new TWiseConfigurationGenerator();
	}

	@Override
	protected boolean parseArgument(TWiseConfigurationGenerator gen, String arg, ListIterator<String> iterator)
		throws IllegalArgumentException {
		if (!super.parseArgument(gen, arg, iterator)) {
			switch (arg) {
			case "-t":
				gen.setT(Integer.parseInt(CLI.getArgValue(iterator, arg)));
				break;
			case "-m":
				gen.setIterations(Integer.parseInt(CLI.getArgValue(iterator, arg)));
				break;
			case "-e":
				gen.setNodes(readExpressionFile(Paths.get(CLI.getArgValue(iterator, arg))));
				break;
			default:
				return false;
			}
		}
		return true;
	}

	private List<List<ClauseList>> readExpressionFile(Path expressionFile) {
		final List<List<ClauseList>> expressionGroups;
		if (expressionFile != null) {
			expressionGroups = FileHandler.parse(expressionFile, new ExpressionGroupFormat())
				.orElseThrow(p -> new IllegalArgumentException(p.isEmpty() ? null : p.get(0).getError().get()));
		} else {
			expressionGroups = null;
		}
		return expressionGroups;
	}

	@Override
	public String getName() {
		return "yasa";
	}

}
