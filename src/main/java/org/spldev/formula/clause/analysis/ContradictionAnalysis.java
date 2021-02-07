package org.spldev.formula.clause.analysis;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds contradicting clauses with respect to a given {@link CNF}. This
 * analysis works by iteratively adding each clause group (see
 * {@link AClauseAnalysis}) to the given {@link CNF}. If a clause group
 * contradicts the current formula, it is marked as a contradiction and removed
 * from the {@link CNF}. Otherwise it is kept as part of the {@link CNF} for the
 * remaining analysis. Clauses are added in the same order a they appear in the
 * given clauses list.<br>
 * For an independent analysis of every clause group use
 * {@link IndependentContradictionAnalysis}.
 *
 * @author Sebastian Krieter
 *
 * @see IndependentContradictionAnalysis
 */
public class ContradictionAnalysis extends AClauseAnalysis<List<LiteralList>> {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	@Override
	protected SatSolver initSolver(CNF satInstance) {
		try {
			return new Sat4JSolver(satInstance);
		} catch (final RuntimeContradictionException e) {
			return null;
		}
	}

	public ContradictionAnalysis() {
		super();
	}

	public ContradictionAnalysis(List<LiteralList> clauseList) {
		super();
		this.clauseList = clauseList;
	}

	@Override
	public List<LiteralList> analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
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
				solver.addClauses(subList);
			} catch (final RuntimeContradictionException e) {
				resultList.set(i, clauseList.get(startIndex));
				monitor.step();
				continue;
			}

			final SatResult hasSolution = solver.hasSolution();
			switch (hasSolution) {
			case FALSE:
				resultList.set(i, clauseList.get(startIndex));
				solver.removeLastClauses(subList.size());
				break;
			case TIMEOUT:
				reportTimeout();
				break;
			case TRUE:
				break;
			default:
				throw new AssertionError(hasSolution);
			}

			monitor.step();
		}

		return resultList;
	}

}
