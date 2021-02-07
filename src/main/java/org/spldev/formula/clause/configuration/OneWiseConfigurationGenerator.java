package org.spldev.formula.clause.configuration;

import java.util.*;

import org.sat4j.core.*;
import org.spldev.formula.clause.*;
import org.spldev.formula.clause.solver.*;
import org.spldev.formula.clause.solver.SatSolver.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;

/**
 * Generates configurations for a given propositional formula such that one-wise
 * feature coverage is achieved.
 *
 * @author Sebastian Krieter
 */
public class OneWiseConfigurationGenerator extends AConfigurationGenerator implements ITWiseConfigurationGenerator {

	public static final Identifier<List<LiteralList>> identifier = new Identifier<>();

	@Override
	public Identifier<List<LiteralList>> getIdentifier() {
		return identifier;
	}

	public enum CoverStrategy {
		POSITIVE, NEGATIVE
	}

	private CoverStrategy coverStrategy = CoverStrategy.NEGATIVE;

	private int[] variables;

	public OneWiseConfigurationGenerator() {
		this(null);
	}

	public OneWiseConfigurationGenerator(int[] features) {
		super();
		setFeatures(features);
	}

	public int[] getFeatures() {
		return variables;
	}

	public void setFeatures(int[] features) {
		variables = features;
	}

	public CoverStrategy getCoverMode() {
		return coverStrategy;
	}

	public void setCoverMode(CoverStrategy coverStrategy) {
		this.coverStrategy = coverStrategy;
	}

	@Override
	protected void generate(SatSolver solver, InternalMonitor monitor) throws Exception {
		final int initialAssignmentLength = solver.getAssignmentSize();

		switch (coverStrategy) {
		case NEGATIVE:
			solver.setSelectionStrategy(SelectionStrategy.NEGATIVE);
			break;
		case POSITIVE:
			solver.setSelectionStrategy(SelectionStrategy.POSITIVE);
			break;
		default:
			throw new RuntimeException("Unknown " + CoverStrategy.class.getName() + ": " + coverStrategy);
		}

		if (solver.hasSolution() == SatResult.TRUE) {
			final VecInt variablesToCover = new VecInt();

			if (variables != null) {
				for (int i = 0; i < variables.length; i++) {
					final int var = variables[i];
					if (var > 0) {
						variablesToCover.push(var);
					}
				}
			}
			while (!variablesToCover.isEmpty()) {
				boolean firstVar = true;
				int[] lastSolution = null;
				for (int i = variablesToCover.size() - 1; i >= 0; i--) {
					int var = variablesToCover.get(i);
					if (var == 0) {
						continue;
					}

					switch (coverStrategy) {
					case NEGATIVE:
						var = -var;
						break;
					case POSITIVE:
						break;
					default:
						throw new RuntimeException("Unknown " + CoverStrategy.class.getName() + ": " + coverStrategy);
					}

					solver.assignmentPush(var);
					switch (solver.hasSolution()) {
					case FALSE:
						solver.assignmentReplaceLast(var);
						if (firstVar) {
							variablesToCover.set(i, 0);
						}
						break;
					case TIMEOUT:
						solver.assignmentPop();
						variablesToCover.set(i, 0);
						break;
					case TRUE:
						lastSolution = solver.getSolution();
						switch (coverStrategy) {
						case NEGATIVE:
							for (int j = i; j < variablesToCover.size(); j++) {
								if (lastSolution[Math.abs(var) - 1] < 0) {
									variablesToCover.set(i, 0);
								}
							}
							break;
						case POSITIVE:
							for (int j = i; j < variablesToCover.size(); j++) {
								if (lastSolution[Math.abs(var) - 1] > 0) {
									variablesToCover.set(i, 0);
								}
							}
							break;
						default:
							throw new RuntimeException("Unknown " + CoverStrategy.class.getName() + ": "
								+ coverStrategy);
						}
						firstVar = false;
						break;
					}
				}

				if (lastSolution != null) {
					addResult(new LiteralList(lastSolution, LiteralList.Order.INDEX, false));
				}
				solver.assignmentClear(initialAssignmentLength);

				while (!variablesToCover.isEmpty()) {
					final int var = variablesToCover.last();
					if (var == 0) {
						variablesToCover.pop();
					} else {
						break;
					}
				}
			}

		}
	}

}
