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
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.solver.*;

/**
 * Modifiable formula for a {@link Sat4JSolver}.
 *
 * @author Sebastian Krieter
 */
public class Sat4JFormula extends AbstractDynamicFormula<IConstr> {

	private final AbstractSat4JSolver<?> sat4jSolver;

	public Sat4JFormula(AbstractSat4JSolver<?> solver, VariableMap variableMap) {
		super(variableMap);
		sat4jSolver = solver;
	}

	protected Sat4JFormula(AbstractSat4JSolver<?> solver, Sat4JFormula oldFormula) {
		super(oldFormula);
		sat4jSolver = solver;
	}

	@Override
	public List<IConstr> push(Formula formula) throws RuntimeContradictionException {
		return push(FormulaToCNF.convert(formula, variableMap).getClauses());
	}

	public List<IConstr> push(List<? extends LiteralList> clauses) {
		final ArrayList<IConstr> constrs = new ArrayList<>();
		for (final LiteralList clause : clauses) {
			try {
				if ((clause.size() == 1) && (clause.getLiterals()[0] == 0)) {
					throw new ContradictionException();
				}
				final IConstr constr = sat4jSolver.solver
					.addClause(new VecInt(Arrays.copyOfRange(clause.getLiterals(), 0, clause.size())));
				constrs.add(constr);
			} catch (final ContradictionException e) {
				for (final IConstr constr : constrs) {
					sat4jSolver.solver.removeConstr(constr);
				}
				throw new RuntimeContradictionException(e);
			}
		}
		if (sat4jSolver.solutionHistory != null) {
			sat4jSolver.solutionHistory.clear();
			sat4jSolver.lastModel = null;
		}
		constraints.addAll(constrs);
		return constrs;
	}

	public IConstr push(LiteralList clause) throws RuntimeContradictionException {
		try {
			if ((clause.size() == 1) && (clause.getLiterals()[0] == 0)) {
				throw new ContradictionException();
			}
			final IConstr constr = sat4jSolver.solver
				.addClause(new VecInt(Arrays.copyOfRange(clause.getLiterals(), 0, clause.size())));
			constraints.add(constr);
			if (sat4jSolver.solutionHistory != null) {
				sat4jSolver.solutionHistory.clear();
				sat4jSolver.lastModel = null;
			}
			return constr;
		} catch (final ContradictionException e) {
			throw new RuntimeContradictionException(e);
		}
	}

	@Override
	public IConstr pop() {
		final IConstr lastConstraint = super.pop();
		sat4jSolver.solver.removeConstr(lastConstraint);
		return lastConstraint;
	}

	@Override
	public void remove(IConstr constr) {
		if (constr != null) {
			sat4jSolver.solver.removeConstr(constr);
			super.remove(constr);
		}
	}

	@Override
	public void pop(int count) {
		if (count > constraints.size()) {
			count = constraints.size();
		}
		for (int i = 0; i < count; i++) {
			final IConstr lastConstraint = removeConstraint(constraints.size() - 1);
			if (lastConstraint != null) {
				sat4jSolver.solver.removeSubsumedConstr(lastConstraint);
			}
		}
		sat4jSolver.solver.clearLearntClauses();
	}

}
