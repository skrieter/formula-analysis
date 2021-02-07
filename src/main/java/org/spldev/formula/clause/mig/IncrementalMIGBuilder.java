package org.spldev.formula.clause.mig;

import java.util.*;
import java.util.stream.*;

import org.sat4j.core.*;
import org.spldev.formula.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.LiteralList.*;
import org.spldev.formula.clause.mig.Vertex.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.util.job.*;
import org.spldev.util.logging.*;

public class IncrementalMIGBuilder extends MIGBuilder2 implements MonitorableFunction<CNF, MIG> {

	private final Random random = new Random(112358);
	private final CNF oldCNF;
	private final MIG oldMig;

	private SatSolver solver;
	private int[] fixedFeatures;

	public IncrementalMIGBuilder(MIG oldMig) {
		this.oldMig = oldMig;
		oldCNF = oldMig.getCnf();
	}

	@Override
	public MIG execute(CNF cnf, InternalMonitor monitor) throws Exception {
		Objects.requireNonNull(oldCNF);
		Objects.requireNonNull(oldMig);
//		monitor.setTotalWork(113);

		init(cnf);
		monitor.step();

		long start, end;
		start = System.nanoTime();

		final Set<String> allVariables = new HashSet<>(oldCNF.getVariableMap().getNames());
		allVariables.addAll(cnf.getVariableMap().getNames());
		final VariableMap variables = new VariableMap(allVariables);

		final HashSet<LiteralList> adaptedNewClauses = cnf.getClauses().stream()
			.map(c -> c.adapt(cnf.getVariableMap(), variables))
			.peek(c -> c.setOrder(Order.NATURAL))
			.collect(Collectors.toCollection(HashSet::new));

		final HashSet<LiteralList> adaptedOldClauses = oldCNF.getClauses().stream()
			.map(c -> c.adapt(oldCNF.getVariableMap(), variables))
			.peek(c -> c.setOrder(Order.NATURAL))
			.collect(Collectors.toCollection(HashSet::new));

		final HashSet<LiteralList> addedClauses = adaptedNewClauses.stream()
			.filter(c -> !adaptedOldClauses.contains(c))
			.collect(Collectors.toCollection(HashSet::new));
		final HashSet<LiteralList> removedClauses = adaptedOldClauses.stream()
			.filter(c -> !adaptedNewClauses.contains(c))
			.collect(Collectors.toCollection(HashSet::new));

		end = System.nanoTime();
		Logger.logInfo("Collect: " + (((end - start) / 1_000_000) / 1_000.0));

		if (addedClauses.isEmpty()) {
			if (removedClauses.isEmpty()) {
				// Unchanged
				if (checkRedundancy) {
					final HashSet<LiteralList> redundantClauses = getOldRedundantClauses(variables);

					cnf.getClauses().stream()
						.map(c -> cleanClause(c, mig))
						.filter(Objects::nonNull)
						.distinct()
						.map(c -> c.adapt(cnf.getVariableMap(), variables))
						.peek(c -> c.setOrder(Order.NATURAL))
						.filter(c -> (c.size() <= 2) || !redundantClauses.contains(c))
						.map(c -> c.adapt(variables, cnf.getVariableMap()))
						.forEach(mig::addClause);
				} else {
					addAllClauses(cnf);
				}
				getOldCoreLiterals(cnf);
			} else {
				// Removed
				if (!satCheck(cnf)) {
					return null;
				}

				final int[] coreDead = getOldCoreLiterals(cnf);
				checkOldCoreLiterals(coreDead);

				if (checkRedundancy) {
					final HashSet<LiteralList> redundantClauses = getOldRedundantClauses(variables);

					final Sat4JSolver redundancySolver = new Sat4JSolver(new CNF(variables));
					cnf.getClauses().stream()
						.map(c -> cleanClause(c, mig))
						.filter(Objects::nonNull)
						.map(c -> c.adapt(cnf.getVariableMap(), variables))
						.distinct()
						.sorted(lengthComparator)
						.peek(c -> c.setOrder(Order.NATURAL))
						.filter(c -> (c.size() <= 2)
							|| !redundantClauses.contains(c)
							|| !isRedundant(redundancySolver, c))
						.peek(redundancySolver::addClause)
						.map(c -> c.adapt(variables, cnf.getVariableMap()))
						.forEach(mig::addClause);
				} else {
					addAllClauses(cnf);
				}
			}
		} else {
			if (removedClauses.isEmpty()) {
				// Added
				if (!satCheck(cnf)) {
					return null;
				}

				final int[] coreDead = getOldCoreLiterals(cnf);
				for (final int literal : coreDead) {
					solver.assignmentPush(literal);
					fixedFeatures[Math.abs(literal) - 1] = 0;
				}
				findNewCoreLiterals();

				if (checkRedundancy) {
					final HashSet<LiteralList> redundantClauses = getOldRedundantClauses(variables);

					final int[] affectedLiterals = addedClauses.stream()
						.flatMapToInt(c -> IntStream.of(c.getLiterals()))
						.distinct()
						.toArray();
					final Sat4JSolver redundancySolver = new Sat4JSolver(new CNF(variables));
					cnf.getClauses().stream()
						.peek(c -> c.setOrder(Order.NATURAL))
						.map(c -> cleanClause(c, mig))
						.filter(Objects::nonNull)
						.map(c -> c.adapt(cnf.getVariableMap(), variables))
						.distinct()
						.sorted(lengthComparator)
						.peek(c -> c.setOrder(Order.NATURAL))
						.filter(c -> (c.size() <= 2)
							|| (!redundantClauses.contains(c)
								&& (!c.containsLiteral(affectedLiterals)
									|| !isRedundant(redundancySolver, c))))
						.peek(redundancySolver::addClause)
						.map(c -> c.adapt(variables, cnf.getVariableMap()))
						.forEach(mig::addClause);
				} else {
					addAllClauses(cnf);
				}
			} else {
				// Replaced
				if (!satCheck(cnf)) {
					return null;
				}
				final int[] coreDead = getOldCoreLiterals(cnf);
				checkOldCoreLiterals(coreDead);
				for (final int literal : coreDead) {
					fixedFeatures[Math.abs(literal) - 1] = 0;
				}
				findNewCoreLiterals();

				start = System.nanoTime();
				if (checkRedundancy) {
					final HashSet<LiteralList> redundantClauses = getOldRedundantClauses(variables);

					final int[] affectedLiterals = addedClauses.stream()
						.flatMapToInt(c -> IntStream.of(c.getLiterals()))
						.distinct()
						.toArray();
					final Sat4JSolver redundancySolver = new Sat4JSolver(new CNF(variables));
					cnf.getClauses().stream()
						.map(c -> cleanClause(c, mig))
						.filter(Objects::nonNull)
						.map(c -> c.adapt(cnf.getVariableMap(), variables))
						.distinct()
						.sorted(lengthComparator)
						.peek(c -> c.setOrder(Order.NATURAL))
						.filter(c -> {
							if (c.size() <= 2) {
								return true;
							}
							if (redundantClauses.contains(c)) {
								return !isRedundant(redundancySolver, c);
							} else {
								return !c.containsLiteral(affectedLiterals) || !isRedundant(redundancySolver, c);
							}
						})
						.peek(redundancySolver::addClause)
						.map(c -> c.adapt(variables, cnf.getVariableMap()))
						.forEach(mig::addClause);
				} else {
					addAllClauses(cnf);
				}

				end = System.nanoTime();
				Logger.logInfo("Add Clauses: " + (((end - start) / 1_000_000) / 1_000.0));
			}
		}
		start = System.nanoTime();
		bfsStrong();
		monitor.step();
		end = System.nanoTime();
		Logger.logInfo("bfs: " + (((end - start) / 1_000_000) / 1_000.0));

		start = System.nanoTime();
		finish();
		monitor.step();
		end = System.nanoTime();
		Logger.logInfo("finish: " + (((end - start) / 1_000_000) / 1_000.0));

		return mig;
	}

