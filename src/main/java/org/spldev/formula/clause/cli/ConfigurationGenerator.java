/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package org.spldev.formula.clause.cli;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.analysis.*;
import org.spldev.formula.clause.configuration.*;
import org.spldev.formula.clause.configuration.twise.*;
import org.spldev.formula.clause.io.*;
import org.spldev.util.*;
import org.spldev.util.cli.*;
import org.spldev.util.io.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

/**
 * Command line interface for sampling algorithms.
 *
 * @author Sebastian Krieter
 */
public class ConfigurationGenerator implements CLIFunction {

	private String algorithm;
	private Path outputFile;
	private Path fmFile;
	private Path expressionFile;
	private Integer t;
	private Integer m;
	private Integer limit;
	private Long seed;

	@Override
	public String getId() {
		return "genconfig";
	}

	@Override
	public void run(List<String> args) {
		parseArguments(args);

		if (fmFile == null) {
			throw new IllegalArgumentException("No feature model specified!");
		}
		if (outputFile == null) {
			throw new IllegalArgumentException("No output file specified!");
		}
		if (algorithm == null) {
			throw new IllegalArgumentException("No algorithm specified!");
		}

		final Result<CNF> cnfResult = FileHandler.parse(fmFile, new DIMACSFormat());
		if (cnfResult.isEmpty()) {
			throw new IllegalArgumentException(cnfResult.getProblems().get(0).getMessage().get());
		}
		final CNF cnf = cnfResult.get();

		final List<List<ClauseList>> expressionGroups;
		if (expressionFile != null) {
			final Result<List<List<ClauseList>>> result = FileHandler.parse(expressionFile,
				new ExpressionGroupFormat());
			if (cnfResult.isEmpty()) {
				throw new IllegalArgumentException(cnfResult.getProblems().get(0).getMessage().get());
			}
			expressionGroups = result.get();
		} else {
			expressionGroups = null;
		}

		IConfigurationGenerator generator = null;
		switch (algorithm.toLowerCase()) {
//		case "icpl": {
//			if (t == null) {
//				throw new IllegalArgumentException("Value of t must be specified for icpl (use -t <value>)");
//			}
//			if (limit == null) {
//				limit = Integer.MAX_VALUE;
//			}
//			generator = new SPLCAToolConfigurationGenerator("ICPL", t, limit);
//			break;
//		}
//		case "chvatal": {
//			if (t == null) {
//				throw new IllegalArgumentException("Value of t must be specified for chvatal (use -t <value>)");
//			}
//			if (limit == null) {
//				limit = Integer.MAX_VALUE;
//			}
//			generator = new SPLCAToolConfigurationGenerator("Chvatal", t, limit);
//			break;
//		}
		case "incling": {
			if (limit == null) {
				limit = Integer.MAX_VALUE;
			}
			generator = new PairWiseConfigurationGenerator(limit);
			if (seed == null) {
				((AbstractAnalysis<?>) generator).setRandom(new Random(seed));
			}
			break;
		}
		case "yasa": {
			if (t == null) {
				throw new IllegalArgumentException("Value of t must be specified for yasa (use -t <value>)");
			}
			if (limit == null) {
				limit = Integer.MAX_VALUE;
			}
			if (expressionGroups == null) {
				generator = new TWiseConfigurationGenerator(t, limit);
			} else {
				generator = new TWiseConfigurationGenerator(expressionGroups, t, limit);
			}
			if (m != null) {
				((TWiseConfigurationGenerator) generator).setIterations(m);
			}
			if (seed == null) {
				((AbstractAnalysis<?>) generator).setRandom(new Random(seed));
			}
			break;
		}
		case "random": {
			if (limit == null) {
				limit = Integer.MAX_VALUE;
			}
			generator = new RandomConfigurationGenerator(limit);
			((RandomConfigurationGenerator) generator).setAllowDuplicates(true);
			if (seed == null) {
				((AbstractAnalysis<?>) generator).setRandom(new Random(seed));
			}
			break;
		}
		case "all": {
			if (limit == null) {
				limit = Integer.MAX_VALUE;
			}
			generator = new AllConfigurationGenerator(limit);
			if (seed == null) {
				((AbstractAnalysis<?>) generator).setRandom(new Random(seed));
			}
			break;
		}
		default:
			throw new IllegalArgumentException("No algorithm specified!");
		}
		final Result<List<LiteralList>> result = Executor.run(generator, cnf);
		result.ifPresentOrElse(list -> {
			try {
				FileHandler.write(new SolutionList(cnf.getVariableMap(), list), outputFile,
					new ConfigurationListFormat());
			} catch (final IOException e) {
				Logger.logError(e);
			}
		}, Logger::logProblems);
	}

