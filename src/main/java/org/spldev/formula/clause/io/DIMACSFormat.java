package org.spldev.formula.clause.io;

import java.io.*;
import java.text.ParseException;

import org.spldev.formula.clause.*;
import org.spldev.util.*;
import org.spldev.util.io.format.*;
import org.spldev.util.io.format.Format;

/**
 * Reads and writes feature models in the DIMACS CNF format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSFormat implements Format<CNF> {

	public static final String ID = ".format.cnf." + DIMACSFormat.class.getSimpleName();

	@Override
	public String serialize(CNF cnf) {
		final DimacsWriter w = new DimacsWriter(cnf);
		w.setWritingVariableDirectory(true);
		return w.write();
	}

	@Override
	public Result<CNF> parse(CharSequence source) {
		final DimacsReader r = new DimacsReader();
		r.setReadingVariableDirectory(true);
		try {
			return Result.of(r.read(source.toString()));
		} catch (final ParseException e) {
			return Result.empty(new ParseProblem(e, e.getErrorOffset()));
		} catch (final IOException e) {
			return Result.empty(e);
		}
	}

	@Override
	public DIMACSFormat getInstance() {
		return this;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public String getName() {
		return "DIMACS";
	}

	@Override
	public String getFileExtension() {
		return "dimacs";
	}

}