	private void addAllClauses(CNF cnf) {
		cnf.getClauses().stream()
			.map(c -> cleanClause(c, mig))
			.filter(Objects::nonNull)
			.forEach(mig::addClause);
	}

	private int[] getOldCoreLiterals(CNF cnf) {
		final long start = System.nanoTime();
		final int[] array = oldMig.getVertices().stream()
			.filter(v -> v.getStatus() == Status.Core)
			.mapToInt(v -> v.getVar())
			.map(l -> Clauses.adapt(l, oldCNF.getVariableMap(), cnf.getVariableMap()))
			.filter(l -> l != 0)
			.peek(l -> {
				mig.getVertex(l).setStatus(Status.Core);
				mig.getVertex(-l).setStatus(Status.Dead);
			})
			.toArray();
		final long end = System.nanoTime();
		Logger.logInfo("getOldCore: " + (((end - start) / 1_000_000) / 1_000.0));
		return array;
	}

	private HashSet<LiteralList> getOldRedundantClauses(VariableMap variables) {
		final Set<LiteralList> oldMigClauses = oldMig.getVertices().stream()
			.flatMap(v -> v.getComplexClauses().stream())
			.collect(Collectors.toCollection(HashSet::new));
		return oldCNF.getClauses().stream()
			.map(c -> cleanClause(c, oldMig))
			.filter(Objects::nonNull)
			.filter(c -> c.size() > 2)
			.filter(c -> !oldMigClauses.contains(c))
			.map(c -> c.adapt(oldCNF.getVariableMap(), variables))
			.peek(c -> c.setOrder(Order.NATURAL))
			.collect(Collectors.toCollection(HashSet::new));
	}

