package io.onedev.server.git.service;

import java.io.Serializable;

public class AheadBehind implements Serializable {

	private final int ahead;
	
	private final int behind;

	public AheadBehind(int ahead, int behind) {
		this.ahead = ahead;
		this.behind = behind;
	}
	
	public int getAhead() {
		return ahead;
	}

	public int getBehind() {
		return behind;
	}

}
