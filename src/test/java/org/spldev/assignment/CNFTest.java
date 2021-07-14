/* -----------------------------------------------------------------------------
 * Formula-Analysis-Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis-Lib.
 * 
 * Formula-Analysis-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.analysis.*;
import org.spldev.formula.clause.transform.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class CNFTest {

	@Test
	public void convert() {
		final VariableMap variables = new VariableMap(Arrays.asList("a", "b", "c"));
		final Literal a = variables.getLiteral("a", true).get();
		final Literal b = variables.getLiteral("b", true).get();
		final Literal c = variables.getLiteral("c", true).get();

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
		final VariableMap variables = new VariableMap(
			Arrays.asList("a", "b", "c", "d", "e"));
		final CNF cnf = new CNF(variables);
		cnf.addClause(new LiteralList(4));
		cnf.addClause(new LiteralList(-5));
		cnf.addClause(new LiteralList(1, 2));
		cnf.addClause(new LiteralList(-1, 3));
		cnf.addClause(new LiteralList(4, 2, -5));
		cnf.addClause(new LiteralList(-2, 3, 4));
		cnf.addClause(new LiteralList(-3, -4, -5));

		final CacheHolder rep = new CacheHolder();
		rep.get(CNFProvider.of(cnf));

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

		final CNFSlicer slicer = new CNFSlicer(new LiteralList(new int[] { 2 }));
		final CNF slicedCNF = Executor.run(slicer, cnf).orElse(Logger::logProblems);

		System.out.println(cnf);
		System.out.println(cnf.adapt(slicedCNF.getVariableMap()).get());
		System.out.println(slicedCNF);
		System.out.println(slicedCNF.adapt(cnf.getVariableMap()).get());
	}

	private void executeAnalysis(CacheHolder rep, Provider<?> builder) {
		final Result<?> result = rep.get(builder);
		Logger.logInfo(builder.getClass().getName());
		result.map(Object::toString)
			.ifPresentOrElse(Logger::logInfo, Logger::logProblems);
	}

}
