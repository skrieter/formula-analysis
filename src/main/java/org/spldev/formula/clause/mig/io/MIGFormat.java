/* -----------------------------------------------------------------------------
 * Formula-Analysis-Lib - Library to analyze propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Analysis-Lib.
 * 
 * Formula-Analysis-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Analysis-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Analysis-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
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
