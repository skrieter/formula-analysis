package org.spldev.formula.clause.mig.visitor;

import org.spldev.formula.clause.mig.*;

abstract class ATraverser implements ITraverser {

	protected final boolean[] dfsMark;
	protected final MIG mig;

	protected Visitor<?> visitor = null;
	protected int[] currentConfiguration = null;

	public ATraverser(MIG mig) {
		this.mig = mig;
		dfsMark = new boolean[mig.getVertices().size()];
	}

	@Override
	public Visitor<?> getVisitor() {
		return visitor;
	}

	@Override
	public void setVisitor(Visitor<?> visitor) {
		this.visitor = visitor;
	}

	@Override
	public void setModel(int[] currentConfiguration) {
		this.currentConfiguration = currentConfiguration;
	}

}
