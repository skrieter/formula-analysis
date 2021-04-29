package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import org.spldev.formula.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.analysis.*;
import org.spldev.formula.clause.mig.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.formula.clause.transform.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.io.*;
import org.spldev.formula.expression.io.parse.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.io.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class CNFTest {
	


////	@Test
//	public void convert() {
//		final Literal a = new LiteralVariable("a");
//		final Literal b = new LiteralVariable("b");
//		final Literal c = new LiteralVariable("c");
//
//		final Implies implies1 = new Implies(a.cloneNode(), b.cloneNode());
//		final Or or = new Or(implies1, c.cloneNode());
//		final Biimplies equals = new Biimplies(a.cloneNode(), b.cloneNode());
//		final And and = new And(equals, c.cloneNode());
//		final Implies formula = new Implies(or, and);
//
////		print(formula);
//
//		final Formula cnfFormula = Formulas.toCNF(formula);
//
//		final Or or2 = new Or(a.cloneNode(), c.cloneNode());
//		final Or or3 = new Or(a.cloneNode(), b.flip());
//		final Or or4 = new Or(c.cloneNode(), b.flip());
//		final Or or5 = new Or(b.cloneNode(), a.flip(), c.flip());
//		final And and2 = new And(or2, or3, or4, or5);
//		
////		print(cnfFormula);
////		print(and2);
//
//		System.out.println(Trees.getPreOrderList(cnfFormula));
//		System.out.println(Trees.getPreOrderList(and2));
//		System.out.println(Trees.getPostOrderList(cnfFormula));
//		System.out.println(Trees.getPostOrderList(and2));
//		assertEquals(Trees.getPreOrderList(cnfFormula), Trees.getPreOrderList(and2));
//		assertEquals(Trees.getPostOrderList(cnfFormula), Trees.getPostOrderList(and2));
//
////		print(cnfFormula);
//	}

//	@Test
//	public void convert1() {
//		FMFactoryManager.getInstance().addExtension(DefaultFeatureModelFactory.getInstance());
//		FMFactoryManager.getInstance().addExtension(MultiFeatureModelFactory.getInstance());
//		FMFactoryManager.getInstance().setWorkspaceLoader(new CoreFactoryWorkspaceLoader());
//
//		FMFormatManager.getInstance().addExtension(new de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat());
//
//		long start, end;
//		final Path p = Paths.get("/home/sebas/Documents/Coding/spldev/evaluation-mig/models/Test2/model.xml");
//		
//		start = System.nanoTime();
//		final IFeatureModel fm = FeatureModelManager.load(p);
//		end = System.nanoTime();
//		System.out.println(((end - start) / 1_000_000) / 1.000);
//
//		start = System.nanoTime();
////		final Node formula1 = AdvancedNodeCreator.createCNF(fm);
//		final AdvancedNodeCreator nodeCreator = new AdvancedNodeCreator(fm);
//		nodeCreator.setCnfType(CNFType.Compact);
////		nodeCreator.setModelType(ModelType.OnlyConstraints);
//		final Node formula1 = nodeCreator.createNodes();
//		end = System.nanoTime();
//		System.out.println(((end - start) / 1_000_000) / 1.000);
//		
//		ArrayList<String> nodes1 = new ArrayList<String>();
//		for (Node node : formula1.getChildren()) {
////			org.prop4j.NodeWriter nw1 = new org.prop4j.NodeWriter(node);
////			nodes1.add(nw1.nodeToString());
//			if (node instanceof org.prop4j.Or) {
//				ArrayList<Node> children = new ArrayList<>(Arrays.asList(node.getChildren()));
//				Collections.sort(children, Comparator.comparing(n -> {
//					org.prop4j.Literal l = (org.prop4j.Literal) n;
//					return (l.positive ? "+" : "") + l.toString();
//				}));
//				StringBuilder sb = new StringBuilder();
//				sb.append("or ");
//				for (Node exp : children) {
//					org.prop4j.Literal l = (org.prop4j.Literal) exp;
//					sb.append((l.positive ? "+" : "") + l.toString());
//					sb.append(" | ");
//				}
//				nodes1.add(sb.toString());
//			} else {
//				org.prop4j.Literal l = (org.prop4j.Literal) node;
//				if (l.var != NodeCreator.varTrue && l.var != NodeCreator.varFalse) {
//					nodes1.add("literal " + (l.positive ? "+" : "") + l.toString());
//				}
//			}
//		}
////		final de.ovgu.featureide.fm.core.analysis.cnf.CNF cnf = Nodes.convert(createCNF);
////		final AdvancedSatSolver solver = new AdvancedSatSolver(cnf);
////		final de.ovgu.featureide.fm.core.analysis.cnf.solver.ISimpleSatSolver.SatResult satResult = solver.hasSolution();
////		System.out.println(satResult);
//
//		start = System.nanoTime();
//		Formula cnfFormula = FileHandler.parse(p, new XmlFeatureModelCNFFormat()).orElse(Logger::logProblems);
////		final List<? extends Expression> children = formula.getChildren();
//		end = System.nanoTime();
//		System.out.println(((end - start) / 1_000_000) / 1.000);
//
////		start = System.nanoTime();
////		final Formula cnfFormula = Formulas.toCNF(formula2);
////		end = System.nanoTime();
////		System.out.println(((end - start) / 1_000_000) / 1.000);
//		ArrayList<String> nodes2 = new ArrayList<String>();
//		NodeWriter nw2 = new NodeWriter();
//		for (Expression node : cnfFormula.getChildren()) {
//			if (node instanceof Or) {
//				final List<? extends Expression> children = new ArrayList<>(node.getChildren());
//				Collections.sort(children, Comparator.comparing(Expression::toString));
//				StringBuilder sb = new StringBuilder();
//				sb.append("or ");
//				for (Expression exp : children) {
//					sb.append(exp.toString());
//					sb.append(" | ");
//				}
//				nodes2.add(sb.toString());
//			} else {
//				nodes2.add("literal " + node.toString());
//			}
////			nodes2.add(nw2.write((Formula) node));
//		}
//
//		Collections.sort(nodes1);
//		Collections.sort(nodes2);
//		HashSet<String> nodeSet1 = new LinkedHashSet<>(nodes1);
//		HashSet<String> nodeSet2 = new LinkedHashSet<>(nodes2);
//		HashSet<String> nodeSet12 = new LinkedHashSet<>(nodes1);
//		HashSet<String> nodeSet22 = new LinkedHashSet<>(nodes2);
//		nodeSet1.removeAll(nodeSet22);
//		nodeSet2.removeAll(nodeSet12);
//
//		System.out.println(nodeSet1.size());
//		nodeSet1.stream().limit(50).forEach(System.out::println);
//		System.out.println("==========================================");
//		System.out.println(nodeSet2.size());
//		nodeSet2.stream().limit(50).forEach(System.out::println);
//		
//		final CNF cnf = Clauses.convertToCNF(cnfFormula);
//		SatSolver solver = new Sat4JSolver(cnf);
//		final SatResult satResult = solver.hasSolution();
//		System.out.println(satResult);
//	}
	
//	@Test
	public void convert2() {
		final Path p = Paths.get("/home/sebas/Documents/Coding/spldev/evaluation-mig/models/Test2/model.xml");
		Formula formula = FileHandler.parse(p, new XmlFeatureModelFormat()).orElse(Logger::logProblems);
//		final List<? extends Expression> children = formula.getChildren();
		NodeWriter nw = new NodeWriter();
		System.out.println(nw.write(formula));
		//		System.out.println("dasda");
		final Formula cnfFormula = Formulas.toCNF(formula);
		final CNF cnf = Clauses.convertToCNF(cnfFormula);
		SatSolver solver = new Sat4JSolver(cnf);
		final SatResult satResult = solver.hasSolution();
		System.out.println(satResult);
		assertTrue(satResult == SatResult.TRUE);
	}

//	@Test
	public void process() {
		final Cache cache = new Cache();

		VariableMap map = new VariableMap(Arrays.asList("a","b","c"));
		cache.get(ExpressionProvider.of(new LiteralVariable("a", map)));

		final Literal a = new LiteralVariable("a", map);
		final Literal b = new LiteralVariable("b", map);
		final Literal c = new LiteralVariable("c", map);

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
		VariableMap map = new VariableMap(Arrays.asList("a","b","c"));
		final Literal a = new LiteralVariable("a", map);
		final Literal b = new LiteralVariable("b", map);
		final Literal c = new LiteralVariable("c", map);

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
