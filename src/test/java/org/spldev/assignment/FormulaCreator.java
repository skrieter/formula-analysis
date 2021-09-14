package org.spldev.assignment;

import java.util.*;
import java.util.function.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.bool.*;

public class FormulaCreator {

	public static Formula getFormula01() {
		final VariableMap map = VariableMap.fromNames(Arrays.asList("p", "q", "r", "s"));
		final Literal p = new LiteralPredicate((BoolVariable) map.getVariable("p").get(), true);
		final Literal q = new LiteralPredicate((BoolVariable) map.getVariable("q").get(), true);
		final Literal r = new LiteralPredicate((BoolVariable) map.getVariable("r").get(), true);
		final Literal s = new LiteralPredicate((BoolVariable) map.getVariable("s").get(), true);

		return new Implies(new And(new Or(p, q), r), s.flip());
	}

	public static Formula getFormula02() {
		final VariableMap map = VariableMap.fromNames(Arrays.asList("p", "q", "r", "s"));
		final Literal p = new LiteralPredicate((BoolVariable) map.getVariable("p").get(), true);
		final Literal q = new LiteralPredicate((BoolVariable) map.getVariable("q").get(), true);
		final Literal r = new LiteralPredicate((BoolVariable) map.getVariable("r").get(), true);
		final Literal s = new LiteralPredicate((BoolVariable) map.getVariable("s").get(), true);

		return new And(
			new Implies(
				r,
				new And(p, q)),
			new Implies(
				s,
				new And(q, p)),
			new Or(
				new And(s.flip(), r),
				new And(s, r.flip())));
	}

	public static void testAllAssignments(VariableMap map, Consumer<Assignment> testFunction) {
		final Assignment assignment = new VariableAssignment(map);
		final int numVariables = map.size();
		final int numAssignments = (int) Math.pow(2, numVariables);
		for (int i = 0; i < numAssignments; i++) {
			for (int j = 0; j < numVariables; j++) {
				assignment.set(j + 1, ((i >> j) & 1) == 1);
			}
			testFunction.accept(assignment);
		}
	}

}
