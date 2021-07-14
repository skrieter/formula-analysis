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
package org.spldev.formula.clause.solver;

import java.util.*;

import org.sat4j.core.*;
import org.sat4j.minisat.*;
import org.sat4j.minisat.core.*;
import org.sat4j.minisat.orders.*;
import org.sat4j.specs.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.formula.clause.solver.SStrategy.*;
import org.spldev.formula.clause.solver.strategy.*;

/**
 * Sat solver with advanced support.
 *
 * @author Sebastian Krieter
 */
public class Sat4JSolver implements SatSolver {

	protected final CNF satInstance;

	protected final Solver<?> solver;
	protected final ArrayList<IConstr> constrList = new ArrayList<>();
	protected final VecInt assignment;
	protected final int[] order;

	protected LinkedList<LiteralList> solutionHistory = null;
	protected int solutionHistoryLimit = -1;
	protected int[] lastModel = null;
	protected SStrategy strategy;

	protected boolean globalTimeout = false;

	private boolean contradiction = false;

	public Sat4JSolver(CNF satInstance) {
		this.satInstance = satInstance;
		solver = createSolver();
		configureSolver();
		initSolver();

		strategy = SStrategy.original();

		assignment = new VecInt(satInstance.getVariableMap().size());
		order = new int[satInstance.getVariableMap().size()];
		setOrderFix();
	}

	protected Sat4JSolver(Sat4JSolver oldSolver) {
		satInstance = oldSolver.satInstance;
		solver = createSolver();
		configureSolver();
		initSolver();

		strategy = oldSolver.strategy;

		assignment = new VecInt(0);
		oldSolver.assignment.copyTo(assignment);
		order = Arrays.copyOf(oldSolver.order, oldSolver.order.length);
	}

	@Override
	public IConstr addClause(LiteralList clause) throws RuntimeContradictionException {
		final IConstr constr = addClause(clause.getLiterals());
		if (solutionHistory != null) {
			solutionHistory.clear();
			lastModel = null;
		}
		return constr;
	}

	@Override
	public List<IConstr> addClauses(Collection<? extends LiteralList> clauses) throws RuntimeContradictionException {
		int addCount = 0;
		for (final LiteralList clause : clauses) {
			try {
				addClause(clause.getLiterals());
				addCount++;
			} catch (final RuntimeContradictionException e) {
				removeLastClauses(addCount);
				throw e;
			}
		}
		if (solutionHistory != null) {
			solutionHistory.clear();
			lastModel = null;
		}
		return constrList;
	}

	public IConstr addClause(int[] literals) throws RuntimeContradictionException {
		if ((literals.length == 1) && (literals[0] == 0)) {
			throw new RuntimeContradictionException();
		}
		try {
			final IConstr constr = solver.addClause(new VecInt(Arrays.copyOfRange(literals, 0, literals.length)));
			constrList.add(constr);
			return constr;
		} catch (final ContradictionException e) {
			throw new RuntimeContradictionException(e);
		}
	}

	@Override
	public CNF getCnf() {
		return satInstance;
	}

	@Override
	public int[] getSolution() {
		return lastModel;
	}

	@Override
	public SatResult hasSolution(LiteralList assignment) {
		return hasSolution(assignment.getLiterals());
	}

	@Override
	public void removeLastClause() throws RuntimeContradictionException {
		removeLastClauses(1);
	}

	@Override
	public void reset() {
		solver.reset();
		if (solutionHistory != null) {
			solutionHistory.clear();
			lastModel = null;
		}
	}

	/**
	 * Creates the Sat4J solver instance.
	 * 
	 * @return Sat4J solver
	 */
	protected Solver<?> createSolver() {
		return (Solver<?>) SolverFactory.newDefault();
	}

	/**
	 * Add clauses to the solver. Initializes the order instance.
	 */
	protected void initSolver() {
		final int size = satInstance.getVariableMap().getMaxIndex();
		final List<LiteralList> clauses = satInstance.getClauses();
		try {
			if (!clauses.isEmpty()) {
				solver.setExpectedNumberOfClauses(clauses.size() + 1);
				addClauses(clauses);
			}
			if (size > 0) {
				final VecInt pseudoClause = new VecInt(size + 1);
				for (int i = 1; i <= size; i++) {
					pseudoClause.push(i);
				}
				pseudoClause.push(-1);
				solver.addClause(pseudoClause);
			}
		} catch (final Exception e) {
			contradiction = true;
		}
		solver.getOrder().init();
	}

