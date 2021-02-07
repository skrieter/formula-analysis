package org.spldev.formula.clause.solver;

/**
 * Exception thrown when a {@link SatSolver solver} detects an obvious
 * contradiction when adding new clauses.<br>
 * Doesn't need to be caught explicitly.
 *
 * @author Sebastian Krieter
 */
public class RuntimeContradictionException extends RuntimeException {

	private static final long serialVersionUID = -4951752949650801254L;

	public RuntimeContradictionException() {
		super();
	}

	public RuntimeContradictionException(String message) {
		super(message);
	}

	public RuntimeContradictionException(Throwable cause) {
		super(cause);
	}

	public RuntimeContradictionException(String message, Throwable cause) {
		super(message, cause);
	}

}
