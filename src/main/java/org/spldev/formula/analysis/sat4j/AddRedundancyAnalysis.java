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
import org.spldev.formula.solver.SatSolver.*;
import org.spldev.formula.solver.sat4j.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds redundant clauses with respect to a given {@link CNF}. This analysis
 * works by iteratively adding each clause group (see {@link AClauseAnalysis})
 * to the given {@link CNF}. If a clause group is redundant with respect to the
 * current formula, it is marked as redundant and removed from the {@link CNF}.
 * Otherwise it is kept as part of the {@link CNF} for the remaining analysis.
 * Clauses are added in the same order a they appear in the given clauses
 * list.<br>
 * For an independent analysis of every clause group use
 * {@link IndependentRedundancyAnalysis}.
 *
 * @author Sebastian Krieter
 *
 * @see RemoveRedundancyAnalysis
 * @see IndependentRedundancyAnalysis
 */
public class AddRedundancyAnalysis extends AClauseAnalysis<List<LiteralList>> {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	public AddRedundancyAnalysis() {
		super();
	}

	public AddRedundancyAnalysis(List<LiteralList> clauseList) {
		super();
		this.clauseList = clauseList;
	}

	@Override
	public List<LiteralList> analyze(Sat4JSolver solver, InternalMonitor monitor) throws Exception {
		if (clauseList == null) {
			return Collections.emptyList();
		}
		if (clauseGroupSize == null) {
			clauseGroupSize = new int[clauseList.size()];
			Arrays.fill(clauseGroupSize, 1);
		}
		monitor.setTotalWork(clauseList.size() + 1);

		final List<LiteralList> resultList = new ArrayList<>(clauseGroupSize.length);
		for (int i = 0; i < clauseList.size(); i++) {
			resultList.add(null);
		}
		// TODO Find a better way of sorting
		// final Integer[] index = Functional.getSortedIndex(resultList, new
		// ClauseLengthComparatorDsc());
		monitor.step();

		int endIndex = 0;
		for (int i = 0; i < clauseGroupSize.length; i++) {
			final int startIndex = endIndex;
			endIndex += clauseGroupSize[i];
			boolean completelyRedundant = true;
			for (int j = startIndex; j < endIndex; j++) {
				final LiteralList clause = clauseList.get(j);
				final SatResult hasSolution = solver.hasSolution(clause.negate());
				switch (hasSolution) {
				case FALSE:
					break;
				case TIMEOUT:
					reportTimeout();
				case TRUE:
					solver.getFormula().push(clause);
					completelyRedundant = false;
					break;
				default:
					throw new AssertionError(hasSolution);
				}
			}
			if (completelyRedundant) {
				resultList.set(i, clauseList.get(startIndex));
			}
		}

		return resultList;
	}

}
