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
import org.spldev.formula.solver.SatSolver.*;
import org.spldev.formula.solver.sat4j.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds contradicting clauses with respect to a given {@link CNF}. This
 * analysis works by adding and removing each clause group (see
 * {@link AClauseAnalysis}) to the given {@link CNF} individually. All clause
 * groups are analyzed separately without considering their
 * interdependencies.<br>
 * For a dependent analysis of all clause groups use
 * {@link ContradictionAnalysis}.
 *
 * @author Sebastian Krieter
 *
 * @see ContradictionAnalysis
 */
public class IndependentContradictionAnalysis extends AClauseAnalysis<List<LiteralList>> {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	public IndependentContradictionAnalysis() {
		super();
	}

	public IndependentContradictionAnalysis(List<LiteralList> clauseList) {
		super();
		this.clauseList = clauseList;
	}

	@Override
	public List<LiteralList> analyze(Sat4JSolver solver, InternalMonitor monitor) throws Exception {
		if (clauseList == null) {
			clauseList = solver.getCnf().getClauses();
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
		monitor.step();

		int endIndex = 0;
		for (int i = 0; i < clauseGroupSize.length; i++) {
			final int startIndex = endIndex;
			endIndex += clauseGroupSize[i];
			final List<LiteralList> subList = clauseList.subList(startIndex, endIndex);

			try {
				solver.getFormula().push(subList);
			} catch (final RuntimeContradictionException e) {
				resultList.set(i, clauseList.get(startIndex));
				monitor.step();
				continue;
			}

			final SatResult hasSolution = solver.hasSolution();
			switch (hasSolution) {
			case FALSE:
				resultList.set(i, clauseList.get(startIndex));
				break;
			case TIMEOUT:
				reportTimeout();
				break;
			case TRUE:
				break;
			default:
				throw new AssertionError(hasSolution);
			}

			solver.getFormula().pop(subList.size());
			monitor.step();
		}

		return resultList;
	}

}
