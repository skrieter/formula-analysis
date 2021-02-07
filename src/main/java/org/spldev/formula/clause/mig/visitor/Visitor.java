package org.spldev.formula.clause.mig.visitor;

public interface Visitor<T> {

	public enum VisitResult {
		Cancel, Continue, Skip, Select
	}

	/**
	 * Called when the traverser first reaches the literal via a strong path and the
	 * corresponding variable is still undefined.
	 *
	 * @param literal the literal reached
	 * @return VisitResult
	 */
	VisitResult visitStrong(int literal);

	/**
	 * Called when the traverser first reaches the literal via a weak path and the
	 * corresponding variable is still undefined.
	 *
	 * @param literal the literal reached
	 * @return VisitResult
	 */
	VisitResult visitWeak(int literal);

	T getResult();

}
