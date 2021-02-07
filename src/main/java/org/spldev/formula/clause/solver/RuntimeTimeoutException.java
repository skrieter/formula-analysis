package org.spldev.formula.clause.solver;

import org.spldev.formula.clause.analysis.*;

/**
 * Exception thrown when an {@link Analysis analysis} experiences a solver
 * timeout.<br>
 * Doesn't need to be caught explicitly.
 *
 * @author Sebastian Krieter
 */
public class RuntimeTimeoutException extends RuntimeException {

	private static final long serialVersionUID = -6922001608864037759L;

	public RuntimeTimeoutException() {
		super();
	}

	public RuntimeTimeoutException(String message) {
		super(message);
	}

	public RuntimeTimeoutException(Throwable cause) {
		super(cause);
	}

	public RuntimeTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
