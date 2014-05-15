package com.vijaysharma.ehyo.plugins.dependencies;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.MavenService;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.QueryByNameResponse;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.UsageException;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;

/**
 * TODO: I should have an object called "BuildArtifact" that combines the build
 * with its artifact instead of having to re-search which build a given artifact
 * came from
 */
public class Dependencies implements Plugin {
	private final TextOutput out;

	public Dependencies() {
		this(Output.out);
	}
	
	Dependencies(TextOutput out) {
		this.out = out;
	}

	@Override
	public String name() {
		return "dependencies";
	}

	@Override
	public String usage() {
		StringBuilder usage = new StringBuilder();
		usage.append("usage: ehyo dependencies [-s <search dependency by name>]\n");
		usage.append("                         [-g <search dependency by group id>\n");
		usage.append("                         [--add] [--upgrade] [--remove]\n\n");
		
		usage.append("Examples:\n");
		usage.append("    ehyo dependencies -s guava\n");
		usage.append("    ehyo dependencies -s retrofit --add\n");
		usage.append("    ehyo dependencies -g com.jakewharton --add\n");
		usage.append("    ehyo dependencies --upgrade\n");
		usage.append("    ehyo dependencies --remove butterknife\n");
		usage.append("    ehyo dependencies --remove\n");
		
		usage.append("Options:\n");
		usage.append("    -s <dependency name>\n");
		usage.append("        Search search.maven.org for the given dependency by name.\n");
		usage.append("        If neither --add or --remove are provided, a list of the found artifacts are displayed.\n");
		usage.append("        Cannot be used with the -g argument\n");
		usage.append("    -g <dependency group ID>\n");
		usage.append("        Search search.maven.org for the given dependency by group ID.\n");
		usage.append("        If neither --add or --remove are provided, a list of the found artifacts are displayed.\n");
		usage.append("        Cannot be used with the -s argument\n");		
		usage.append("    --add\n");
		usage.append("        Adds the above search results to the project build.\n");
		usage.append("        Requires one of the above search parameters to be defined.\n");
		usage.append("    --upgrade <dependency name>\n");
		usage.append("        Upgrades a dependency in a build file.\n");
		usage.append("        Ignores the above search options, and searches the build file for dependency\n");
		usage.append("        If a dependency name is not given, a list of upgrade-able dependencies will be listed.\n");
		usage.append("    --remove <dependency name>\n");
		usage.append("        Removes a dependency from a build file.\n");
		usage.append("        Ignores the above search options, and searches the build file for dependency\n");
		usage.append("        If a dependency name is not given, a list of removable dependencies will be listed.\n");
		
		usage.append("Notes:\n");
		usage.append("    This command requires an internet connection in order to search maven.org\n");
		
		return usage.toString();
	}

	@Override
	public void execute(List<String> args, Service service) {
		if ( args.isEmpty() )
			throw new UsageException(usage());
		
		boolean searchByName = args.contains("-s");
		boolean searchByGroupId = args.contains("-g");
		boolean containsRemove = args.contains("--remove");
		boolean containsAdd = args.contains("--add");
		boolean containsUpgrade = args.contains("--upgrade");
		
		if ( ! searchByName && ! containsRemove && ! searchByGroupId && ! containsUpgrade)
			throw new UsageException(usage());
		
		if ( searchByName && searchByGroupId )
			throw new UsageException("Can't search by name and group ID. Please choose one.\n" + usage());
		
		if ( searchByName ) {
			String name = getArgValue("-s", args);
			List<Artifact> artifacts = searchArtifactsByName(name, service);
			if ( containsAdd )
				addArtifacts(artifacts, service);
			else
				listArtifacts(artifacts, service);
		} else if ( searchByGroupId ) {
			String groupId = getArgValue("-g", args);
			List<Artifact> artifacts = searchArtifactsByGroupId(groupId, service);
			if ( containsAdd )
				addArtifacts(artifacts, service);
			else
				listArtifacts(artifacts, service);
		} else if (containsUpgrade ) {
			String toUpgrade = getArgValue("--upgrade", args);
			upgrade(toUpgrade, service);
		} else if ( containsRemove ) {
			String toRemove = getArgValue("--remove", args);
			remove(toRemove, service);
		}
	}

