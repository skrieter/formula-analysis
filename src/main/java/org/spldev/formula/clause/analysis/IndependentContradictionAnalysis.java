package org.spldev.formula.clause.analysis;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
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

	@Override
	protected SatSolver initSolver(CNF satInstance) {
		try {
			return new Sat4JSolver(satInstance);
		} catch (final RuntimeContradictionException e) {
			return null;
		}
	}

	public IndependentContradictionAnalysis() {
		super();
	}

	public IndependentContradictionAnalysis(List<LiteralList> clauseList) {
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
				break;
			case TIMEOUT:
				reportTimeout();
				break;
			case TRUE:
				break;
			default:
				throw new AssertionError(hasSolution);
			}

			solver.removeLastClauses(subList.size());
			monitor.step();
		}

		return resultList;
	}

}
