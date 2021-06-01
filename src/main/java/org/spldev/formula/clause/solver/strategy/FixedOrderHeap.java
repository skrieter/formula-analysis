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
package org.spldev.formula.clause.solver.strategy;

import org.sat4j.minisat.core.*;
import org.sat4j.minisat.orders.*;
import org.sat4j.specs.*;

/**
 * Modified variable order for {@link ISolver}.<br>
 * Initializes the used heap in a certain order.
 *
 * @author Sebastian Krieter
 */
public class FixedOrderHeap extends VarOrderHeap {

	private static final long serialVersionUID = 1L;
	private int[] order;

	public FixedOrderHeap(IPhaseSelectionStrategy strategy, int[] order) {
		super(strategy);
		this.order = order;
	}

	@Override
	public void init() {
		int nlength = lits.nVars() + 1;
		if ((activity == null) || (activity.length < nlength)) {
			activity = new double[nlength];
		}
		phaseStrategy.init(nlength);
		activity[0] = -1;
		heap = new Heap(activity);
		heap.setBounds(nlength);
		nlength--;
		for (int i = 0; i < nlength; i++) {
			final int x = order[i];
			activity[x] = 0.0;
			if (lits.belongsToPool(x)) {
				heap.insert(x);
			}
		}
	}

	public int[] getOrder() {
		return order;
	}

	public void setOrder(int[] order) {
		this.order = order;
	}

}
