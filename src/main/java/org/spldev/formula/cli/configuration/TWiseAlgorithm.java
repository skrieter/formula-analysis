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

import java.nio.file.*;
import java.util.*;

import org.spldev.formula.analysis.sat4j.twise.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.io.*;
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
			case "-s":
				gen.setRandom(new Random(Long.parseLong(CLI.getArgValue(iterator, arg))));
				break;
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
			expressionGroups = FileHandler.load(expressionFile, new ExpressionGroupFormat())
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
