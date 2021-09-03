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
package org.spldev.formula.solver.sat4j;

import java.util.*;

import org.sat4j.core.*;
import org.sat4j.specs.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.clauses.LiteralList.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.solver.*;
import org.spldev.util.data.*;

/**
 * Base class for solvers using Sat4J.
 *
 * @author Sebastian Krieter
 */
public abstract class AbstractSat4JSolver<T extends ISolver> implements SolutionSolver<LiteralList> {

	public static final int MAX_SOLUTION_BUFFER = 1000;

	protected final CNF satInstance;

	protected final T solver;
	protected final Sat4JAssumptions assumptions;
	protected final Sat4JFormula formula;

	// TODO extract solution history in separate class
	protected LinkedList<LiteralList> solutionHistory = null;
	protected int solutionHistoryLimit = -1;
	protected int[] lastModel = null;

	protected boolean globalTimeout = false;

	private boolean contradiction = false;

	public AbstractSat4JSolver(CNF cnf) {
		satInstance = cnf;
		solver = createSolver();
		configureSolver();
		formula = new Sat4JFormula(this, cnf.getVariableMap());
		initSolver();

		assumptions = new Sat4JAssumptions(satInstance.getVariableMap());
	}

	/**
	 * @return The {@link CNF sat instance} given to the solver.
	 */
	public CNF getCnf() {
		return satInstance;
	}

	@Override
	public Sat4JAssumptions getAssumptions() {
		return assumptions;
	}

	@Override
	public Sat4JFormula getDynamicFormula() {
		return formula;
	}

	@Override
	public VariableMap getVariables() {
		return formula.getVariableMap();
	}

	/**
	 * Returns a copy of the last solution found by satisfiability solver. Can only
	 * be called after a successful call of {@link #hasSolution()} or
	 * {@link #hasSolution(int...)}.
	 *
	 * @return A {@link LiteralList} representing the satisfying assignment.
	 *
	 * @see #hasSolution()
	 * @see #hasSolution(int...)
	 */
	@Override
	public LiteralList getSolution() {
		return new LiteralList(getLastModelCopy(), Order.INDEX, false);
	}

	public int[] getInternalSolution() {
		return lastModel;
	}

	private int[] getLastModelCopy() {
		return Arrays.copyOf(lastModel, lastModel.length);
	}

	/**
	 * Checks whether there is a satisfying solution considering the clauses of the
	 * solver and the given variable assignment.
	 *
	 * @param assignment The temporarily variable assignment for this call.
	 * @return A {@link SatResult}.
	 *
	 * @see #hasSolution()
	 * @see #hasSolution(int...)
	 * @see #getInternalSolution()
	 */
	public SatResult hasSolution(LiteralList assignment) {
		return hasSolution(assignment.getLiterals());
	}

	/**
	 * Completely resets the solver, removing all its assignments, variables, and
	 * clauses.
	 */
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
	protected abstract T createSolver();

	/**
	 * Add clauses to the solver. Initializes the order instance.
	 */
	protected void initSolver() {
		final int size = satInstance.getVariableMap().getMaxIndex();
		final List<LiteralList> clauses = satInstance.getClauses();
		try {
			if (!clauses.isEmpty()) {
				solver.setExpectedNumberOfClauses(clauses.size() + 1);
				formula.push(clauses);
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
	}

	public void setTimeout(int timeout) {
		solver.setTimeoutMs(timeout);
	}

	public Sat4JFormula getFormula() {
		return formula;
	}

	@Override
	public LiteralList findSolution() {
		return hasSolution() == SatResult.TRUE ? getSolution() : null;
	}

	public List<LiteralList> getSolutionHistory() {
		return solutionHistory != null ? Collections.unmodifiableList(solutionHistory) : Collections.emptyList();
	}

	private int[] getAssumptionArray() {
		final List<Pair<Integer, Object>> all = assumptions.getAll();
		final int[] literals = new int[all.size()];
		int index = 0;
		for (final Pair<Integer, Object> entry : all) {
			final int variable = entry.getKey();
			literals[index++] = (entry.getValue() == Boolean.TRUE) ? variable : -variable;
		}
		return literals;
	}

	/**
	 * Checks whether there is a satisfying solution considering the clauses of the
	 * solver.
	 *
	 * @return A {@link SatResult}.
	 *
	 * @see #hasSolution(LiteralList)
	 * @see #hasSolution(int...)
	 * @see #getInternalSolution()
	 */
	@Override
	public SatResult hasSolution() {
		if (contradiction) {
			lastModel = null;
			return SatResult.FALSE;
		}

		final int[] assumptionArray = getAssumptionArray();
		if (solutionHistory != null) {
			for (final LiteralList solution : solutionHistory) {
				if (solution.containsAllLiterals(assumptionArray)) {
					lastModel = solution.getLiterals();
					return SatResult.TRUE;
				}
			}
		}

		try {
			if (solver.isSatisfiable(new VecInt(assumptionArray), globalTimeout)) {
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
	 * Checks whether there is a satisfying solution considering the clauses of the
	 * solver and the given variable assignment.<br>
	 * Does only consider the given {@code assignment} and <b>not</b> the global
	 * assignment variable of the solver.
	 *
	 * @param assignment The temporarily variable assignment for this call.
	 * @return A {@link SatResult}.
	 *
	 * @see #hasSolution(LiteralList)
	 * @see #hasSolution()
	 * @see #getInternalSolution()
	 */
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
			solutionHistory.addFirst(getSolution());
			if (solutionHistory.size() > solutionHistoryLimit) {
				solutionHistory.removeLast();
			}
		}
	}

	public int[] getContradictoryAssignment() {
		final IVecInt unsatExplanation = solver.unsatExplanation();
		return Arrays.copyOf(unsatExplanation.toArray(), unsatExplanation.size());
	}

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

	public boolean isGlobalTimeout() {
		return globalTimeout;
	}

	public void setGlobalTimeout(boolean globalTimeout) {
		this.globalTimeout = globalTimeout;
	}

	protected void configureSolver() {
		solver.setTimeoutMs(1_000_000);
		solver.setDBSimplificationAllowed(true);
		solver.setKeepSolverHot(true);
		solver.setVerbose(false);
	}

}
