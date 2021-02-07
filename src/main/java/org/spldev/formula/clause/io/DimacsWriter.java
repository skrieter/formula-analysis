package org.spldev.formula.clause.io;

import org.spldev.formula.clause.*;

public class DimacsWriter {

	/**
	 * Whether the writer should write a variable directory listing the names of the
	 * variables.
	 */
	private boolean writingVariableDirectory = true;

	private final CNF cnf;

	/**
	 * Constructs a new instance of this class with the given CNF.
	 *
	 * @param cnf the CNF to transform; not null
	 * @throws IllegalArgumentException if the input is null or not in CNF
	 */
	public DimacsWriter(CNF cnf) throws IllegalArgumentException {
		if (cnf == null) {
			throw new IllegalArgumentException();
		}
		this.cnf = cnf;
	}

	/**
	 * <p>
	 * Sets the writing variable directory flag. If true, the writer will write a
	 * variable directory at the start of the output. This is a set of comments
	 * naming the variables. This can later be used during reading so the variables
	 * are not just numbers.
	 * </p>
	 *
	 * <p>
	 * Defaults to false.
	 * </p>
	 *
	 * @param writingVariableDirectory whether to write the variable directory
	 */
	public void setWritingVariableDirectory(boolean writingVariableDirectory) {
		this.writingVariableDirectory = writingVariableDirectory;
	}

	public boolean isWritingVariableDirectory() {
		return writingVariableDirectory;
	}

	/**
	 * Writes the DIMACS CNF file format.
	 *
	 * @return the transformed CNF; not null
	 */
	public String write() {
		final StringBuilder sb = new StringBuilder();
		if (writingVariableDirectory) {
			writeVariableDirectory(sb);
		}
		writeProblem(sb);
		writeClauses(sb);
		return sb.toString();
	}

	/**
	 * Writes the variable directory.
	 *
	 * @param sb the string builder that builds the document
	 */
	private void writeVariableDirectory(StringBuilder sb) {
		int index = 1;
		for (final String name : cnf.getVariableMap().getNames()) {
			writeVariableDirectoryEntry(sb, index++, name);
		}
	}

	/**
	 * Writes an entry of the variable directory.
	 *
	 * @param sb       the string builder that builds the document
	 * @param variable variable to list in the entry
	 * @param index    index of the variable
	 */
	private void writeVariableDirectoryEntry(StringBuilder sb, int index, String name) {
		sb.append(DIMACSConstants.COMMENT_START);
		sb.append(index);
		sb.append(' ');
		sb.append(String.valueOf(name));
		sb.append(System.lineSeparator());
	}

	/**
	 * Writes the problem description.
	 *
	 * @param sb the string builder that builds the document
	 */
	private void writeProblem(StringBuilder sb) {
		sb.append(DIMACSConstants.PROBLEM);
		sb.append(' ');
		sb.append(DIMACSConstants.CNF);
		sb.append(' ');
		sb.append(cnf.getVariableMap().size());
		sb.append(' ');
		sb.append(cnf.getClauses().size());
		sb.append(System.lineSeparator());
	}

	/**
	 * Writes the given clause.
	 *
	 * @param sb     the string builder that builds the document
	 * @param clause clause to transform; not null
	 */
	private void writeClause(StringBuilder sb, LiteralList clause) {
		for (final int l : clause.getLiterals()) {
			sb.append(l);
			sb.append(' ');
		}
		sb.append(DIMACSConstants.CLAUSE_END);
		sb.append(System.lineSeparator());
	}

	/**
	 * Writes all clauses.
	 *
	 * @param sb the string builder that builds the document
	 */
	private void writeClauses(StringBuilder sb) {
		for (final LiteralList clause : cnf.getClauses()) {
			writeClause(sb, clause);
		}
	}

}
