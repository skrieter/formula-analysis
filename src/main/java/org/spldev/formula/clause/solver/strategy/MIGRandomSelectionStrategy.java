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

import org.sat4j.core.LiteralsUtils;
import org.sat4j.minisat.core.IPhaseSelectionStrategy;
import org.spldev.formula.clause.solver.SampleDistribution;

/**
 * Uses a sample of configurations to achieve a phase selection that corresponds
 * to a uniform distribution of configurations in the configuration space.
 *
 * @author Sebastian Krieter
 */
public class MIGRandomSelectionStrategy implements IPhaseSelectionStrategy {

	private static final long serialVersionUID = 1L;

	private final SampleDistribution dist;

	public MIGRandomSelectionStrategy(SampleDistribution sampleDistribution) {
		this.dist = sampleDistribution;
	}

	public void undo(int var) {
		dist.unset(var);
	}

	@Override
	public void assignLiteral(int p) {
		dist.set(LiteralsUtils.toDimacs(p));
	}

	@Override
	public void init(int nlength) {
	}

	@Override
	public void init(int var, int p) {
	}

	@Override
	public int select(int var) {
		return dist.getRandomLiteral(var) > 0 ? posLit(var) : negLit(var);
	}

	@Override
	public void updateVar(int p) {
	}

	@Override
	public void updateVarAtDecisionLevel(int q) {
	}

	@Override
	public String toString() {
		return "uniform random phase selection";
	}

}
