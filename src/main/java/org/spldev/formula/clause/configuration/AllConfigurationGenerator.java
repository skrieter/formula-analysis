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
package org.spldev.formula.clause.configuration;

import org.spldev.formula.clause.LiteralList;
import org.spldev.formula.clause.LiteralList.Order;
import org.spldev.formula.clause.solver.RuntimeContradictionException;

/**
 * Generates all configurations for a given propositional formula.
 *
 * @author Sebastian Krieter
 */
public class AllConfigurationGenerator extends ConfigurationGenerator {

	private boolean satisfiable = true;
	
	@Override
	public LiteralList get() {
		if (!satisfiable) {
			return null;
		}
		final int[] solution = solver.findSolution();
		if (solution == null) {
			satisfiable = false;
			return null;
		}
		final LiteralList result = new LiteralList(solution, Order.INDEX, false);
		try {
			solver.addClause(result.negate());
		} catch (final RuntimeContradictionException e) {
			satisfiable = false;
			return null;
		}
		return result;
	}


}