	@Override
	public void setTimeout(int timeout) {
		solver.setTimeoutMs(timeout);
	}

	@Override
	public void assignmentClear(int size) {
		assignment.shrinkTo(size);
	}

	@Override
	public void asignmentEnsure(int size) {
		assignment.ensure(size);
	}

	@Override
	public void assignmentPop() {
		assignment.pop();
	}

	@Override
	public void assignmentPush(int var) {
		assignment.push(var);
	}

	@Override
	public void assignmentPushAll(int[] vars) {
		assignment.pushAll(new VecInt(vars));
	}

	@Override
	public void assignmentReplaceLast(int var) {
		assignment.pop().unsafePush(var);
	}

	@Override
	public void assignmentDelete(int i) {
		assignment.delete(i);
	}

	@Override
	public void assignmentSet(int index, int var) {
		assignment.set(index, var);
	}

	@Override
	public int getAssignmentSize() {
		return assignment.size();
	}

	@Override
	public Sat4JSolver clone() {
		if (this.getClass() == Sat4JSolver.class) {
			return new Sat4JSolver(this);
		} else {
			throw new RuntimeException("Cloning not supported for " + this.getClass().toString());
		}
	}

	@Override
	public int[] findSolution() {
		return hasSolution() == SatResult.TRUE ? getSolution() : null;
	}

	@Override
	public int[] getAssignmentArray() {
		return Arrays.copyOf(assignment.toArray(), assignment.size());
	}

	@Override
	public int[] getAssignmentArray(int from) {
		return Arrays.copyOfRange(assignment.toArray(), from, assignment.size());
	}

	@Override
	public int[] getAssignmentArray(int from, int to) {
		return Arrays.copyOfRange(assignment.toArray(), from, to);
	}

	@Override
	public int assignmentGet(int i) {
		return assignment.get(i);
	}

	@Override
	public int[] getOrder() {
		return order;
	}

	@Override
	public SStrategy getSelectionStrategy() {
		return strategy;
	}

	@Override
	public List<LiteralList> getSolutionHistory() {
		return solutionHistory != null ? Collections.unmodifiableList(solutionHistory) : Collections.emptyList();
	}

	@Override
	public SatResult hasSolution() {
		if (contradiction) {
			lastModel = null;
			return SatResult.FALSE;
		}

		if (solutionHistory != null) {
			final int[] array = Arrays.copyOf(assignment.toArray(), assignment.size());
			for (final LiteralList solution : solutionHistory) {
				if (solution.containsAllLiterals(array)) {
					lastModel = solution.getLiterals();
					return SatResult.TRUE;
				}
			}
		}

		try {
			if (solver.isSatisfiable(assignment, globalTimeout)) {
				lastModel = solver.model();
				addSolution();
				return SatResult.TRUE;
			} else {
				lastModel = null;
				return SatResult.FALSE;
			}
		} catch (final TimeoutException e) {
			lastModel = null;
			return SatResult.TIMEOUT;
		}
	}

	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * Does only consider the given {@code assignment} and <b>not</b> the global
	 * assignment variable of the solver.
	 */
	@Override
	public SatResult hasSolution(int... assignment) {
		if (contradiction) {
			return SatResult.FALSE;
		}

		if (solutionHistory != null) {
			for (final LiteralList solution : solutionHistory) {
				if (solution.containsAllLiterals(assignment)) {
					lastModel = solution.getLiterals();
					return SatResult.TRUE;
				}
			}
		}

		final int[] unitClauses = new int[assignment.length];
		System.arraycopy(assignment, 0, unitClauses, 0, unitClauses.length);

		try {
			// TODO why is this necessary?
			if (solver.isSatisfiable(new VecInt(unitClauses), globalTimeout)) {
				lastModel = solver.model();
				addSolution();
				return SatResult.TRUE;
			} else {
				lastModel = null;
				return SatResult.FALSE;
			}
		} catch (final TimeoutException e) {
			lastModel = null;
			return SatResult.TIMEOUT;
		}
	}

