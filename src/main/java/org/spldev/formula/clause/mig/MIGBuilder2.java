package org.spldev.formula.clause.mig;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.formula.clause.mig.Vertex.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.job.*;

/**
 * Adjacency matrix implementation for a feature graph.
 *
 * @author Sebastian Krieter
 */

public class MIGBuilder2 {

	/**
	 * For sorting clauses by length. Starting with the longest.
	 */
	protected static final Comparator<LiteralList> lengthComparator = Comparator.comparing(o -> o
		.getLiterals().length);

	protected boolean checkRedundancy = true;
	protected boolean detectStrong = false;

	protected MIG mig;

	protected void init(CNF cnf) {
		mig = new MIG(cnf);
	}

	protected boolean getCoreFeatures(CNF cnf, InternalMonitor monitor) {
		monitor.setTotalWork(cnf.getVariableMap().size() + 2);
		// satisfiable?
		final SatSolver solver = new Sat4JSolver(cnf);
		solver.rememberSolutionHistory(0);
		final int[] firstSolution = solver.findSolution();
		monitor.step();
		if (firstSolution != null) {
			final Random random = new Random(112358);
			solver.setSelectionStrategy(firstSolution, true, true);
			LiteralList.resetConflicts(firstSolution, solver.findSolution());
			monitor.step();

			// find core/dead features
			for (int i = 0; i < firstSolution.length; i++) {
				final int varX = firstSolution[i];
				if (varX != 0) {
					solver.assignmentPush(-varX);
					final SatResult hasSolution = solver.hasSolution();
					switch (hasSolution) {
					case FALSE:
						solver.assignmentReplaceLast(varX);
						mig.getVertex(-varX).setStatus(Status.Dead);
						mig.getVertex(varX).setStatus(Status.Core);
						break;
					case TIMEOUT:
						solver.assignmentPop();
						break;
					case TRUE:
						solver.assignmentPop();
						LiteralList.resetConflicts(firstSolution, solver.getSolution());
						solver.shuffleOrder(random);
						break;
					}
				}
				monitor.step();
			}
			monitor.done();
			return true;
		}
		monitor.done();
		return false;
	}

	protected void addClauses(CNF cnf, InternalMonitor monitor) {
		monitor.setTotalWork(cnf.getClauses().size());
		Stream<LiteralList> stream = cnf.getClauses().stream()
			.map(c -> cleanClause(c, mig))
			.filter(Objects::nonNull)
			.distinct();
		if (checkRedundancy) {
			final Sat4JSolver newSolver = new Sat4JSolver(new CNF(cnf.getVariableMap()));
			stream = stream
				.sorted(lengthComparator)
				.peek(c -> monitor.step())
				.filter(clause -> //
				(clause.getLiterals().length < 3) //
					|| !isRedundant(newSolver, clause)) //
				.peek(newSolver::addClause); //
		} else {
			stream = stream.peek(c -> monitor.step());
		}
		stream.forEach(mig::addClause);
		monitor.done();
	}

	protected LiteralList cleanClause(LiteralList clause, MIG mig) {
		final int[] literals = clause.getLiterals();
		final LinkedHashSet<Integer> literalSet = new LinkedHashSet<>(literals.length << 1);

		// Sort out dead and core features
		int childrenCount = clause.size();
		for (int i = 0; i < childrenCount; i++) {
			final int var = literals[i];
			mig.size();
			final Status status = mig.getVertex(var).getStatus();
			switch (status) {
			case Core:
				return null;
			case Dead:
				if (childrenCount <= 2) {
					return null;
				}
				childrenCount--;
				// Switch literals (faster than deletion within an array)
				literals[i] = literals[childrenCount];
				literals[childrenCount] = var;
				i--;
				break;
			case Normal:
				if (literalSet.contains(-var)) {
					return null;
				} else {
					literalSet.add(var);
				}
				break;
			default:
				throw new IllegalStateException(String.valueOf(status));
			}
		}
		final int[] literalArray = new int[literalSet.size()];
		int i = 0;
		for (final int lit : literalSet) {
			literalArray[i++] = lit;
		}
		return new LiteralList(literalArray, Order.NATURAL);
	}

	protected final boolean isRedundant(SatSolver solver, LiteralList curClause) {
		return solver.hasSolution(curClause.negate()) == SatResult.FALSE;
	}

	protected void bfsStrong() {
		final boolean[] mark = new boolean[mig.size() + 1];
		final ArrayDeque<Vertex> queue = new ArrayDeque<>();
		for (final Vertex vertex : mig.getVertices()) {
			Arrays.fill(mark, false);
			final Vertex complement = mig.getVertex(-vertex.getVar());

			mark[Math.abs(vertex.getVar())] = true;
			for (final Vertex stronglyConnetedVertex : vertex.getStrongEdges()) {
				mark[Math.abs(stronglyConnetedVertex.getVar())] = true;
				queue.add(stronglyConnetedVertex);
			}
			while (!queue.isEmpty()) {
				final Vertex curVertex = queue.removeFirst();
				for (final Vertex stronglyConnetedVertex : curVertex.getStrongEdges()) {
					final int index = Math.abs(stronglyConnetedVertex.getVar());
					if (!mark[index]) {
						mark[index] = true;
						queue.add(stronglyConnetedVertex);

						final Vertex stronglyConnetedComplement = mig.getVertex(-stronglyConnetedVertex.getVar());
						vertex.addStronglyConnected(stronglyConnetedVertex);
						stronglyConnetedComplement.addStronglyConnected(complement);
					}
				}
			}
		}
	}

	protected void finish() {
		for (final Vertex vertex : mig.getVertices()) {
			Collections.sort(vertex.complexClauses);
			Collections.sort(vertex.stronglyConnetedVertices);
			vertex.complexClauses.trimToSize();
			vertex.stronglyConnetedVertices.trimToSize();
		}
	}

	public boolean isCheckRedundancy() {
		return checkRedundancy;
	}

	public void setCheckRedundancy(boolean checkRedundancy) {
		this.checkRedundancy = checkRedundancy;
	}

	public boolean isDetectStrong() {
		return detectStrong;
	}

	public void setDetectStrong(boolean detectStrong) {
		this.detectStrong = detectStrong;
	}

}
