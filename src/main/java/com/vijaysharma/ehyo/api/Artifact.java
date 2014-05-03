package com.vijaysharma.ehyo.api;

import static com.google.common.base.Joiner.on;

import com.google.common.base.Strings;

public class Artifact {
	private String id;
	private String g;
	private String a;
	private String latestVersion;
	
	public String getId() {
		return id;
	}
	
	public String getGroupId() {
		return g;
	}
	
	public String getArtifactId() {
		return a;
	}
	
	public String getLatestVersion() {
		return latestVersion;
	}

	@Override
	public String toString() {
		return on(":").join(getGroupId(), getArtifactId(), getLatestVersion());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((g == null) ? 0 : g.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((latestVersion == null) ? 0 : latestVersion.hashCode());
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
		Artifact other = (Artifact) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (g == null) {
			if (other.g != null)
				return false;
		} else if (!g.equals(other.g))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (latestVersion == null) {
			if (other.latestVersion != null)
				return false;
		} else if (!latestVersion.equals(other.latestVersion))
			return false;
		return true;
	}

	public static Artifact read(String library) {
		if (Strings.isNullOrEmpty(library))
			throw new IllegalArgumentException("Cannot parse empty library");
		
		String[] split = library.split(":");
		if (split.length != 3)
			throw new IllegalArgumentException("Failed to parse library '" + library + "'");
		
		Artifact artifact = new Artifact();
		artifact.g = split[0];
		artifact.a = split[1];
		artifact.latestVersion = split[2];
		artifact.id = split[0] + ":" + split[1];
		
		return artifact;
	}
}
