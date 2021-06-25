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
package org.spldev.formula.clause.solver.strategy;

import static org.sat4j.core.LiteralsUtils.negLit;
import static org.sat4j.core.LiteralsUtils.posLit;

import java.util.Random;

import org.sat4j.minisat.core.IPhaseSelectionStrategy;

/**
 * Random phase selection.
 *
 * @author Sebastian Krieter
 */
public class RandomSelectionStrategy implements IPhaseSelectionStrategy {

	private static final long serialVersionUID = 1L;

	public final Random RAND = new Random(123456789);

	@Override
	public void assignLiteral(int p) {}

	@Override
	public void init(int nlength) {}

	@Override
	public void init(int var, int p) {}

	@Override
	public int select(int var) {
		return RAND.nextBoolean() ? posLit(var) : negLit(var);
	}

	@Override
	public void updateVar(int p) {}

	@Override
	public void updateVarAtDecisionLevel(int q) {}

	@Override
	public String toString() {
		return "random phase selection";
	}

}
