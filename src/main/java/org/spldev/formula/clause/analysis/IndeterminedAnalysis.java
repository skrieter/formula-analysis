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

import java.util.*;

import org.sat4j.core.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.formula.clause.transform.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

/**
 * Finds indetermined features.
 *
 * @author Sebastian Krieter
 */
public class IndeterminedAnalysis extends AVariableAnalysis<LiteralList> {

	public static final Identifier<LiteralList> identifier = new Identifier<>();

	@Override
	public Identifier<LiteralList> getIdentifier() {
		return identifier;
	}

	@Override
	public LiteralList analyze(Sat4JSolver solver, InternalMonitor monitor) throws Exception {
		if (variables == null) {
			variables = LiteralList.getVariables(solver.getCnf());
		}
		monitor.setTotalWork(2 * variables.getLiterals().length);

		final VecInt potentialResultList = new VecInt();
		final List<LiteralList> relevantClauses = new ArrayList<>();

		final Sat4JSolver modSolver = new Sat4JSolver(solver.getCnf());
		for (final int literal : variables.getLiterals()) {
			final List<LiteralList> clauses = solver.getCnf().getClauses();
			for (final LiteralList clause : clauses) {
				if (clause.containsAnyVariable(literal)) {
					final LiteralList newClause = clause.removeVariables(literal);
					if (newClause != null) {
						relevantClauses.add(newClause);
					}
				}
			}
			try {
				modSolver.addClauses(relevantClauses);
			} catch (final RuntimeContradictionException e) {
				relevantClauses.clear();
				monitor.step();
				continue;
			}

			final SatResult hasSolution = modSolver.hasSolution();
			switch (hasSolution) {
			case FALSE:
				break;
			case TIMEOUT:
				reportTimeout();
				break;
			case TRUE:
				potentialResultList.push(literal);
				break;
			default:
				throw new AssertionError(hasSolution);
			}
			modSolver.removeLastClauses(relevantClauses.size());

			relevantClauses.clear();
			monitor.step();
		}

		final VecInt resultList = new VecInt();
		while (!potentialResultList.isEmpty()) {
			final int literal = potentialResultList.last();
			potentialResultList.pop();
			final CNF slicedCNF = Executor.run(new CNFSlicer(variables.removeAll(new LiteralList(literal))), solver
				.getCnf()).orElse(Logger::logProblems);
			final List<LiteralList> clauses = slicedCNF.getClauses();
			for (final LiteralList clause : clauses) {
				if (clause.containsAnyVariable(literal)) {
					final LiteralList newClause = clause.removeVariables(literal);
					if (newClause != null) {
						relevantClauses.add(newClause);
					}
				}
			}
			try {
				modSolver.addClauses(relevantClauses);
			} catch (final RuntimeContradictionException e) {
				relevantClauses.clear();
				monitor.step();
				continue;
			}

			final SatResult hasSolution = modSolver.hasSolution();
			switch (hasSolution) {
			case FALSE:
				break;
			case TIMEOUT:
				reportTimeout();
				break;
			case TRUE:
				resultList.push(literal);
				break;
			default:
				throw new AssertionError(hasSolution);
			}
			modSolver.removeLastClauses(relevantClauses.size());

			relevantClauses.clear();
			monitor.step();
		}

		return new LiteralList(Arrays.copyOf(resultList.toArray(), resultList.size()));
	}

}
