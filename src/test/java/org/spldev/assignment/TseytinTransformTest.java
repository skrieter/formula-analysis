package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.*;
import org.spldev.formula.analysis.sat4j.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.bool.*;

public class TseytinTransformTest {

	@Test
	public void testImplies() {
		VariableMap map = VariableMap.fromNames(Arrays.asList("p", "q", "r", "s"));
		final Literal p = new LiteralPredicate((BoolVariable) map.getVariable("p").get(), true);
		final Literal q = new LiteralPredicate((BoolVariable) map.getVariable("q").get(), true);
		final Literal r = new LiteralPredicate((BoolVariable) map.getVariable("r").get(), true);
		final Literal s = new LiteralPredicate((BoolVariable) map.getVariable("s").get(), true);

		final Implies formulaOrg = new Implies(new And(new Or(p, q), r), s.flip());

		testTransform(formulaOrg);
	}

	private void testTransform(final Implies formulaOrg) {
		VariableMap map = VariableMap.fromExpression(formulaOrg);
		final VariableMap orgMap = map.clone();
		final Formula formulaTseytin = Formulas.toTsyetinCNF(formulaOrg).get();

		ModelRepresentation rep1 = new ModelRepresentation(formulaOrg);
		ModelRepresentation rep2 = new ModelRepresentation(formulaTseytin);

		final Assignment assignment = new VariableAssignment(map);
		final int numVariables = orgMap.size();
		final int numAssignments = (int) Math.pow(2, numVariables);
		for (int i = 0; i < numAssignments; i++) {
			for (int j = 0; j < numVariables; j++) {
				assignment.set(j + 1, (i >> j & 1) == 1);
			}
			final Boolean orgEval = (Boolean) Formulas.evaluate(formulaOrg, assignment).orElse(null);
			final Boolean cnfEval = evaluate(rep1, assignment);
			final Boolean tseytinEval = evaluate(rep2, assignment);
			assertEquals(orgEval, cnfEval);
			assertEquals(orgEval, tseytinEval);
		}
	}

	private Boolean evaluate(ModelRepresentation rep1, final Assignment assignment) {
		final HasSolutionAnalysis analysis = new HasSolutionAnalysis();
		analysis.getAssumptions().setAll(assignment.getAll());
		return analysis.getResult(rep1).orElse((Boolean) null);
	}

}
