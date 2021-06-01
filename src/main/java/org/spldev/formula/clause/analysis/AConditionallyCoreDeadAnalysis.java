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

	protected static int countNegative(int[] model) {
		int count = 0;
		for (int i = 0; i < model.length; i++) {
			count += model[i] >>> (Integer.SIZE - 1);
		}
		return count;
	}

}
