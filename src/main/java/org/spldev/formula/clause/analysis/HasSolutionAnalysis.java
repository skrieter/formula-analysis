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
package org.spldev.formula.clause.analysis;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Determines whether a given {@link CNF} is satisfiable and returns the found
 * solution.
 *
 * @author Sebastian Krieter
 */
public class HasSolutionAnalysis extends Sat4JAnalysis<Boolean> {

	public static final Identifier<Boolean> identifier = new Identifier<>();

	@Override
	public Identifier<Boolean> getIdentifier() {
		return identifier;
	}

	@Override
	public Boolean analyze(Sat4JSolver solver, InternalMonitor monitor) throws Exception {
		final SatResult hasSolution = solver.hasSolution();
		switch (hasSolution) {
		case FALSE:
			return false;
		case TIMEOUT:
			reportTimeout();
			return false;
		case TRUE:
			return true;
		default:
			throw new AssertionError(hasSolution);
		}
	}

}
