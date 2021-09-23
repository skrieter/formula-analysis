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
package org.spldev.formula.analysis.sat4j;

import java.util.*;

import org.spldev.formula.clauses.*;
import org.spldev.formula.solver.*;
import org.spldev.util.job.*;

/**
 * Finds random valid solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public abstract class RandomConfigurationGenerator extends AbstractConfigurationGenerator {

	protected boolean satisfiable = true;

	public RandomConfigurationGenerator() {
		super();
		setRandom(new Random());
	}

	@Override
	protected void init(InternalMonitor monitor) {
		super.init(monitor);
		satisfiable = true;
	}

	@Override
	public LiteralList get() {
		if (!satisfiable) {
			return null;
		}
		reset();
		solver.shuffleOrder(random);
		final LiteralList solution = solver.findSolution();
		if (solution == null) {
			satisfiable = false;
			return null;
		}
		if (!allowDuplicates) {
			try {
				forbidSolution(solution.negate());
			} catch (final RuntimeContradictionException e) {
				satisfiable = false;
			}
		}
		return solution;
	}

	protected void forbidSolution(final LiteralList negate) {
		solver.getFormula().push(negate);
	}

	protected void reset() {
	}

}
