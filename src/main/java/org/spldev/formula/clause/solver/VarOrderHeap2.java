package org.spldev.formula.clause.solver;

import org.sat4j.minisat.core.*;
import org.sat4j.minisat.orders.*;
import org.sat4j.specs.*;

/**
 * Modified variable order for {@link ISolver}.<br>
 * Initializes the used heap in a certain order.
 *
 * @author Sebastian Krieter
 */
public class VarOrderHeap2 extends VarOrderHeap {

	private static final long serialVersionUID = 1L;
	private int[] order;

	public VarOrderHeap2(IPhaseSelectionStrategy strategy, int[] order) {
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
