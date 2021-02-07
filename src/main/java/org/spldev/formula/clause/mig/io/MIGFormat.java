package org.spldev.formula.clause.mig.io;

import org.spldev.formula.clause.mig.*;
import org.spldev.util.*;
import org.spldev.util.io.format.*;

/**
 * Reads / Writes a feature graph.
 *
 * @author Sebastian Krieter
 */
public class MIGFormat implements Format<MIG> {

	public static final String ID = "format.mig." + MIGFormat.class.getSimpleName();

	@Override
	public Result<MIG> parse(CharSequence source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String serialize(MIG object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "ModalImplicationGraph";
	}

	@Override
	public String getFileExtension() {
		return "mig";
	}

}
