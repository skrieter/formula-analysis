package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.spldev.formula.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.analysis.*;
import org.spldev.formula.clause.io.*;
import org.spldev.formula.clause.mig.*;
import org.spldev.formula.clause.transform.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.tree.*;
import org.spldev.tree.visitor.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.io.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

public class CNFTest {

//	@Test
	public void convert() {
		final Literal a = new LiteralVariable("a");
		final Literal b = new LiteralVariable("b");
		final Literal c = new LiteralVariable("c");

		final Implies implies1 = new Implies(a.cloneNode(), b.cloneNode());
		final Or or = new Or(implies1, c.cloneNode());
		final Biimplies equals = new Biimplies(a.cloneNode(), b.cloneNode());
		final And and = new And(equals, c.cloneNode());
		final Implies formula = new Implies(or, and);

		print(formula);

		final Formula cnfFormula = Formulas.toCNF(formula);

		final Or or2 = new Or(a.cloneNode(), b.cloneNode().flip());
		final Or or3 = new Or(a.cloneNode(), c.cloneNode());
		final Or or4 = new Or(b.cloneNode().flip(), c.cloneNode());
		final Or or5 = new Or(c.cloneNode().flip(), a.cloneNode().flip(), b.cloneNode());
		final And and2 = new And(or2, or3, or4, or5);

		assertEquals(Trees.getPreOrderList(cnfFormula), Trees.getPreOrderList(and2));
		assertEquals(Trees.postOrderStream(cnfFormula).collect(Collectors.toList()), Trees.postOrderStream(and2)
			.collect(Collectors.toList()));

		assertTrue(Trees.equals(cnfFormula, null));

		// print(formula);
		print(cnfFormula);
	}

//	@Test
	public void process() {
		final Cache cache = new Cache();

		cache.get(ExpressionProvider.of(new LiteralVariable("a")));

		final Literal a = new LiteralVariable("a");
		final Literal b = new LiteralVariable("b");
		final Literal c = new LiteralVariable("c");

		final Implies implies1 = new Implies(a.cloneNode(), b.cloneNode());
		final Implies implies2 = new Implies(b.cloneNode(), c.cloneNode());
		final And and = new And(implies1, implies2);

		print(and);

		final Formula cnfFormula = Formulas.toCNF(and);
		final FormulaToCNF converter = new FormulaToCNF();
		final CNF convert = Executor.run(converter, cnfFormula).orElse(Logger::logProblems);

		System.out.println(convert);

		final CNFSlicer slicer = new CNFSlicer(Arrays.asList(b.getName()), convert.getVariableMap());
		final CNF runMethod = Executor.run(slicer, convert).orElse(Logger::logProblems);

		System.out.println(runMethod);
	}

//	@Test
	public void slice() {
		final Literal a = new LiteralVariable("a");
		final Literal b = new LiteralVariable("b");
		final Literal c = new LiteralVariable("c");

		final Implies implies1 = new Implies(a.cloneNode(), b.cloneNode());
		final Implies implies2 = new Implies(b.cloneNode(), c.cloneNode());
		final And and = new And(implies1, implies2);

		print(and);

		final Formula cnfFormula = Formulas.toCNF(and);
		final FormulaToCNF converter = new FormulaToCNF();
		final CNF convert = Executor.run(converter, cnfFormula).orElse(Logger::logProblems);

		System.out.println(convert);

		final CNFSlicer slicer = new CNFSlicer(
			Arrays.asList(b.getName()),
			convert.getVariableMap());
		final CNF runMethod = Executor.run(slicer, convert).orElse(Logger::logProblems);

		System.out.println(runMethod);
	}

	private void print(Formula formula) {
		System.out.println(Trees.traverse(formula, new TreePrinter()).get());
	}

//	@Test
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

		final Cache rep = new Cache();
		rep.get(CNFProvider.of(cnf));

//		executeAnalysis(new HasSolutionAnalysis(cnf));
//		executeAnalysis(new AddRedundancyAnalysis(cnf));
//		executeAnalysis(new AtomicSetAnalysis(cnf));
//		executeAnalysis(new CauseAnalysis(cnf));
//		executeAnalysis(new ContradictionAnalysis(cnf));
//		executeAnalysis(new CoreDeadAnalysis(cnf));
//		executeAnalysis(new CountSolutionsAnalysis(cnf));
//		executeAnalysis(new IndependentContradictionAnalysis(cnf));
//		executeAnalysis(new IndependentRedundancyAnalysis(cnf));
//		executeAnalysis(new IndeterminedAnalysis(cnf));
//		executeAnalysis(new RemoveRedundancyAnalysis(cnf));

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

//		AddRedundancyAnalysis
//		AtomicSetAnalysis
//		CauseAnalysis
//		ConditionallyCoreDeadAnalysisMIG
//		ContradictionAnalysis
//		CoreDeadAnalysis
//		CountSolutionsAnalysis
//		IndependentContradictionAnalysis
//		IndependentRedundancyAnalysis
//		IndeterminedAnalysis
//		RemoveRedundancyAnalysis
	}

	private void executeAnalysis(Cache rep, Provider<?> builder) {
		final Result<?> result = rep.get(builder);
		result.map(Object::toString)
			.ifPresentOrElse(Logger::logInfo, Logger::logProblems);
	}

//	private void executeAnalysis(MonitorableSupplier<?> a1) {
//		final Object result1 = LongRunningWrapper.runMethod(a1);
//		System.out.println(result1);
//	}

//	@Test
	public void test(Consumer<? super Path> testMethod) {
		final Path path = Paths.get("src/test/resources");
		try {
			Files.walk(path)
				.filter(Files::isRegularFile)
				.peek(System.out::println)
				.forEach(testMethod);
		} catch (final IOException e) {
			Logger.logError(e);
		}
	}

//	@Test
	public void test04() {
		test(modelFile -> {
//			if (!modelFile.toString().contains("GPL")) {
//				return;
//			}
			if (!modelFile.toString().contains("Automotive02")) {
				return;
			}
			final Cache rep = new Cache();
			if (modelFile.getFileName().toString().endsWith(".xml")) {
				rep.get((ExpressionProvider) (c, m) -> {
					return FileHandler.parse(modelFile, new XmlFeatureModelFormat());
				}).ifPresentOrElse(
					e -> Logger.logInfo("Parsed " + modelFile.getFileName()),
					Logger::logProblems);
				rep.get(CNFProvider.fromExpression()).ifPresentOrElse(
					e -> Logger.logInfo("Transformed to CNF"),
					Logger::logProblems);
			} else {
				rep.get(CNFProvider.loader(modelFile));
			}
			rep.get(new HasSolutionAnalysis())
				.map(Objects::toString)
				.ifPresentOrElse(Logger::logInfo, Logger::logProblems);
			rep.get(MIGProvider.fromCNF()).ifPresentOrElse(
				e -> Logger.logInfo("Build MIG"),
				Logger::logProblems);
		});
	}

//	@Test
	public void test01() {
		test(modelFile -> {
			final Result<CNF> result = Clauses.load(modelFile);
			final CNF cnf = result.orElse(Logger::logProblems);
			doSomething(cnf);
		});
	}

//	@Test
	public void test02() {
		test(modelFile -> {
			final CNF cnf = Clauses.open(modelFile);
			doSomething(cnf);
		});
	}

//	@Test
	public void test03() {
		test(modelFile -> {
			final Result<CNF> result = Clauses.load(modelFile);
			doSomething(result.get());
		});
	}

	private void doSomething(CNF cnf) {
		System.out.println(cnf.getVariableMap().size());
	}

}
