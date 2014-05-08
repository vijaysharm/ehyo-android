package com.vijaysharma.ehyo.core.models;

public class SourceSetType {
	private final String type;
	
	public SourceSetType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "SourceSetType [type=" + type + "]";
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
		SourceSetType other = (SourceSetType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
