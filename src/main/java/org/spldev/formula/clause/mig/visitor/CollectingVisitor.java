package org.spldev.formula.clause.mig.visitor;

import org.sat4j.core.*;

public class CollectingVisitor implements Visitor<VecInt[]> {
	final VecInt[] literalList = new VecInt[] { new VecInt(), new VecInt() };

	@Override
	public VisitResult visitStrong(int curLiteral) {
		literalList[0].push(curLiteral);
		return VisitResult.Continue;
	}

	@Override
	public VisitResult visitWeak(int curLiteral) {
		literalList[1].push(curLiteral);
		return VisitResult.Continue;
	}

	@Override
	public VecInt[] getResult() {
		return literalList;
	}
}