	private void upgrade(String toUpgrade, Service service ) {
		List<Artifact> all = getArtifactsFromBuild(toUpgrade, service);
		Map<Artifact, Artifact> upgrades = findUpgrades(all, service);

		if ( upgrades.isEmpty() )
			throw new GentleMessageException("None of your dependencies require upgrading");
		
		Map<Artifact, Artifact> doUpgrade = Maps.newHashMap();
		for ( Entry<Artifact, Artifact> upgrade : upgrades.entrySet() ) {
			if (doUpgrade(upgrade.getKey(), upgrade.getValue(), service))
				doUpgrade.put(upgrade.getKey(), upgrade.getValue());
		}
		
		for ( Entry<Artifact, Artifact> upgrade : doUpgrade.entrySet() ) {
			for( BuildConfiguration config : service.getBuildConfigurations() ) {
				Set<Artifact> allArtifacts = config.getArtifacts();
				if (allArtifacts.contains(upgrade.getKey())) {
					config.removeArtifact(upgrade.getKey());
					config.addArtifact(upgrade.getValue());
				}
			}
		}
	}
	
	private Map<Artifact, Artifact> findUpgrades(List<Artifact> all, Service service) {
		ImmutableMap.Builder<Artifact, Artifact> upgrades = ImmutableMap.builder();

		MavenService mavenService = service.getMavenService();
		for ( Artifact artifact : all ) {
			String query = "id:\"" + artifact.getId() + "\"";
			QueryByNameResponse response = mavenService.searchByName(query);
			Artifact[] artifacts = response.getResponse().getArtifacts();
			if ( artifacts.length == 0 )
				continue;

			Artifact upgrade = artifacts[0];
			if ( artifact.equals(upgrade) )
				continue;
			
			upgrades.put(artifact, artifacts[0]);
		}
		
		return upgrades.build();
	}

	private void remove(String toRemove, Service service) {
		List<Artifact> all = getArtifactsFromBuild(toRemove, service);

		List<Artifact> selectedArtifacts = service.createSelector("Which artifact would you like to remove?", Artifact.class)
				.select(all, false);
		
		List<BuildConfiguration> toModify = Lists.newArrayList();
		for( BuildConfiguration config : service.getBuildConfigurations() ) {
			Set<Artifact> artifacts = config.getArtifacts();
			for ( Artifact artifact : selectedArtifacts ) {
				if (artifacts.contains(artifact))
					toModify.add(config);
			}
		}
		
		List<BuildConfiguration> selectedBuilds = service.createSelector("Which configuration would you like to remove " + Joiner.on(", ").join(selectedArtifacts) + " from?", BuildConfiguration.class)
				.select(toModify, false);
		
		for ( BuildConfiguration config : selectedBuilds ) {
			config.removeArtifacts(Sets.newHashSet(selectedArtifacts));
		}
	}

	private List<Artifact> getArtifactsFromBuild(String library, Service service) {
		List<Artifact> all = Lists.newArrayList();
		
		if ( Strings.isNullOrEmpty(library) ) {
			for( BuildConfiguration config : service.getBuildConfigurations() ) {
				all.addAll(config.getArtifacts());
			}
			
			if ( all.isEmpty() )
				throw new GentleMessageException("No build dependecies found");
			
		} else {
			for( BuildConfiguration config : service.getBuildConfigurations() ) {
				Set<Artifact> artifacts = config.getArtifacts();
				for ( Artifact artifact : artifacts ) {
					if (artifact.toString().toLowerCase().contains(library.toLowerCase())) {
						all.add(artifact);
					}
				}
			}
			
			if ( all.isEmpty() )
				throw new GentleMessageException("No build configs found ressembling '" + library + "'");
		}
		
		return all;
	}

	private void listArtifacts(List<Artifact> artifacts, Service service) {
		StringBuilder output = new StringBuilder();
		output.append("Found the following artifacts:\n");
		for ( Artifact artifact : artifacts ) {
			output.append("    " + artifact + "\n");
		}
		
		output.append("Append --add to this command to add the dependency to your build.");
		out.println(output.toString());
	}