	private void resetArguments() {
		algorithm = null;
		outputFile = null;
		fmFile = null;
		expressionFile = null;
		t = null;
		m = null;
		limit = null;
		seed = null;
	}

	private void parseArguments(List<String> args) {
		resetArguments();
		for (final Iterator<String> iterator = args.iterator(); iterator.hasNext();) {
			final String arg = iterator.next();
			if (arg.startsWith("-")) {
				switch (arg.substring(1)) {
				case "a": {
					algorithm = getArgValue(iterator, arg);
					break;
				}
				case "o": {
					outputFile = Paths.get(getArgValue(iterator, arg));
					break;
				}
				case "fm": {
					fmFile = Paths.get(getArgValue(iterator, arg));
					break;
				}
				case "t": {
					t = Integer.parseInt(getArgValue(iterator, arg));
					break;
				}
				case "m": {
					m = Integer.parseInt(getArgValue(iterator, arg));
					break;
				}
				case "l": {
					limit = Integer.parseInt(getArgValue(iterator, arg));
					break;
				}
				case "e": {
					expressionFile = Paths.get(getArgValue(iterator, arg));
					break;
				}
				case "s": {
					seed = Long.parseLong(getArgValue(iterator, arg));
					break;
				}
				default: {
					throw new IllegalArgumentException(arg);
				}
				}
			} else {
				throw new IllegalArgumentException(arg);
			}
		}
	}

	private String getArgValue(final Iterator<String> iterator, final String arg) {
		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			throw new IllegalArgumentException("No value specified for " + arg);
		}
	}

	@Override
	public String getHelp() {
		final StringBuilder helpBuilder = new StringBuilder();
		helpBuilder.append("Help for command genconfig:\n");
		helpBuilder.append("\tGeneral Parameters:\n");
		helpBuilder.append("\t\t-fm <Path>   Specify path to feature model file.\n");
		helpBuilder.append("\t\t-o <Path>    Specify path to output file.\n");
		helpBuilder.append("\t\t-a <Name>    Specify algorithm by name. One of:\n");
		helpBuilder.append("\t\t                 icpl\n");
		helpBuilder.append("\t\t                 chvatal\n");
		helpBuilder.append("\t\t                 incling\n");
		helpBuilder.append("\t\t                 yasa\n");
		helpBuilder.append("\t\t                 random\n");
		helpBuilder.append("\t\t                 all\n");
		helpBuilder.append("\n");
		helpBuilder.append("\tAlgorithm Specific Parameters:\n");
		helpBuilder.append("\t\ticpl:\n");
		helpBuilder.append("\t\t\t-t <Value>    Specify value for t\n");
		helpBuilder.append("\t\t\t-l <Value>    Specify maximum number of configurations.\n");
		helpBuilder.append("\t\tchvatal:\n");
		helpBuilder.append("\t\t\t-t <Value>    Specify value for t\n");
		helpBuilder.append("\t\t\t-l <Value>    Specify maximum number of configurations.\n");
		helpBuilder.append("\t\tincling:\n");
		helpBuilder.append("\t\t\t-l <Value>    Specify maximum number of configurations.\n");
		helpBuilder.append("\t\t\t-s <Value>    Specify random seed.\n");
		helpBuilder.append("\t\tyasa:\n");
		helpBuilder.append("\t\t\t-t <Value>    Specify value for t\n");
		helpBuilder.append("\t\t\t-m <Value>    Specify value for m\n");
		helpBuilder.append("\t\t\t-l <Value>    Specify maximum number of configurations.\n");
		helpBuilder.append("\t\t\t-s <Value>    Specify random seed.\n");
		helpBuilder.append("\t\t\t-e <Path>     Specify path to expression file\n");
		helpBuilder.append("\t\trandom:\n");
		helpBuilder.append("\t\t\t-l <Value>    Specify maximum number of configurations.\n");
		helpBuilder.append("\t\t\t-s <Value>    Specify random seed.\n");
		helpBuilder.append("\t\tall:\n");
		helpBuilder.append("\t\t\t-l <Value>    Specify maximum number of configurations.\n");
		helpBuilder.append("\t\t\t-s <Value>    Specify random seed.\n");
		return helpBuilder.toString();
	}

}
