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
import org.spldev.formula.clause.configuration.*;
import org.spldev.formula.clause.io.*;
import org.spldev.util.*;
import org.spldev.util.cli.*;
import org.spldev.util.extension.*;
import org.spldev.util.io.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

/**
 * Command line interface for sampling algorithms.
 *
 * @author Sebastian Krieter
 */
public class ConfigurationGeneratorCLI extends ExtensionPoint<ConfigurationGeneratorAlgorithm> implements CLIFunction {

	@Override
	public String getId() {
		return "genconfig";
	}

	@Override
	public void run(List<String> args) {
		Path outputFile = null;
		Path fmFile = null;
		ConfigurationGeneratorAlgorithm algorithm = null;

		final List<String> remainingArguments = new ArrayList<>();
		for (final ListIterator<String> iterator = args.listIterator(); iterator.hasNext();) {
			final String arg = iterator.next();
			switch (arg) {
			case "-a": {
				String name = CLI.getArgValue(iterator, arg).toLowerCase();
				for (ConfigurationGeneratorAlgorithm algExtension : getExtensions()) {
					if (Objects.equals(name, algExtension.getName())) {
						algorithm = algExtension;
					}
				}
			}
			case "-o": {
				outputFile = Paths.get(CLI.getArgValue(iterator, arg));
				break;
			}
			case "-fm": {
				fmFile = Paths.get(CLI.getArgValue(iterator, arg));
				break;
			}
			default: {
				remainingArguments.add(arg);
			}
			}
		}

		if (fmFile == null) {
			throw new IllegalArgumentException("No feature model specified!");
		}
		if (outputFile == null) {
			throw new IllegalArgumentException("No output file specified!");
		}
		if (algorithm == null) {
			throw new IllegalArgumentException("No algorithm specified!");
		}
		final ConfigurationGenerator generator = algorithm.parseArguments(remainingArguments).orElse(
			Logger::logProblems);

//		IConfigurationGenerator generator = null;
//		switch (algorithm.toLowerCase()) {
//		case "icpl": {
//			if (t == null) {
//				throw new IllegalArgumentException("Value of t must be specified for icpl (use -t <value>)");
//			}
//			generator = new SPLCAToolConfigurationGenerator("ICPL", t, limit);
//			break;
//		}
//		case "chvatal": {
//			if (t == null) {
//				throw new IllegalArgumentException("Value of t must be specified for chvatal (use -t <value>)");
//			}
//			generator = new SPLCAToolConfigurationGenerator("Chvatal", t, limit);
//			break;
//		}

		final CNF cnf = FileHandler.parse(fmFile, new DIMACSFormat())
			.orElseThrow(p -> new IllegalArgumentException(p.isEmpty() ? null : p.get(0).getError().get()));
		final Path out = outputFile;
		final Result<List<LiteralList>> result = Executor.run(generator, cnf);
		result.ifPresentOrElse(list -> {
			try {
				FileHandler.write(new SolutionList(cnf.getVariableMap(), list), out,
					new ConfigurationListFormat());
			} catch (final IOException e) {
				Logger.logError(e);
			}
		}, Logger::logProblems);
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
