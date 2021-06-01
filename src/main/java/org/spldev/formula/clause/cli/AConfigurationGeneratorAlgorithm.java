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

import java.util.List;
import java.util.ListIterator;

import org.spldev.formula.clause.configuration.ConfigurationGenerator;
import org.spldev.util.Result;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public abstract class AConfigurationGeneratorAlgorithm<T extends ConfigurationGenerator>
		implements ConfigurationGeneratorAlgorithm {

	public Result<ConfigurationGenerator> parseArguments(List<String> args) {
		final T gen = createConfigurationGenerator();
		try {
			for (final ListIterator<String> iterator = args.listIterator(); iterator.hasNext();) {
				final String arg = iterator.next();
				if (!parseArgument(gen, arg, iterator)) {
					throw new IllegalArgumentException("Unkown argument " + arg);
				}
			}
			return Result.of(gen);
		} catch (Exception e) {
			return Result.empty(e);
		}
	}

	protected abstract T createConfigurationGenerator();

	protected boolean parseArgument(T gen, String arg, ListIterator<String> iterator) throws IllegalArgumentException {
		return true;
	}

	@Override
	public String getId() {
		return getClass().getCanonicalName();
	}

}
