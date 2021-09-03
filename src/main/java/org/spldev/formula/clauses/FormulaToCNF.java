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
package org.spldev.formula.clauses;

import java.util.*;

import org.spldev.formula.clauses.LiteralList.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.util.job.*;

/**
 * Several methods concerning {@link Expression} framework.
 *
 * @author Sebastian Krieter
 */
public final class FormulaToCNF implements MonitorableFunction<Formula, CNF> {

	private boolean keepLiteralOrder;
	private VariableMap variableMapping;

	public static CNF convert(Formula formula) {
		return Executor.run(new FormulaToCNF(), formula).get();
	}

	public static CNF convert(Formula formula, VariableMap variableMapping) {
		FormulaToCNF function = new FormulaToCNF();
		function.setVariableMapping(variableMapping);
		function.setKeepLiteralOrder(true);
		return Executor.run(function, formula).get();
	}

	@Override
	public CNF execute(Formula node, InternalMonitor monitor) {
		if (node == null) {
			return null;
		}
		final VariableMap mapping = variableMapping != null ? variableMapping : VariableMap.fromExpression(node);
		final ClauseList clauses = new ClauseList();
		final Optional<Object> formulaValue = Formulas.evaluate(node, new VariableAssignment(mapping));
		if (formulaValue.isPresent()) {
			if (formulaValue.get() == Boolean.FALSE) {
				clauses.add(new LiteralList());
			}
		} else {
			final Formula cnf = Formulas.toCNF(node).get();
			cnf.getChildren().stream().map(exp -> getClause(exp, mapping)).filter(Objects::nonNull).forEach(
				clauses::add);
		}
		return new CNF(mapping, clauses);
	}

	public boolean isKeepLiteralOrder() {
		return keepLiteralOrder;
	}

	public void setKeepLiteralOrder(boolean keepLiteralOrder) {
		this.keepLiteralOrder = keepLiteralOrder;
	}

	public VariableMap getVariableMapping() {
		return variableMapping;
	}

	public void setVariableMapping(VariableMap variableMapping) {
		this.variableMapping = variableMapping;
	}

	private LiteralList getClause(Expression clauseExpression, VariableMap mapping) {
		if (clauseExpression instanceof Literal) {
			final Literal literal = (Literal) clauseExpression;
			final int variable = mapping.getIndex(literal.getName()).orElseThrow(RuntimeException::new);
			return new LiteralList(new int[] { literal.isPositive() ? variable : -variable }, keepLiteralOrder
				? Order.UNORDERED
				: Order.NATURAL);
		} else {
			final List<? extends Expression> clauseChildren = clauseExpression.getChildren();
			if (clauseChildren.stream().anyMatch(literal -> literal == Literal.True)) {
				return null;
			} else {
				final int[] literals = clauseChildren.stream()
					.filter(literal -> literal != Literal.False)
					.filter(literal -> literal instanceof LiteralPredicate)
					.mapToInt(literal -> {
						final int variable = mapping.getIndex(
							((LiteralPredicate) literal).getVariable().getName()).orElseThrow(RuntimeException::new);
						return ((Literal) literal).isPositive() ? variable : -variable;
					}).toArray();
				return new LiteralList(literals, keepLiteralOrder ? Order.UNORDERED : Order.NATURAL);
			}
		}
	}

}
