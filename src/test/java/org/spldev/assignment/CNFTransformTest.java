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
package org.spldev.assignment;

import org.junit.jupiter.api.Test;
import org.spldev.formula.ModelRepresentation;
import org.spldev.formula.expression.Formula;
import org.spldev.formula.expression.FormulaProvider;
import org.spldev.formula.expression.Formulas;
import org.spldev.formula.expression.atomic.literal.VariableMap;
import org.spldev.formula.expression.io.DIMACSFormat;
import org.spldev.formula.expression.io.parse.KConfigReaderFormat;
import org.spldev.util.io.FileHandler;
import org.spldev.util.io.format.FormatSupplier;
import org.spldev.util.tree.Trees;
import org.spldev.util.tree.visitor.TreePrinter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		final Formula formulaCNF = rep.get(FormulaProvider.CNF.fromFormula());

		FormulaCreator.testAllAssignments(map, assignment -> {
			final Boolean orgEval = (Boolean) Formulas.evaluate(formulaOrg, assignment).orElseThrow();
			final Boolean cnfEval = (Boolean) Formulas.evaluate(formulaCNF, assignment).orElseThrow();
			assertEquals(orgEval, cnfEval, assignment.toString());
		});
		assertTrue(Trees.equals(formulaOrg, formulaClone));
		assertEquals(mapClone, map);
		assertEquals(mapClone, VariableMap.fromExpression(formulaOrg));
	}

	@Test
	public void testKConfigReader() throws IOException {
		Path modelFile = Paths.get("src/test/resources/kconfigreader/min-example.model");
		Formula formula = FileHandler.load(modelFile, FormatSupplier.of(new KConfigReaderFormat())).orElseThrow();
		System.out.println(Trees.traverse(formula, new TreePrinter()));

		ModelRepresentation rep = new ModelRepresentation(formula);
		Formula f1 = rep.get(FormulaProvider.CNF.fromFormula());
		System.out.println(Trees.traverse(f1, new TreePrinter()));
		FileHandler.save(f1, Paths.get(
			"src/test/resources/kconfigreader/min-example1.dimacs"), new DIMACSFormat());

		rep = new ModelRepresentation(formula);
		Formula f2 = rep.get(FormulaProvider.TseytinCNF.fromFormula());
		System.out.println(Trees.traverse(f2, new TreePrinter()));
		FileHandler.save(f2, Paths.get(
			"src/test/resources/kconfigreader/min-example2.dimacs"), new DIMACSFormat());

		try {
			rep = new ModelRepresentation(formula);
			Formula f3 = rep.get(FormulaProvider.TseytinCNF.fromFormula(10, 10));
			System.out.println(Trees.traverse(f3, new TreePrinter()));
			FileHandler.save(f3, Paths.get(
				"src/test/resources/kconfigreader/min-example3.dimacs"), new DIMACSFormat());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
