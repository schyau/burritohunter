package com.potato.burritohunter.stuff;

import com.squareup.otto.Bus;

public final class SomeUtil {
	private static final Bus BUS = new Bus(); // down boy, don't be so eager.

	public static Bus getBus() {
		return BUS;
	}

	private SomeUtil() {}
	
	
}