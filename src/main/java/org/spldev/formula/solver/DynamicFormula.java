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
package org.spldev.formula.solver;

import java.util.*;

import org.spldev.formula.expression.atomic.literal.*;

/**
 * Modifiable formula for solvers.
 *
 * @param <I> type of clauses added to a solver
 * @param <O> type of the constraint object used within a solver
 * 
 * @author Sebastian Krieter
 */
public interface DynamicFormula<I, O> {

	List<O> getConstraints();

	List<I> getOriginConstraints();

	VariableMap getVariableMap();

	/**
	 * Adds a clause.
	 *
	 * @param clause The clause to add.
	 *
	 * @return The identifying constraint object of the clause that can be used to
	 *         remove it from the solver.
	 *
	 * @see #pushAll(Collection)
	 * @see #pop()
	 * @see #pop(count)
	 * @see #remove(O)
	 */
	O push(I clause);

	/**
	 * Adds multiple clauses.
	 *
	 * @param clauses A collection of clauses.
	 *
	 * @return A list of the identifying constraint objects of the added clauses
	 *         that can be used to remove them from the solver.
	 *
	 * @see #push(I)
	 * @see #pop()
	 * @see #pop(count)
	 * @see #remove(O)
	 */
	default List<O> pushAll(Collection<? extends I> clauses) {
		int addCount = 0;
		final ArrayList<O> constraintList = new ArrayList<>(clauses.size());
		for (final I clause : clauses) {
			try {
				push(clause);
				addCount++;
			} catch (final Exception e) {
				pop(addCount);
				throw e;
			}
		}
		return constraintList;
	}

	O peek();

	/**
	 * Removes the last clause added to the solver. This method should be preferred
	 * over {@link #remove(O)}, if possible.<br>
	 * <b>Note:</b> This method may not be supported by all solvers.
	 */
	O pop();

	/**
	 * Removes the last clauses added to the solver. This method should be preferred
	 * over {@link #remove(O)}, if possible.<br>
	 * <b>Note:</b> This method may not be supported by all solvers.
	 *
	 * @param count The number of clauses to be removed.
	 *
	 * @see #push(I)
	 */
	default void pop(int count) {
		if (count > size()) {
			count = size();
		}
		for (int i = 0; i < count; i++) {
			pop();
		}
	}

	default void clear() {
		pop(size());
	}

	int size();

	/**
	 * Removes a certain clause. If possible, instead of using this method consider
	 * using {@link #pop()} as it runs faster.<br>
	 * <b>Note:</b> This method may not be supported by all solvers.
	 *
	 * @param constraint The identifying constraint object for the clause.
	 */
	void remove(O constraint);

}