	protected boolean satCheck(CNF cnf) {
		final long start = System.nanoTime();

		solver = new Sat4JSolver(cnf);
		solver.rememberSolutionHistory(0);
		fixedFeatures = solver.findSolution();

		final long end = System.nanoTime();
		Logger.logInfo("Sat: " + (((end - start) / 1_000_000) / 1_000.0));

		return fixedFeatures != null;
	}

	// For removed clauses
	protected void checkOldCoreLiterals(int[] coreDead) {
		final long start = System.nanoTime();
		checkOldCoreLiterals2(coreDead);
		final long end = System.nanoTime();
		Logger.logInfo("checkOldCoreLiterals: " + (((end - start) / 1_000_000) / 1_000.0));

	}

	// For added clauses
	protected void findNewCoreLiterals() {
		final long start = System.nanoTime();
		findNewCoreLiterals1();
		final long end = System.nanoTime();
		Logger.logInfo("findNewCoreLiterals: " + (((end - start) / 1_000_000) / 1_000.0));
	}

	protected void findNewCoreLiterals1() {
		solver.setSelectionStrategy(fixedFeatures, true, true);
		for (final int varX : fixedFeatures) {
			if (varX != 0) {
				solver.assignmentPush(-varX);
				switch (solver.hasSolution()) {
				case FALSE:
					solver.assignmentReplaceLast(varX);
					mig.getVertex(-varX).setStatus(Status.Dead);
					mig.getVertex(varX).setStatus(Status.Core);
					break;
				case TIMEOUT:
					solver.assignmentPop();
					break;
				case TRUE:
					solver.assignmentPop();
					LiteralList.resetConflicts(fixedFeatures, solver.getSolution());
					solver.shuffleOrder(random);
					break;
				}
			}
		}
	}

	private void findNewCoreLiterals2() {
		solver.setSelectionStrategy(fixedFeatures, true, true);
		split(fixedFeatures, 0, fixedFeatures.length, 0);
	}

	private void split(int[] model, int start, int end, int depth) {
		final VecInt vars = new VecInt(end - start);
		for (int j = start; j < end; j++) {
			final int var = model[j];
			if (var != 0) {
				vars.push(-var);
			}
		}
		if ((vars.size() <= 4) || (depth > 100)) {
			for (int j = start; j < end; j++) {
				final int varX = model[j];
				if (varX != 0) {
					solver.assignmentPush(-varX);
					switch (solver.hasSolution()) {
					case FALSE:
						solver.assignmentReplaceLast(varX);
						mig.getVertex(-varX).setStatus(Status.Dead);
						mig.getVertex(varX).setStatus(Status.Core);
						break;
					case TIMEOUT:
						solver.assignmentPop();
						break;
					case TRUE:
						solver.assignmentPop();
						LiteralList.resetConflicts(model, solver.getSolution());
						solver.shuffleOrder(random);
						break;
					}
				}
			}
		} else {
			final LiteralList mainClause = new LiteralList(Arrays.copyOf(vars.toArray(), vars.size()), Order.UNORDERED);
			switch (solver.hasSolution(mainClause)) {
			case FALSE:
				final int halfLength = (end - start) >> 1;
				split(model, start + halfLength, end, depth + 1);
				split(model, start, start + halfLength, depth + 1);
				break;
			case TIMEOUT:
				break;
			case TRUE:
				LiteralList.resetConflicts(model, solver.getSolution());
				solver.shuffleOrder(random);
				break;
			}
		}
	}