	private void addArtifacts(List<Artifact> artifacts, Service service) {
		List<Artifact> selectedArtifacts = service.createSelector("Which artifact would you like to add?", Artifact.class)
				.select(artifacts, false);
		
		List<BuildConfiguration> toModify = Lists.newArrayList();
		for ( BuildConfiguration config : service.getBuildConfigurations() ) {
			for ( Artifact artifact : artifacts ) {
				Set<Artifact> all = config.getArtifacts();
				if ( ! all.contains(artifact) ) {
					toModify.add(config);
					break;
				}
			}
		}
		
		if ( toModify.isEmpty() )
			throw new GentleMessageException("All build configurations contain " + Joiner.on(", ").join(selectedArtifacts));
		
		List<BuildConfiguration> selectedBuild = service.createSelector("Which build configuration would you like add " + Joiner.on(", ").join(selectedArtifacts) + " to?", BuildConfiguration.class)
				.select(toModify, false);
		
		for ( BuildConfiguration config : selectedBuild ) {
			for ( Artifact artifact : selectedArtifacts ) {
				Artifact existing = findSimilar(config, artifact);
				if ( existing == null ) {
					config.addArtifact(artifact);
				} else {
					if ( doUpgrade(config, existing, artifact, service) ) {
						config.removeArtifact(existing);
						config.addArtifact(artifact);
					}
				}
			}
		}
	}

	private boolean doUpgrade(Artifact existing, Artifact artifact, Service service) {
		StringBuilder message = new StringBuilder();
		message.append("A different version of " + artifact.getArtifactId() + " was found\n");
		message.append("Would you like to modify the dependency from ");
		message.append(existing.getLatestVersion() + " to " + artifact.getLatestVersion());
		
		String yes = "Yes - Switch " + artifact.getArtifactId() + " to " + artifact.getLatestVersion();
		String no = "No - Keep " + artifact.getArtifactId() + " at " + existing.getLatestVersion();
		
		List<String> yesno = Lists.newArrayList(yes, no);
		List<String> select = service.createSelector( message.toString(), String.class).select(yesno, false);
		
		return select.contains(yes);
	}
	
	private boolean doUpgrade(BuildConfiguration config, Artifact existing, Artifact artifact, Service service) {
		StringBuilder message = new StringBuilder();
		message.append("A different version of " + artifact.getArtifactId() + " was found in '" + config + "'.\n");
		message.append("Would you like to modify the dependency from ");
		message.append(existing.getLatestVersion() + " to " + artifact.getLatestVersion());
		
		String yes = "Yes - Switch " + artifact.getArtifactId() + " to " + artifact.getLatestVersion();
		String no = "No - Keep " + artifact.getArtifactId() + " at " + existing.getLatestVersion();
		
		List<String> yesno = Lists.newArrayList(yes, no);
		List<String> select = service.createSelector( message.toString(), String.class).select(yesno, false);
		
		return select.contains(yes);
	}

	private Artifact findSimilar(BuildConfiguration config, Artifact artifact) {
		for ( Artifact existing : config.getArtifacts() ) {
			if (existing.equals(artifact))
				continue;

			if (artifact.getGroupId().equals(existing.getGroupId()) && 
				artifact.getArtifactId().equals(existing.getArtifactId())) {
				return existing;
			}
		}
		
		return null;
	}
	
	private List<Artifact> searchArtifactsByName(String name, Service service) {
		if (Strings.isNullOrEmpty(name))
			throw new UsageException("'-s' has a required argument\n" + usage());
		
		MavenService mavenService = service.getMavenService();
		
		QueryByNameResponse response = mavenService.searchByName(name);

		Artifact[] artifacts = response.getResponse().getArtifacts();
		if ( artifacts.length == 0 )
			throw new GentleMessageException("No artifacts found containing '" + name + "'");
		
		return ImmutableList.copyOf(artifacts);
	}
	
	private List<Artifact> searchArtifactsByGroupId(String group, Service service) {
		if (Strings.isNullOrEmpty(group))
			throw new UsageException("'-g' has a required argument\n" + usage());
		
		MavenService mavenService = service.getMavenService();
		
		String query = "g:\"" + group + "\"";
		QueryByNameResponse response = mavenService.searchByName(query);

		Artifact[] artifacts = response.getResponse().getArtifacts();
		if ( artifacts.length == 0 )
			throw new GentleMessageException("No artifacts found containing '" + group + "'");
		
		return ImmutableList.copyOf(artifacts);
	}
	
	private static String getArgValue(String arg, List<String> args) {
		int libIndex = args.indexOf(arg);
		
		if (libIndex == -1) {
			return null;
		}
		
		int libValueIndex = libIndex + 1;
		if ( libValueIndex >= args.size() )
			return null;
		
		return args.get(libValueIndex);
	}
}
