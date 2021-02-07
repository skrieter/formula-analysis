package org.spldev.formula.clause.mig.visitor;

public class DefaultVisitor implements Visitor<Void> {

	@Override
	public VisitResult visitStrong(int curLiteral) {
		return VisitResult.Continue;
	}

	@Override
	public VisitResult visitWeak(int curLiteral) {
		return VisitResult.Continue;
	}

	@Override
	public Void getResult() {
		return null;
	}

}
