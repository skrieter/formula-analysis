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
 * Base class for modifiable formulas.
 * 
 * @param <O> type of the constraint object used within a solver
 *
 * @author Sebastian Krieter
 */
public abstract class AbstractDynamicFormula<O> implements DynamicFormula<O> {

	protected final ArrayList<O> constraints;
	protected final VariableMap variableMap;

	public AbstractDynamicFormula(VariableMap variableMap) {
		this.variableMap = variableMap;
		constraints = new ArrayList<>();
	}

	protected AbstractDynamicFormula(AbstractDynamicFormula<O> oldFormula) {
		variableMap = oldFormula.variableMap;
		constraints = new ArrayList<>(oldFormula.constraints);
	}

	@Override
	public List<O> getConstraints() {
		return constraints;
	}

	@Override
	public VariableMap getVariableMap() {
		return variableMap;
	}

	@Override
	public O pop() {
		return removeConstraint(constraints.size() - 1);
	}

	@Override
	public void remove(O constraint) {
		removeConstraint(constraints.indexOf(constraint));
	}

	protected O removeConstraint(final int index) {
		return constraints.remove(index);
	}

	@Override
	public int size() {
		return constraints.size();
	}

	@Override
	public O peek() {
		return constraints.get(constraints.size() - 1);
	}

}
