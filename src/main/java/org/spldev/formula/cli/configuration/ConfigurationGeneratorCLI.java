/* -----------------------------------------------------------------------------
 * Formula-Analysis Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis Lib.
 * 
 * Formula-Analysis Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula-analysis> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.cli.configuration;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.spldev.formula.*;
import org.spldev.formula.analysis.sat4j.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.expression.io.*;
import org.spldev.formula.io.*;
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
public class ConfigurationGeneratorCLI implements CLIFunction {

	@Override
	public String getId() {
		return "genconfig";
	}

	@Override
	public void run(List<String> args) {
		Path outputFile = null;
		Path fmFile = null;
		ConfigurationGeneratorAlgorithm algorithm = null;
		int limit = Integer.MAX_VALUE;

		final List<String> remainingArguments = new ArrayList<>();
		for (final ListIterator<String> iterator = args.listIterator(); iterator.hasNext();) {
			final String arg = iterator.next();
			switch (arg) {
			case "-a": {
				// TODO add plugin for icpl and chvatal
				final String name = CLI.getArgValue(iterator, arg).toLowerCase();
				for (final ConfigurationGeneratorAlgorithm algExtension : ConfigurationGeneratorAlgorithmManager
					.getInstance().getExtensions()) {
					if (Objects.equals(name, algExtension.getName())) {
						algorithm = algExtension;
						break;
					}
				}
				if (algorithm == null) {
					throw new IllegalArgumentException("Unkown algorithm: " + name);
				}
				break;
			}
			case "-o": {
				outputFile = Paths.get(CLI.getArgValue(iterator, arg));
				break;
			}
			case "-fm": {
				fmFile = Paths.get(CLI.getArgValue(iterator, arg));
				break;
			}
			case "-l":
				limit = Integer.parseInt(CLI.getArgValue(iterator, arg));
				break;
			default: {
				remainingArguments.add(arg);
				break;
			}
			}
		}

		if (fmFile == null) {
			throw new IllegalArgumentException("No input file specified!");
		}
		if (outputFile == null) {
			throw new IllegalArgumentException("No output file specified!");
		}
		if (algorithm == null) {
			throw new IllegalArgumentException("No algorithm specified!");
		}
		final ConfigurationGenerator generator = algorithm.parseArguments(remainingArguments)
			.orElse(Logger::logProblems);
		if (generator != null) {
			generator.setLimit(limit);
			final ModelRepresentation c = FileHandler.load(fmFile, FormulaFormatManager.getInstance()) //
				.map(ModelRepresentation::new) //
				.orElseThrow(p -> new IllegalArgumentException(p.isEmpty() ? null : p.get(0).getError().get()));
			final Path out = outputFile;
			final Result<SolutionList> result = Executor.run(generator, c);
			result.ifPresentOrElse(list -> {
				try {
					FileHandler.save(list, out, new ConfigurationListFormat());
				} catch (final IOException e) {
					Logger.logError(e);
				}
			}, Logger::logProblems);
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
