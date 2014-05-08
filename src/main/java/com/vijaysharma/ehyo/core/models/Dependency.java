package com.vijaysharma.ehyo.core.models;

public class Dependency {
	private final DependencyType type;
	private final String library;
	
	public Dependency(DependencyType type, String library) {
		this.type = type;
		this.library = library;
	}

	public DependencyType getType() {
		return type;
	}

	public String getLibrary() {
		return library;
	}

	@Override
	public String toString() {
		return "Dependency [type=" + type + ", library=" + library + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((library == null) ? 0 : library.hashCode());
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
		Dependency other = (Dependency) obj;
		if (library == null) {
			if (other.library != null)
				return false;
		} else if (!library.equals(other.library))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
