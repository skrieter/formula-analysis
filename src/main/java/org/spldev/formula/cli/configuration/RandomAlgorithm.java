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

import java.util.*;

import org.spldev.formula.analysis.sat4j.*;
import org.spldev.util.cli.*;

/**
 * Generates random configurations for a given propositional formula.
 *
 * @author Sebastian Krieter
 */
public abstract class RandomAlgorithm<T extends RandomConfigurationGenerator>
	extends AConfigurationGeneratorAlgorithm<T> {

	@Override
	protected boolean parseArgument(T gen, String arg, ListIterator<String> iterator) throws IllegalArgumentException {
		switch (arg) {
		case "-s":
			gen.setRandom(new Random(Long.parseLong(CLI.getArgValue(iterator, arg))));
			break;
		default:
			return false;
		}
		return true;
	}

}
