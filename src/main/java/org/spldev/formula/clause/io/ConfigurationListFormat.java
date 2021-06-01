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
package org.spldev.formula.clause.io;

import java.util.*;
import java.util.regex.*;

import org.spldev.formula.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.util.*;
import org.spldev.util.Problem.*;
import org.spldev.util.io.format.*;

/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class ConfigurationListFormat implements Format<SolutionList> {

	public static final String ID = ConfigurationListFormat.class.getCanonicalName();

	@Override
	public String serialize(SolutionList configurationList) {
		final StringBuilder csv = new StringBuilder();
		csv.append("Configuration");
		final List<String> names = configurationList.getVariables().getNames();
		for (final String name : names) {
			csv.append(';');
			csv.append(name);
		}
		csv.append('\n');
		int configurationIndex = 0;
		for (final LiteralList configuration : configurationList.getSolutions()) {
			csv.append(configurationIndex++);
			final int[] literals = configuration.getLiterals();
			for (int i = 0; i < literals.length; i++) {
				csv.append(';');
				csv.append(literals[i] < 0 ? 0 : 1);
			}
			csv.append('\n');
		}
		return csv.toString();
	}

	@Override
	public Result<SolutionList> parse(CharSequence source) {
		int lineNumber = 0;
		final SolutionList configurationList = new SolutionList();
		try (final Scanner scanner = new Scanner(source.toString())) {
			scanner.useDelimiter(Pattern.compile("\\n+\\Z|\\n+|\\Z"));
			{
				final String line = scanner.next();
				if (line.trim().isEmpty()) {
					return Result.empty(new ParseProblem("Empty file!", lineNumber, Severity.ERROR));
				}
				final String[] names = line.split(";");
				configurationList.setVariables(new VariableMap(Arrays.asList(names).subList(1, names.length)));
			}

			while (scanner.hasNext()) {
				final String line = scanner.next();
				lineNumber++;
				final String[] split = line.split(";");
				if ((split.length - 1) != configurationList.getVariables().size()) {
					return Result.empty(new ParseProblem("Number of selections does not match number of features!",
						lineNumber, Severity.ERROR));
				}
				final int[] literals = new int[configurationList.getVariables().size()];
				for (int i = 1; i < split.length; i++) {
					literals[i - 1] = split[i].equals("0") ? -i : i;
				}
				configurationList.addSolution(new LiteralList(literals, Order.INDEX, false));
			}
		} catch (final Exception e) {
			return Result.empty(new ParseProblem(e.getMessage(), lineNumber, Severity.ERROR));
		}
		return Result.of(configurationList);
	}

	@Override
	public String getFileExtension() {
		return "csv";
	}

	@Override
	public ConfigurationListFormat getInstance() {
		return this;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public String getName() {
		return "ConfigurationList";
	}

}
