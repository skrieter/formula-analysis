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

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.*;
import org.spldev.formula.analysis.*;
import org.spldev.formula.analysis.mig.*;
import org.spldev.formula.analysis.sat4j.*;
import org.spldev.formula.clauses.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.integer.*;
import org.spldev.formula.transform.*;
import org.spldev.util.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class CNFTest {

	@Test
	public void convert() {
		final VariableMap variables = VariableMap.fromNames(Arrays.asList("a", "b", "c"));
		final Literal a = new LiteralVariable((BoolVariable) variables.getVariable("a").get(), true);
		final Literal b = new LiteralVariable((BoolVariable) variables.getVariable("b").get(), true);
		final Literal c = new LiteralVariable((BoolVariable) variables.getVariable("c").get(), true);

		final Implies implies1 = new Implies(a, b);
		final Or or = new Or(implies1, c);
		final Biimplies equals = new Biimplies(a, b);
		final And and = new And(equals, c);
		final Implies formula = new Implies(or, and);

		final Formula cnfFormula = Formulas.toCNF(formula);

		final Or or2 = new Or(a, c);
		final Or or3 = new Or(a, b.flip());
		final Or or4 = new Or(c, b.flip());
		final Or or5 = new Or(b, a.flip(), c.flip());
		final And and2 = new And(or2, or3, or4, or5);

		sortChildren(cnfFormula);
		sortChildren(and2);
		assertEquals(Trees.getPreOrderList(cnfFormula), Trees.getPreOrderList(and2));
		assertEquals(Trees.getPostOrderList(cnfFormula), Trees.getPostOrderList(and2));
	}

	private void sortChildren(final Expression root) {
		Trees.postOrderStream(root).forEach(node -> {
			final ArrayList<Expression> sortedChildren = new ArrayList<>(node.getChildren());
			Collections.sort(sortedChildren, Comparator.comparing(e -> Trees.getPreOrderList(e).toString()));
			node.setChildren(sortedChildren);
		});
	}

	@SuppressWarnings("unused")
	private void print(Formula formula) {
		System.out.println(Trees.traverse(formula, new TreePrinter()).get());
	}

	@Test
	public void testAnalyses() {
		final VariableMap variables = VariableMap.fromNames(
			Arrays.asList("a", "b", "c", "d", "e"));
		final Literal a = new LiteralVariable((BoolVariable) variables.getVariable("a").get(), true);
		final Literal b = new LiteralVariable((BoolVariable) variables.getVariable("b").get(), true);
		final Literal c = new LiteralVariable((BoolVariable) variables.getVariable("c").get(), true);
		final Literal d = new LiteralVariable((BoolVariable) variables.getVariable("d").get(), true);
		final Literal e = new LiteralVariable((BoolVariable) variables.getVariable("e").get(), true);

		final And formula = new And(
			new Or(d),
			new Or(e.flip()),
			new Or(a, b),
			new Or(a.flip(), c),
			new Or(d, b, e.flip()),
			new Or(b.flip(), c, d),
			new Or(c.flip(), d.flip(), e.flip()));

		final ModelRepresentation rep = new ModelRepresentation(formula);

		System.out.println("---------");

		executeAnalysis(rep, new HasSolutionAnalysis());
		executeAnalysis(rep, new AddRedundancyAnalysis());
		executeAnalysis(rep, new AtomicSetAnalysis());
		executeAnalysis(rep, new CauseAnalysis());
		executeAnalysis(rep, new ContradictionAnalysis());
		executeAnalysis(rep, new CoreDeadAnalysis());
		executeAnalysis(rep, new CountSolutionsAnalysis());
		executeAnalysis(rep, new IndependentContradictionAnalysis());
		executeAnalysis(rep, new IndependentRedundancyAnalysis());
		executeAnalysis(rep, new IndeterminedAnalysis());
		executeAnalysis(rep, new RemoveRedundancyAnalysis());
		executeAnalysis(rep, new ConditionallyCoreDeadAnalysisMIG());

		final CNF cnf = rep.get(CNFProvider.fromFormula());
		final CNFSlicer slicer = new CNFSlicer(new LiteralList(new int[] { 2 }));
		final CNF slicedCNF = Executor.run(slicer, cnf).orElse(Logger::logProblems);

		System.out.println(cnf);
		System.out.println(cnf.adapt(slicedCNF.getVariableMap()).get());
		System.out.println(slicedCNF);
		System.out.println(slicedCNF.adapt(cnf.getVariableMap()).get());
	}

	private void executeAnalysis(ModelRepresentation rep, Analysis<?> analysis) {
		final Result<?> result = analysis.getResult(rep);
		Logger.logInfo(analysis.getClass().getName());
		result.map(Object::toString)
			.ifPresentOrElse(Logger::logInfo, p -> {
				Logger.logProblems(p);
				fail();
			});
	}

}
