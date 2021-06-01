/* -----------------------------------------------------------------------------
 * Formula-Analysis-Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis-Lib.
 * 
 * Formula-Analysis-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.clause.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.spldev.formula.clause.CNF;
import org.spldev.formula.clause.Clauses;
import org.spldev.formula.clause.LiteralList;
import org.spldev.formula.clause.SolutionList;
import org.spldev.formula.clause.configuration.ConfigurationGenerator;
import org.spldev.formula.clause.configuration.ConfigurationSampler;
import org.spldev.formula.clause.io.ConfigurationListFormat;
import org.spldev.formula.expression.io.DIMACSFormat;
import org.spldev.util.Result;
import org.spldev.util.cli.CLI;
import org.spldev.util.cli.CLIFunction;
import org.spldev.util.extension.ExtensionPoint;
import org.spldev.util.io.FileHandler;
import org.spldev.util.job.Executor;
import org.spldev.util.logging.Logger;

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
		int limit = Integer.MAX_VALUE;

		final List<String> remainingArguments = new ArrayList<>();
		for (final ListIterator<String> iterator = args.listIterator(); iterator.hasNext();) {
			final String arg = iterator.next();
			switch (arg) {
			case "-a": {
				// TODO add plugin for icpl and chvatal
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
			case "-l":
				limit = Integer.parseInt(CLI.getArgValue(iterator, arg));
				break;
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
		final ConfigurationGenerator generator = algorithm.parseArguments(remainingArguments)
				.orElse(Logger::logProblems);
		if (generator != null) {
			ConfigurationSampler sampler = new ConfigurationSampler(generator, limit);

			final CNF cnf = FileHandler.parse(fmFile, new DIMACSFormat()).map(Clauses::convertToCNF)
					.orElseThrow(p -> new IllegalArgumentException(p.isEmpty() ? null : p.get(0).getError().get()));
			final Path out = outputFile;
			final Result<List<LiteralList>> result = Executor.run(sampler, cnf);
			result.ifPresentOrElse(list -> {
				try {
					FileHandler.serialize(new SolutionList(cnf.getVariableMap(), list), out, new ConfigurationListFormat());
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
