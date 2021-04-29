package org.spldev.formula.clause.analysis;

import org.sat4j.core.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.mig.*;
import org.spldev.formula.clause.mig.visitor.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Finds core and dead features using a {@link MIG model implication graph}.
 *
 * @author Sebastian Krieter
 */
public class ConditionallyCoreDeadAnalysisMIG extends AConditionallyCoreDeadAnalysis {

	public static final Identifier<LiteralList> identifier = new Identifier<>();

	@Override
	public Identifier<LiteralList> getIdentifier() {
		return identifier;
	}

	private SatSolver solver;
	private MIG mig;

	@Override
	public Result<LiteralList> apply(Cache formula, InternalMonitor monitor) {
		monitor.setTotalWork(2);
		mig = formula.get(MIGProvider.fromCNF(), monitor.subTask(1)).get();
		return super.apply(formula, monitor.subTask(1));
	}

	public ConditionallyCoreDeadAnalysisMIG() {
		super();
	}

	public ConditionallyCoreDeadAnalysisMIG(MIG mig) {
		this.mig = mig;
	}

	@Override
	public LiteralList analyze(SatSolver solver, InternalMonitor monitor) throws Exception {
		this.solver = solver;
		monitor.setTotalWork(solver.getCnf().getVariableMap().size() + 2);

		final Traverser traverser = mig.traverse();
		solver.asignmentEnsure(fixedVariables.length + 1);
		final int[] knownValues = new int[solver.getCnf().getVariableMap().size()];

		for (final int fixedVar : fixedVariables) {
			final int var = Math.abs(fixedVar);
			knownValues[var - 1] = fixedVar;
			monitor.step();
		}

		// get core / dead variables
		for (final Vertex vertex : mig.getVertices()) {
			if (vertex.isCore()) {
				final int var = vertex.getVar();
				knownValues[Math.abs(var) - 1] = var;
				monitor.step();
			}
		}

		traverser.setModel(knownValues);
		final CollectingVisitor visitor = new CollectingVisitor();
		traverser.setVisitor(visitor);
		for (int i = 0; i < newCount; i++) {
			traverser.traverse(fixedVariables[i]);
		}
		final VecInt computedValues = visitor.getResult()[0];
		VecInt valuesToCompute = visitor.getResult()[1];

		monitor.setTotalWork(valuesToCompute.size() + computedValues.size() + 3);

		for (int i = 0; i < computedValues.size(); i++) {
			final int computedVar = computedValues.get(i);
			final int var = Math.abs(computedVar);
			knownValues[var - 1] = computedVar;
			monitor.step();
		}

		if (variableOrder != null) {
			final VecInt sortedValuesToCalulate = new VecInt(valuesToCompute.size());
			for (int i = variableOrder.length - 1; i >= 0; i--) {
				final int var = variableOrder[i];
				if (valuesToCompute.contains(var)) {
					sortedValuesToCalulate.push(var);
				}
				if (valuesToCompute.contains(-var)) {
					sortedValuesToCalulate.push(-var);
				}
			}
			valuesToCompute = sortedValuesToCalulate;
		}

		for (final int var : knownValues) {
			if (var != 0) {
				solver.assignmentPush(var);
			}
		}
		monitor.checkCancel();

		if (!valuesToCompute.isEmpty()) {
			solver.setSelectionStrategy(SelectionStrategy.POSITIVE);
			final int[] unkownValues = solver.findSolution();
			monitor.step();

			if (unkownValues != null) {
				solver.setSelectionStrategy(SelectionStrategy.NEGATIVE);
				final int[] model2 = solver.findSolution();
				monitor.step();

				LiteralList.resetConflicts(unkownValues, model2);
				solver.setSelectionStrategy(unkownValues, true);

				for (int k = 0; k < knownValues.length; k++) {
					final int var = knownValues[k];
					if ((var != 0) && (unkownValues[k] != 0)) {
						unkownValues[k] = 0;
					}
				}
				monitor.step();

				sat(unkownValues, valuesToCompute, monitor, traverser);
			}
		}
		return new LiteralList(solver.getAssignmentArray(0, solver.getAssignmentSize()));
	}

	private void sat(int[] unkownValues, VecInt valuesToCalulate, InternalMonitor monitor, Traverser traverser) {
		final CollectingVisitor visitor = new CollectingVisitor();
		traverser.setVisitor(visitor);

		while (!valuesToCalulate.isEmpty()) {
			final int varX = valuesToCalulate.last();
			valuesToCalulate.pop();
			final int i = Math.abs(varX) - 1;
			if (unkownValues[i] == varX) {
				solver.assignmentPush(-varX);
				switch (solver.hasSolution()) {
				case FALSE:
					solver.assignmentReplaceLast(varX);
					unkownValues[i] = 0;
					monitor.step();
					traverser.traverseStrong(varX);
					final VecInt newFoundValues = visitor.getResult()[0];
					for (int j = 0; j < newFoundValues.size(); j++) {
						final int var = newFoundValues.get(j);
						solver.assignmentPush(var);
						unkownValues[Math.abs(var) - 1] = 0;
						monitor.step();
					}
					break;
				case TIMEOUT:
					solver.assignmentPop();
					unkownValues[Math.abs(varX) - 1] = 0;
					monitor.step();
					break;
				case TRUE:
					solver.assignmentPop();
					LiteralList.resetConflicts(unkownValues, solver.getSolution());
					solver.shuffleOrder(getRandom());
					break;
				}
			}
		}
	}

}
