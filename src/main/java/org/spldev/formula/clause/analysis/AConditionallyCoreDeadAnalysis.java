package org.spldev.formula.clause.analysis;

import org.spldev.formula.clause.*;

/**
 * Finds core and dead features under certain assumptions. Regularly returns
 * intermediate results to a given monitor.
 *
 * @author Sebastian Krieter
 */
public abstract class AConditionallyCoreDeadAnalysis extends AbstractAnalysis<LiteralList> {

	protected int[] fixedVariables;
	protected int[] variableOrder;
	protected int newCount;

	public AConditionallyCoreDeadAnalysis() {
		super();
		resetFixedFeatures();
	}

	public void setFixedFeatures(int[] fixedVariables, int newCount) {
		this.fixedVariables = fixedVariables;
		this.newCount = newCount;
	}

	public void setVariableOrder(int[] variableOrder) {
		this.variableOrder = variableOrder;
	}

	public void resetFixedFeatures() {
		fixedVariables = new int[0];
		newCount = 0;
	}

	public void updateModel(final int[] model1, int[] model2) {
		for (int i = 0; i < model1.length; i++) {
			final int x = model1[i];
			final int y = model2[i];
			if ((x != 0) && (x != y)) {
				model1[i] = 0;
			}
		}
	}

	protected static int countNegative(int[] model) {
		int count = 0;
		for (int i = 0; i < model.length; i++) {
			count += model[i] >>> (Integer.SIZE - 1);
		}
		return count;
	}

}
