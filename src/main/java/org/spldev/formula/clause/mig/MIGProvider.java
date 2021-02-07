package org.spldev.formula.clause.mig;

import java.nio.file.*;

import org.spldev.formula.clause.*;
import org.spldev.formula.clause.mig.io.*;
import org.spldev.util.*;
import org.spldev.util.data.*;
import org.spldev.util.io.format.*;

/**
 * Abstract creator to derive an element from a {@link Cache }.
 *
 * @author Sebastian Krieter
 */
@FunctionalInterface
public interface MIGProvider extends Provider<MIG> {

	Identifier<MIG> identifier = new Identifier<>();

	@Override
	default Identifier<MIG> getIdentifier() {
		return identifier;
	}

	static MIGProvider empty() {
		return (c, m) -> Result.empty();
	}

	static MIGProvider of(MIG mig) {
		return (c, m) -> Result.of(mig);
	}

	static MIGProvider loader(Path path) {
		return (c, m) -> Provider.load(path, FormatSupplier.of(new MIGFormat()));
	}

	static <T> MIGProvider fromCNF() {
		return (c, m) -> Provider.convert(c, CNFProvider.identifier, new MIGBuilder(), m);
	}

	static <T> MIGProvider fromOldMig(MIG oldMig) {
		return (c, m) -> Provider.convert(c, CNFProvider.identifier, new IncrementalMIGBuilder(oldMig), m);
	}

}
