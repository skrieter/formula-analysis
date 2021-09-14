package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.*;
import org.spldev.formula.analysis.sat4j.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.util.tree.*;

public class TseytinTransformTest {

	@Test
	public void testImplies() {
		testTransform(FormulaCreator.getFormula01());
	}

	@Test
	public void testComplex() {
		testTransform(FormulaCreator.getFormula02());
	}

	private void testTransform(final Formula formulaOrg) {
		final Formula formulaClone = Trees.cloneTree(formulaOrg);
		final VariableMap map = VariableMap.fromExpression(formulaOrg);
		final VariableMap mapClone = map.clone();

		final ModelRepresentation rep = new ModelRepresentation(formulaOrg);
		rep.get(CNFProvider.fromTseytinFormula());

		FormulaCreator.testAllAssignments(map, assignment -> {
			final Boolean orgEval = (Boolean) Formulas.evaluate(formulaOrg, assignment).orElseThrow();
			final Boolean tseytinEval = evaluate(rep, assignment);
			assertEquals(orgEval, tseytinEval, assignment.toString());
		});
		assertTrue(Trees.equals(formulaOrg, formulaClone));
		assertEquals(mapClone, map);
		assertEquals(mapClone, VariableMap.fromExpression(formulaOrg));
	}

	private Boolean evaluate(ModelRepresentation rep, final Assignment assignment) {
		final HasSolutionAnalysis analysis = new HasSolutionAnalysis();
		analysis.getAssumptions().setAll(assignment.getAll());
		return analysis.getResult(rep).orElseThrow();
	}

}
