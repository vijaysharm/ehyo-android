package com.vijaysharma.ehyo.core;

import java.io.File;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.commandline.PluginOptions;

public class RunAction implements Action {
	private final String[] args;
	private final File root;
	private final PluginOptions pluginOptions;
	private final boolean dryrun;
	private final boolean help;

	public RunAction(String[] args, 
					 File root, 
					 PluginOptions pluginOptions,
					 boolean dryrun, 
					 boolean help) {
		this.args = args;
		this.root = root;
		this.pluginOptions = pluginOptions;
		this.dryrun = dryrun;
		this.help = help;
	}

	@Override
	public void run() {
		
	}
}
