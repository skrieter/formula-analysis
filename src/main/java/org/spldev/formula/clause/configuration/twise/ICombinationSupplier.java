package org.spldev.formula.clause.configuration.twise;

import java.util.function.*;

/**
 * An abstract supplier for combinations of elements.
 *
 * @param <T> The type of the elements.
 *
 * @author Sebastian Krieter
 */
public interface ICombinationSupplier<T> extends Supplier<T> {

	long size();

}