	private void addSolution() {
		if (solutionHistory != null) {
			solutionHistory.addFirst(new LiteralList(getSolution(), Order.INDEX, false));
			if (solutionHistory.size() > solutionHistoryLimit) {
				solutionHistory.removeLast();
			}
		}
	}

	@Override
	public int[] getContradictoryAssignment() {
		final IVecInt unsatExplanation = solver.unsatExplanation();
		return Arrays.copyOf(unsatExplanation.toArray(), unsatExplanation.size());
	}

	@Override
	public void setOrder(int[] order) {
		assert order.length <= this.order.length;
		System.arraycopy(order, 0, this.order, 0, order.length);
	}

	@Override
	public void setOrderFix() {
		for (int i = 0; i < order.length; i++) {
			order[i] = i + 1;
		}
	}

	@Override
	public void shuffleOrder() {
		shuffleOrder(new Random());
	}

	@Override
	public void shuffleOrder(Random rnd) {
		for (int i = order.length - 1; i >= 0; i--) {
			final int index = rnd.nextInt(i + 1);
			final int a = order[index];
			order[index] = order[i];
			order[i] = a;
		}
	}

	private void setSelectionStrategy(IOrder strategy) {
		solver.setOrder(strategy);
		solver.getOrder().init();
	}

	@Override
	public void setSelectionStrategy(SStrategy strategy) {
		this.strategy = strategy;
		switch (strategy.strategy()) {
		case FastRandom:
			setSelectionStrategy(new FixedOrderHeap(new RandomSelectionStrategy(), order));
			break;
		case Fixed:
			setSelectionStrategy(new FixedOrderHeap( //
				new FixedLiteralSelectionStrategy(((FixedStrategy) strategy).getModel()), //
				order));
			break;
		case InverseFixed:
			setSelectionStrategy(new FixedOrderHeap( //
				new FixedLiteralSelectionStrategy(((InverseFixedStrategy) strategy).getModel()), //
				order));
			break;
		case MIGRandom:
			setSelectionStrategy(new FixedOrderHeap2(new UniformRandomSelectionStrategy(
				((MIGRandomStrategy) strategy).getDist()), order));
			break;
		case Negative:
			setSelectionStrategy(new FixedOrderHeap(new NegativeLiteralSelectionStrategy(), order));
			break;
		case Original:
			setSelectionStrategy(new VarOrderHeap(new RSATPhaseSelectionStrategy()));
			break;
		case Positive:
			setSelectionStrategy(new FixedOrderHeap(new PositiveLiteralSelectionStrategy(), order));
			break;
		case UniformRandom:
			setSelectionStrategy(new FixedOrderHeap2(new UniformRandomSelectionStrategy(
				((UniformRandomStrategy) strategy).getDist()), order));
			break;
		default:
			throw new IllegalStateException(String.valueOf(strategy.strategy()));
		}
	}

	@Override
	public List<LiteralList> rememberSolutionHistory(int numberOfSolutions) {
		if (numberOfSolutions > 0) {
			solutionHistory = new LinkedList<>();
			solutionHistoryLimit = numberOfSolutions;
		} else {
			solutionHistory = null;
			solutionHistoryLimit = -1;
		}
		return getSolutionHistory();
	}

	@Override
	public boolean isGlobalTimeout() {
		return globalTimeout;
	}

	@Override
	public void setGlobalTimeout(boolean globalTimeout) {
		this.globalTimeout = globalTimeout;
	}

	@Override
	public void removeClause(IConstr constr) throws RuntimeContradictionException {
		if (constr != null) {
			try {
				solver.removeConstr(constr);
			} catch (final Exception e) {
				throw new RuntimeContradictionException(e);
			}
		}
	}

	@Override
	public void removeLastClauses(int numberOfClauses) throws RuntimeContradictionException {
		try {
			final int size = constrList.size();
			final List<IConstr> lastClauses = constrList.subList(size - numberOfClauses, size);
			Collections.reverse(lastClauses);
			lastClauses.stream().filter(Objects::nonNull).forEach(solver::removeSubsumedConstr);
			lastClauses.clear();
			solver.clearLearntClauses();
		} catch (final Exception e) {
			throw new RuntimeContradictionException(e);
		}
	}

	protected void configureSolver() {
		solver.setTimeoutMs(1_000_000);
		solver.setDBSimplificationAllowed(true);
		solver.setKeepSolverHot(true);
		solver.setVerbose(false);
	}

}
