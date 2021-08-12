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
package org.spldev.formula.solver.javasmt;

import java.util.*;

import org.sosy_lab.java_smt.api.*;
import org.sosy_lab.java_smt.api.Formula;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.solver.*;

/**
 * Formula for {@link JavaSmtSolver}.
 *
 * @author Sebastian Krieter
 */
public class JavaSmtFormula extends AbstractDynamicFormula<org.spldev.formula.expression.Formula, BooleanFormula> {

	private final ArrayList<Formula> variables;
	private final FormulaToJavaSmt translator;

	public JavaSmtFormula(SolverContext solverContext, org.spldev.formula.expression.Formula orignalFormula) {
		this(solverContext, VariableMap.fromExpression(orignalFormula));
		if (orignalFormula instanceof And) {
			for (final Expression clause : orignalFormula.getChildren()) {
				push((org.spldev.formula.expression.Formula) clause);
			}
		}
	}

	public JavaSmtFormula(SolverContext solverContext, VariableMap variableMap) {
		super(variableMap);
		translator = new FormulaToJavaSmt(solverContext);
		variables = translator.getVariables();
	}

	public List<Formula> getVariables() {
		return variables;
	}

	@Override
	public BooleanFormula push(org.spldev.formula.expression.Formula clause) throws RuntimeContradictionException {
		final BooleanFormula constraint = translator.nodeToFormula(clause);
		constraints.add(constraint);
		originClauses.add(clause);
		return constraint;
	}

	@Override
	public void clear() {
		constraints.clear();
		originClauses.clear();
	}

}
