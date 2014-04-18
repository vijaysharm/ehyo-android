package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.InternalActions.BuildActions;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;

public class BuildActionHandler implements PluginActionHandler<GradleBuildDocument, BuildActions>{
	@Override
	public void modify(GradleBuildDocument document, BuildActions action) {
		Collection<Dependency> add = action.getAddedDependencies();
		Collection<Dependency> remove = action.getRemovedDependencies();
		Collection<Dependency> all = document.getDependencies();
		
		Set<Dependency> toBeAdded = Sets.newHashSet(add);
		Set<Dependency> toBeRemoved = Sets.newHashSet(remove);
		toBeAdded.removeAll(remove);
		toBeRemoved.removeAll(add);
		
		for ( Dependency dependency : remove ) {
			if (!all.contains(dependency))
				toBeRemoved.remove(dependency);
		}
		
		for ( Dependency dependency : add ) {
			if (all.contains(dependency))
				toBeAdded.remove(dependency);
		}
		
		for ( Dependency dependency : toBeAdded ) {
			document.addDependency(dependency);
		}
		
		for ( Dependency dependency : toBeRemoved ) {
			document.removeDependency(dependency);
		}		
	}

//	private String formatDependency(BuildActionDependencyValue lib) {
//		StringBuilder dependency = new StringBuilder();
//		
//		DefaultBuildConfiguration configuration = lib.getConfiguration();
//		BuildType buildType = configuration.getBuildType();
//		Flavor flavor = configuration.getFlavor();
//		
//		String compileString = buildType.getCompileString(flavor);
//		dependency.append(compileString).append(" \'" + lib.getDependency() + "\'");
//		
//		return dependency.toString();
//	}
}
