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
package org.spldev.formula.solver.sat4j;

import org.sat4j.specs.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.solver.SatSolver.*;

public abstract class ModelComparator {

	public static boolean eq(CNF cnf1, final CNF cnf2) throws TimeoutException {
		return compare(cnf2, cnf1) && compare(cnf1, cnf2);
	}

	public static boolean compare(CNF cnf1, final CNF cnf2) throws TimeoutException {
		final Sat4JSolver solver = new Sat4JSolver(cnf1);
		for (final LiteralList clause : cnf2.getClauses()) {
			final SatResult satResult = solver.hasSolution(clause.negate());
			switch (satResult) {
			case FALSE:
				break;
			case TIMEOUT:
				throw new TimeoutException();
			case TRUE:
				return false;
			default:
				assert false;
			}
		}
		return true;
	}

}
