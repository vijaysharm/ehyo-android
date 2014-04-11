package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.List;

import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class RunActionBuilder {

	private boolean help;
	private boolean dryrun;
	private List<String> args;
	private PluginOptions pluginOptions;
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

	public void setPluginOptions(PluginOptions options) {
		this.pluginOptions = options;
		
	}

	public void setDirectory(File root) {
		this.root = root;
	}
	
	public Action build() {
		return new RunAction(this.args, root, pluginOptions, dryrun, help);
	}
}
