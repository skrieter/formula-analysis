package org.spldev.formula.clause.configuration.twise;

import org.spldev.util.job.*;

public final class MemoryMonitor implements UpdateFunction {

	public long maxUsedMemory = 0;
	public long maxAllocatedMemory = 0;

	@Override
	public boolean update() {
		final long allocatedMemory = Runtime.getRuntime().totalMemory();
		final long freeMemory = Runtime.getRuntime().freeMemory();
		final long usedMemory = allocatedMemory - freeMemory;
		if (allocatedMemory > maxAllocatedMemory) {
			maxAllocatedMemory = allocatedMemory;
		}
		if (usedMemory > maxUsedMemory) {
			maxUsedMemory = usedMemory;
		}
		return true;
	}

}