	private void checkOldCoreLiterals1(int[] coreDead) {
		solver.setSelectionStrategy(fixedFeatures, true, true);
		for (final int literal : coreDead) {
			final int varX = fixedFeatures[Math.abs(literal) - 1];
			if (varX == -literal) {
				fixedFeatures[Math.abs(literal) - 1] = 0;
				mig.getVertex(-varX).setStatus(Status.Normal);
				mig.getVertex(varX).setStatus(Status.Normal);
			} else if (varX == 0) {
				mig.getVertex(-literal).setStatus(Status.Normal);
				mig.getVertex(literal).setStatus(Status.Normal);
			} else {
				solver.assignmentPush(-varX);
				switch (solver.hasSolution()) {
				case FALSE:
					solver.assignmentReplaceLast(varX);
					break;
				case TIMEOUT:
					solver.assignmentPop();
					mig.getVertex(-varX).setStatus(Status.Normal);
					mig.getVertex(varX).setStatus(Status.Normal);
					break;
				case TRUE:
					solver.assignmentPop();
					mig.getVertex(-varX).setStatus(Status.Normal);
					mig.getVertex(varX).setStatus(Status.Normal);
					LiteralList.resetConflicts(fixedFeatures, solver.getSolution());
					solver.shuffleOrder(random);
					break;
				}
			}
		}
	}

	protected void checkOldCoreLiterals2(int[] coreDead) {
		solver.setSelectionStrategy(fixedFeatures, true, false);
		final int[] negCoreDead = new int[coreDead.length];
		for (int i = 0; i < coreDead.length; i++) {
			negCoreDead[i] = -coreDead[i];
		}
		checkOldCoreLiteralsRec(negCoreDead, 0);
	}

	protected void checkOldCoreLiteralsRec(int[] coreDead, int depth) {
		if ((coreDead.length <= 4) || (depth > 4)) {
			for (final int literal : coreDead) {
				final int varX = fixedFeatures[Math.abs(literal) - 1];
				if (varX == literal) {
					fixedFeatures[Math.abs(literal) - 1] = 0;
					mig.getVertex(-literal).setStatus(Status.Normal);
					mig.getVertex(literal).setStatus(Status.Normal);
				} else if (varX == 0) {
					mig.getVertex(-literal).setStatus(Status.Normal);
					mig.getVertex(literal).setStatus(Status.Normal);
				} else {
					solver.assignmentPush(-varX);
					switch (solver.hasSolution()) {
					case FALSE:
						solver.assignmentReplaceLast(varX);
						break;
					case TIMEOUT:
						solver.assignmentPop();
						mig.getVertex(-varX).setStatus(Status.Normal);
						mig.getVertex(varX).setStatus(Status.Normal);
						break;
					case TRUE:
						solver.assignmentPop();
						mig.getVertex(-varX).setStatus(Status.Normal);
						mig.getVertex(varX).setStatus(Status.Normal);
						LiteralList.resetConflicts(fixedFeatures, solver.getSolution());
						solver.shuffleOrder(random);
						break;
					}
				}
			}
		} else {
			try {
				solver.addClause(new LiteralList(coreDead, Order.UNORDERED));
				switch (solver.hasSolution()) {
				case FALSE:
					solver.removeLastClause();
					for (final int literal : coreDead) {
						solver.assignmentPush(-literal);
					}
					break;
				case TIMEOUT:
					solver.removeLastClause();
					break;
				case TRUE:
					solver.removeLastClause();
					final int half = coreDead.length >> 1;
					checkOldCoreLiteralsRec(Arrays.copyOfRange(coreDead, 0, half), depth + 1);
					checkOldCoreLiteralsRec(Arrays.copyOfRange(coreDead, half, coreDead.length), depth + 1);
					break;
				}
			} catch (final RuntimeContradictionException e) {
				for (final int literal : coreDead) {
					solver.assignmentPush(-literal);
				}
			}
		}
	}

}
