package org.spldev.formula.clause;

import org.spldev.formula.clause.io.*;
import org.spldev.util.io.format.*;

public final class CNFFormatManager extends FormatManager<CNF> {

	private static CNFFormatManager INSTANCE = new CNFFormatManager();

	static {
		INSTANCE.addExtension(new DIMACSFormat());
	}

	public static CNFFormatManager getInstance() {
		return INSTANCE;
	}

	private CNFFormatManager() {
	}

}
