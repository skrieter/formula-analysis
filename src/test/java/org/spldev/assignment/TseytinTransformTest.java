package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.*;
import org.spldev.formula.analysis.sat4j.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.bool.*;

public class TseytinTransformTest {

	@Test
	public void testImplies() {
		final VariableMap map = VariableMap.fromNames(Arrays.asList("p", "q", "r", "s"));
		final Literal p = new LiteralPredicate((BoolVariable) map.getVariable("p").get(), true);
		final Literal q = new LiteralPredicate((BoolVariable) map.getVariable("q").get(), true);
		final Literal r = new LiteralPredicate((BoolVariable) map.getVariable("r").get(), true);
		final Literal s = new LiteralPredicate((BoolVariable) map.getVariable("s").get(), true);

		final Formula formulaOrg = new Implies(new And(new Or(p, q), r), s.flip());

		testTransform(formulaOrg);
	}

	@Test
	public void testComplex() {
		final VariableMap map = VariableMap.fromNames(Arrays.asList("p", "q", "r", "s"));
		final Literal p = new LiteralPredicate((BoolVariable) map.getVariable("p").get(), true);
		final Literal q = new LiteralPredicate((BoolVariable) map.getVariable("q").get(), true);
		final Literal r = new LiteralPredicate((BoolVariable) map.getVariable("r").get(), true);
		final Literal s = new LiteralPredicate((BoolVariable) map.getVariable("s").get(), true);

		final Formula formulaOrg = new And(
			new Implies(
				r,
				new And(p, q)),
			new Implies(
				s,
				new And(q, p)),
			new Or(
				new And(s.flip(), r),
				new And(s, r.flip())));

		testTransform(formulaOrg);
	}

	private void testTransform(final Formula formulaOrg) {
		final VariableMap map = VariableMap.fromExpression(formulaOrg);
		final VariableMap orgMap = map.clone();
		final Formula formulaTseytin = Formulas.toTseytinCNF(formulaOrg).get();

		final ModelRepresentation rep1 = new ModelRepresentation(formulaOrg);
		rep1.get(CNFProvider.fromFormula());
		final ModelRepresentation rep2 = new ModelRepresentation(formulaTseytin);
		rep2.get(CNFProvider.fromTseytinFormula());

		final Assignment assignment = new VariableAssignment(map);
		final int numVariables = orgMap.size();
		final int numAssignments = (int) Math.pow(2, numVariables);
		for (int i = 0; i < numAssignments; i++) {
			for (int j = 0; j < numVariables; j++) {
				assignment.set(j + 1, ((i >> j) & 1) == 1);
			}
			final Boolean orgEval = (Boolean) Formulas.evaluate(formulaOrg, assignment).orElse(null);
			final Boolean cnfEval = evaluate(rep1, assignment);
			final Boolean tseytinEval = evaluate(rep2, assignment);
			assertEquals(orgEval, cnfEval, Integer.toString(i));
			assertEquals(orgEval, tseytinEval, Integer.toString(i));
		}
	}

	private Boolean evaluate(ModelRepresentation rep1, final Assignment assignment) {
		final HasSolutionAnalysis analysis = new HasSolutionAnalysis();
		analysis.getAssumptions().setAll(assignment.getAll());
		return analysis.getResult(rep1).orElse((Boolean) null);
	}

}
