package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.util.tree.*;

public class CNFTransformTest {

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
		final Formula formulaCNF = rep.get(CCNFProvider.fromFormula());

		FormulaCreator.testAllAssignments(map, assignment -> {
			final Boolean orgEval = (Boolean) Formulas.evaluate(formulaOrg, assignment).orElseThrow();
			final Boolean cnfEval = (Boolean) Formulas.evaluate(formulaCNF, assignment).orElseThrow();
			assertEquals(orgEval, cnfEval, assignment.toString());
		});
		assertTrue(Trees.equals(formulaOrg, formulaClone));
		assertEquals(mapClone, map);
		assertEquals(mapClone, VariableMap.fromExpression(formulaOrg));
	}

}