package org.spldev.formula.clause.analysis;

import java.util.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds redundant clauses with respect to a given {@link CNF}. This analysis
 * works by adding and removing each clause group (see {@link AClauseAnalysis})
 * to the given {@link CNF} individually. All clause groups are analyzed
 * separately without considering their interdependencies.<br>
 * For a dependent analysis of all clause groups use
 * {@link RemoveRedundancyAnalysis}.
 *
 * @author Sebastian Krieter
 *
 * @see RemoveRedundancyAnalysis
 */
public class IndependentRedundancyAnalysis extends AClauseAnalysis<List<LiteralList>> {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	public IndependentRedundancyAnalysis() {
		super();
	}

	public IndependentRedundancyAnalysis(List<LiteralList> clauseList) {
		super();
		this.clauseList = clauseList;
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
		monitor.setTotalWork(clauseList.size() + 1);

		final List<LiteralList> resultList = new ArrayList<>(clauseGroupSize.length);
		for (int i = 0; i < clauseList.size(); i++) {
			resultList.add(null);
		}
		monitor.step();

		final List<LiteralList> solutionList = solver.rememberSolutionHistory(SatSolver.MAX_SOLUTION_BUFFER);

		final int[] firstSolution = solver.findSolution();
		if (firstSolution != null) {
			solver.setSelectionStrategy(SelectionStrategy.RANDOM);

			int endIndex = 0;
			groupLoop: for (int i = 0; i < clauseGroupSize.length; i++) {
				final int startIndex = endIndex;
				endIndex += clauseGroupSize[i];
				clauseLoop: for (int j = startIndex; j < endIndex; j++) {
					final LiteralList clause = clauseList.get(j);
					final LiteralList complement = clause.negate();

					for (final LiteralList solution : solutionList) {
						if (solution.containsAll(complement)) {
							continue clauseLoop;
						}
					}

					final SatResult hasSolution = solver.hasSolution(complement);
					switch (hasSolution) {
					case FALSE:
						resultList.set(i, clause);
						continue groupLoop;
					case TIMEOUT:
						reportTimeout();
						break;
					case TRUE:
						solver.shuffleOrder(getRandom());
						break;
					default:
						throw new AssertionError(hasSolution);
					}
				}
			}
		}

		return resultList;
	}

}
