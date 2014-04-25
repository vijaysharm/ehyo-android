package com.vijaysharma.ehyo.core.models;

public class BuildType {
	public static final BuildType COMPILE = new BuildType("compile");
	public static final BuildType RELEASE = new BuildType("release");
	public static final BuildType DEBUG = new BuildType("debug");
	public static final BuildType ANDROID_TEST = new BuildType("androidTest");
	
	private final String type;

	public BuildType(String type) {
		this.type = type;
	}
	
	public String getCompileString() {
		if ( this == COMPILE ) {
			return getType();
		}

		return getType() + "Compile";
	}
	
	public String getCompileString( Flavor flavor ) {
		if ( flavor == null ) {
			return getCompileString();
		}
		
		if ( this == COMPILE ) {
			return flavor.getFlavor().toLowerCase() + "Compile";
		}
	
		String flavorCompile = 
				Character.toUpperCase(flavor.getFlavor().charAt(0)) + 
									  flavor.getFlavor().substring(1);

		return getType() + flavorCompile + "Compile";
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "BuildType [type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuildType other = (BuildType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
