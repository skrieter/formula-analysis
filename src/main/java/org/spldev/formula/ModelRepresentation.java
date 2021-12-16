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
package org.spldev.formula;

import java.nio.file.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.io.*;
import org.spldev.util.data.*;
import org.spldev.util.io.*;
import org.spldev.util.logging.*;

/**
 * Representation of a feature model as a formula {@link #formula}, where
 * features are mapped to {@link #variables}. Analysis results are stored in a
 * {@link #cache} for later reuse.
 */
public class ModelRepresentation {

	private final Cache cache = new Cache();
	private final Formula formula;
	private final VariableMap variables;

	public static Result<ModelRepresentation> load(final Path modelFile) {
		return FileHandler.load(modelFile, FormulaFormatManager.getInstance()) //
			.map(ModelRepresentation::new);
	}

	public ModelRepresentation(Formula formula) {
		this(formula, VariableMap.fromExpression(formula));
	}

	public ModelRepresentation(Formula formula, VariableMap variables) {
		this.formula = formula;
		this.variables = variables;
		cache.set(FormulaProvider.of(formula));
	}

	public <T> Result<T> getResult(Provider<T> provider) {
		return cache.get(provider, null);
	}

	public <T> T get(Provider<T> provider) {
		return cache.get(provider).orElse(Logger::logProblems);
	}

	public Cache getCache() {
		return cache;
	}

	public Formula getFormula() {
		return formula;
	}

	public VariableMap getVariables() {
		return variables;
	}

}
