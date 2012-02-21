package com.mtihc.minecraft.worldguardflagger;

public enum Permission {

	PRESETS("worldguardflagger.presets"),
	FLAGS("worldguardflagger.flags"),
	CLEAR("worldguardflagger.clear"),
	CLEAR_MEMBER("worldguardflagger.clear.member"),
	CLEAR_OWNER("worldguardflagger.clear.owner"),
	SET("worldguardflagger.set"),
	SET_MEMBER("worldguardflagger.set.member"),
	SET_OWNER("worldguardflagger.set.owner"),
	RELOAD("worldguardflagger.reload");
	
	private String node;

	/**
	 * 
	 * @param the permission node
	 */
	private Permission(String node) {
		this.node = node;
	}

	/**
	 * @return the permission node
	 */
	public String getNode() {
		return node;
	}
}
