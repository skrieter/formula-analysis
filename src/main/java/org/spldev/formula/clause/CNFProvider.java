package org.spldev.formula.clause;

import java.nio.file.*;

import org.spldev.formula.expression.*;
import org.spldev.util.*;
import org.spldev.util.data.*;

/**
 * Abstract creator to derive an element from a {@link Cache}.
 *
 * @author Sebastian Krieter
 */
@FunctionalInterface
public interface CNFProvider extends Provider<CNF> {

	Identifier<CNF> identifier = new Identifier<>();

	@Override
	default Identifier<CNF> getIdentifier() {
		return identifier;
	}

	static CNFProvider empty() {
		return (c, m) -> Result.empty();
	}

	static CNFProvider of(CNF cnf) {
		return (c, m) -> Result.of(cnf);
	}

	static CNFProvider in(Cache cache) {
		return (c, m) -> cache.get(identifier);
	}

	static CNFProvider loader(Path path) {
		return (c, m) -> Provider.load(path, CNFFormatManager.getInstance());
	}

	static <T> CNFProvider fromExpression() {
		return (c, m) -> Provider.convert(c, ExpressionProvider.identifier, new FormulaToCNF(), m);
	}

}
