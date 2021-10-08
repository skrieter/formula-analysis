package org.spldev.formula.cli.analysis;

import org.spldev.formula.analysis.sat4j.*;
import org.spldev.util.cli.*;

public class SatisfiableAlgorithm extends AlgorithmWrapper<HasSolutionAnalysis> {

	@Override
	protected HasSolutionAnalysis createAlgorithm() {
		return new HasSolutionAnalysis();
	}

	@Override
	public String getName() {
		return "satisfiable";
	}

	@Override
	public String getHelp() {
		final StringBuilder helpBuilder = new StringBuilder();
		helpBuilder.append("\t");
		helpBuilder.append(getName());
		return helpBuilder.toString();
	}

}
