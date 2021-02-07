package org.spldev.formula.clause.analysis;

import java.util.*;

import org.sat4j.specs.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds redundant clauses with respect to a given {@link CNF}. This analysis
 * works by adding every clause group (see {@link AClauseAnalysis}) to the given
 * {@link CNF} at the beginning an then removing and readding each clause group
 * individually. If a clause group is redundant with respect to the current
 * formula, it is marked as redundant and removed completely from the
 * {@link CNF}. Otherwise it is kept as part of the {@link CNF} for the
 * remaining analysis. Clauses are added in the same order a they appear in the
 * given clauses list.<br>
 * For an independent analysis of every clause group use
 * {@link IndependentRedundancyAnalysis}.
 *
 * @author Sebastian Krieter
 *
 * @see AddRedundancyAnalysis
 * @see IndependentRedundancyAnalysis
 */
public class RemoveRedundancyAnalysis extends AClauseAnalysis<List<LiteralList>> {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	public RemoveRedundancyAnalysis() {
		super();
	}

	public RemoveRedundancyAnalysis(List<LiteralList> clauseList) {
		this.clauseList = clauseList;
	}

	@Override
	protected SatSolver initSolver(CNF satInstance) {
		try {
			return new Sat4JSolver(satInstance);
		} catch (final RuntimeContradictionException e) {
			return null;
		}
	}

	@Override
	public List<LiteralList> analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		if (clauseList == null) {
			return Collections.emptyList();
		}
		if (clauseGroupSize == null) {
			clauseGroupSize = new int[clauseList.size()];
			Arrays.fill(clauseGroupSize, 1);
		}
		monitor.setTotalWork(clauseGroupSize.length + 1);

		final List<LiteralList> resultList = new ArrayList<>(clauseGroupSize.length);
		for (int i = 0; i < clauseList.size(); i++) {
			resultList.add(null);
		}

		final List<IConstr> constrs = new ArrayList<>(clauseList.size());
		for (final LiteralList clause : clauseList) {
			constrs.add(solver.addClause(clause));
		}

		monitor.step();

		int endIndex = 0;
		for (int i = 0; i < clauseGroupSize.length; i++) {
			final int startIndex = endIndex;
			endIndex += clauseGroupSize[i];
			boolean completelyRedundant = true;
			boolean removedAtLeastOne = false;
			for (int j = startIndex; j < endIndex; j++) {
				final IConstr cm = constrs.get(j);
				if (cm != null) {
					removedAtLeastOne = true;
					solver.removeClause(cm);
				}
			}

			if (removedAtLeastOne) {
				for (int j = startIndex; j < endIndex; j++) {
					final LiteralList clause = clauseList.get(j);

					final SatResult hasSolution = solver.hasSolution(clause.negate());
					switch (hasSolution) {
					case FALSE:
						break;
					case TIMEOUT:
						reportTimeout();
						break;
					case TRUE:
						solver.addClause(clause);
						completelyRedundant = false;
						break;
					default:
						throw new AssertionError(hasSolution);
					}
				}
			}

			if (completelyRedundant) {
				resultList.set(i, clauseList.get(startIndex));
			}
			monitor.step();
		}

		return resultList;
	}

}
