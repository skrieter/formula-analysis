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

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

/**
 * Finds clauses responsible for core and dead features.
 *
 * @author Sebastian Krieter
 */
public class CauseAnalysis extends AClauseAnalysis<List<CauseAnalysis.Anomalies>> {

	public static final Identifier<List<CauseAnalysis.Anomalies>> identifier = new Identifier<>();

	@Override
	public Identifier<List<CauseAnalysis.Anomalies>> getIdentifier() {
		return identifier;
	}

	public static class Anomalies {

		protected LiteralList deadVariables = new LiteralList();
		protected List<LiteralList> redundantClauses = Collections.emptyList();

		public LiteralList getDeadVariables() {
			return deadVariables;
		}

		public void setDeadVariables(LiteralList variables) {
			if (variables == null) {
				deadVariables = new LiteralList();
			} else {
				deadVariables = variables;
			}
		}

		public List<LiteralList> getRedundantClauses() {
			return redundantClauses;
		}

		public void setRedundantClauses(List<LiteralList> redundantClauses) {
			if (redundantClauses == null) {
				this.redundantClauses = Collections.emptyList();
			} else {
				this.redundantClauses = redundantClauses;
			}
		}

	}

	private Anomalies anomalies;
	protected boolean[] relevantConstraint;

	public Anomalies getAnomalies() {
		return anomalies;
	}

	public void setAnomalies(Anomalies anomalies) {
		this.anomalies = anomalies;
	}

	public boolean[] getRelevantConstraint() {
		return relevantConstraint;
	}

	public void setRelevantConstraint(boolean[] relevantConstraint) {
		this.relevantConstraint = relevantConstraint;
	}

	@Override
	public List<Anomalies> analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		if (clauseList == null) {
			return Collections.emptyList();
		}
		if (clauseGroupSize == null) {
			clauseGroupSize = new int[clauseList.size()];
			Arrays.fill(clauseGroupSize, 1);
		}
		final List<Anomalies> resultList = new ArrayList<>(clauseGroupSize.length);
		for (int i = 0; i < clauseList.size(); i++) {
			resultList.add(null);
		}
		if (anomalies == null) {
			return resultList;
		}
		monitor.setTotalWork(clauseList.size() + 3);

		LiteralList remainingVariables = anomalies.deadVariables.getVariables();
		final List<LiteralList> remainingClauses = new ArrayList<>(anomalies.redundantClauses);
		monitor.step();

		if (!remainingClauses.isEmpty()) {
			final List<LiteralList> result = Executor.run(
				new IndependentRedundancyAnalysis(remainingClauses),
				solver.getCnf()).orElse(Logger::logProblems);
			remainingClauses.removeIf(result::contains);
		}
		monitor.step();

		if (remainingVariables.getLiterals().length > 0) {
			remainingVariables = remainingVariables.removeAll(
				Executor.run(new CoreDeadAnalysis(remainingVariables),
					solver.getCnf()).orElse(Logger::logProblems));
		}
		monitor.step();

		int endIndex = 0;
		for (int i = 0; i < clauseGroupSize.length; i++) {
			if ((remainingVariables.getLiterals().length == 0) && remainingClauses.isEmpty()) {
				break;
			}

			final int startIndex = endIndex;
			endIndex += clauseGroupSize[i];
			solver.addClauses(clauseList.subList(startIndex, endIndex));
			if (relevantConstraint[i]) {
				if (remainingVariables.getLiterals().length > 0) {
					final LiteralList deadVariables = new CoreDeadAnalysis(remainingVariables).execute(solver,
						new NullMonitor());
					if (deadVariables.getLiterals().length != 0) {
						getAnomalies(resultList, i).setDeadVariables(deadVariables);
						remainingVariables = remainingVariables.removeAll(deadVariables);
					}
				}

				if (!remainingClauses.isEmpty()) {
					final List<LiteralList> newClauseList = new IndependentRedundancyAnalysis(remainingClauses).execute(
						solver, new NullMonitor());
					newClauseList.removeIf(Objects::isNull);
					if (!newClauseList.isEmpty()) {
						getAnomalies(resultList, i).setRedundantClauses(newClauseList);
						remainingClauses.removeAll(newClauseList);
					}
				}
			}

			monitor.step();
		}

		return resultList;
	}

	protected Anomalies getAnomalies(final List<Anomalies> resultList, final Integer curIndex) {
		Anomalies curAnomalies = resultList.get(curIndex);
		if (curAnomalies == null) {
			curAnomalies = new Anomalies();
			resultList.set(curIndex, curAnomalies);
		}
		return curAnomalies;
	}

}
