package org.spldev.formula.clause.solver;

import java.util.*;

import org.sat4j.specs.*;
import org.spldev.formula.clause.*;

/**
 * Finds certain solutions of propositional formulas.
 *
 * @author Sebastian Krieter
 */
public interface SatSolver {

	int MAX_SOLUTION_BUFFER = 1000;

	public enum SelectionStrategy {
		NEGATIVE, ORG, POSITIVE, RANDOM, UNIFORM_RANDOM, FIXED
	}

	/**
	 * Possible outcomes of a satisfiability solver call.<br>
	 * One of {@code TRUE}, {@code FALSE}, or {@code TIMEOUT}.
	 *
	 * @author Sebastian Krieter
	 */
	public enum SatResult {
		FALSE, TIMEOUT, TRUE
	}

	/**
	 * Adds a clause.
	 *
	 * @param mainClause The clause to add.
	 *
	 * @return The identifying constraint object of the clause that can be used to
	 *         remove it from the solver.
	 *
	 * @see #removeClause(IConstr)
	 */
	IConstr addClause(LiteralList mainClause) throws RuntimeContradictionException;

	/**
	 * Adds multiple clauses.
	 *
	 * @param clauses A collection of clauses.
	 *
	 * @return A list of the identifying constraint objects of the added clauses
	 *         that can be used to remove them from the solver.
	 *
	 * @see #removeClause(IConstr)
	 */
	List<IConstr> addClauses(Collection<? extends LiteralList> clauses) throws RuntimeContradictionException;

	/**
	 * Removes a certain clause. If possible, instead of using this method consider
	 * using {@link #removeLastClause()} as it runs faster.<br>
	 * <b>Note:</b> This method may not be supported by all solvers.
	 *
	 * @param constr The identifying constraint object for the clause.
	 */
	void removeClause(IConstr constr) throws RuntimeContradictionException;

	/**
	 * Removes the last clause added to the solver. This method should be preferred
	 * over {@link #removeClause(IConstr)}, if possible.<br>
	 * <b>Note:</b> This method may not be supported by all solvers.
	 */
	void removeLastClause() throws RuntimeContradictionException;

	/**
	 * Removes the last clauses added to the solver. This method should be preferred
	 * over {@link #removeClause(IConstr)}, if possible.<br>
	 * <b>Note:</b> This method may not be supported by all solvers.
	 *
	 * @param numberOfClauses The number of clauses that should be removed.
	 *
	 * @see #addClause(LiteralList)
	 */
	void removeLastClauses(int numberOfClauses) throws RuntimeContradictionException;

	/**
	 * Checks whether there is a satisfying solution considering the clauses of the
	 * solver.
	 *
	 * @return A {@link SatResult}.
	 *
	 * @see #hasSolution(LiteralList)
	 * @see #hasSolution(int...)
	 * @see #getSolution()
	 */
	SatResult hasSolution();

	/**
	 * Checks whether there is a satisfying solution considering the clauses of the
	 * solver and the given variable assignment.
	 *
	 * @param assignment The temporarily variable assignment for this call.
	 * @return A {@link SatResult}.
	 *
	 * @see #hasSolution(LiteralList)
	 * @see #hasSolution()
	 * @see #getSolution()
	 */
	SatResult hasSolution(int... assignment);

	/**
	 * Checks whether there is a satisfying solution considering the clauses of the
	 * solver and the given variable assignment.
	 *
	 * @param assignment The temporarily variable assignment for this call.
	 * @return A {@link SatResult}.
	 *
	 * @see #hasSolution()
	 * @see #hasSolution(int...)
	 * @see #getSolution()
	 */
	SatResult hasSolution(LiteralList assignment);

	/**
	 * Returns the last solution found by satisfiability solver. Can only be called
	 * after a successful call of {@link #hasSolution()} or
	 * {@link #hasSolution(int...)}.
	 *
	 * @return An int array representing the satisfying assignment.
	 *
	 * @see #hasSolution()
	 * @see #hasSolution(int...)
	 */
	int[] getSolution();

	/**
	 * @return The {@link CNF sat instance} given to the solver.
	 */
	CNF getCnf();

	/**
	 * Completely resets the solver, removing all its assignments, variables, and
	 * clauses.
	 */
	void reset();

	void setTimeout(int timeout);

	List<LiteralList> getSolutionHistory();

	List<LiteralList> rememberSolutionHistory(int size);

	/**
	 * @return A new instance of the solver.
	 */
	SatSolver clone();

	int[] findSolution();

	int[] getOrder();

	void setOrder(int[] order);

	void setOrderFix();

	void shuffleOrder();

	void shuffleOrder(Random rnd);

	SelectionStrategy getSelectionStrategy();

	void setSelectionStrategy(SelectionStrategy strategy);

	void setSelectionStrategy(int[] model, boolean min);

	void setSelectionStrategy(int[] model, boolean min, boolean inverse);

	void setSelectionStrategy(List<LiteralList> sample);

	void assignmentPop();

	void assignmentPush(int x);

	void assignmentPushAll(int[] literals);

	void assignmentReplaceLast(int x);

	void assignmentClear(int size);

	void asignmentEnsure(int size);

	int assignmentGet(int i);

	void assignmentDelete(int i);

	void assignmentSet(int index, int var);

	int[] getAssignmentArray();

	int[] getAssignmentArray(int from);

	int[] getAssignmentArray(int from, int to);

	int getAssignmentSize();

	int[] getContradictoryAssignment();

	boolean isGlobalTimeout();

	void setGlobalTimeout(boolean globalTimeout);

}