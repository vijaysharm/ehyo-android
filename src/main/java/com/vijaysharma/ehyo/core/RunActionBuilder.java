package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.List;
import java.util.Set;

public class RunActionBuilder {
	private boolean help;
	private boolean dryrun;
	private List<String> args;
	private Set<String> pluginNamespaces;
	private File root;

	public RunActionBuilder(List<String> args) {
		this.args = args;
		this.help = false;
		this.dryrun = false;
	}
	
	public void setShowHelp(boolean help) {
		this.help = help;
	}

	public void setDryrun(boolean dryrun) {
		this.dryrun = dryrun;
	}

	public void setDirectory(File root) {
		this.root = root;
	}
	
	public void setPluginNamespace(Set<String> namespaces) {
		pluginNamespaces = namespaces;
	}
	
	public Action build() {
		return new RunAction(this.args, root, pluginNamespaces, dryrun, help);
	}
}
