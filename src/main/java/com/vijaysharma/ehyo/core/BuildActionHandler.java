package com.vijaysharma.ehyo.core;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.InternalActions.BuildActions;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.DependencyType;
import com.vijaysharma.ehyo.core.models.GradleBuildDocument;

public class BuildActionHandler implements PluginActionHandler<GradleBuildDocument, BuildActions>{
	
	// TODO: Need to output dependencies that are not defined in the document
	// during removal, and need to output when adding an existing dependency 
	@Override
	public void modify(GradleBuildDocument document, BuildActions action) {
		Collection<Dependency> add = action.getAddedDependencies();
		Collection<Dependency> remove = action.getRemovedDependencies();
		Collection<Dependency> all = getAllDependencies(document);
		
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
		
		if ( ! toBeAdded.isEmpty() )
			document.addDependencies(toBeAdded);
		
		if ( ! toBeRemoved.isEmpty() )
			document.removeDependencies(toBeRemoved);
	}

	private Collection<Dependency> getAllDependencies(GradleBuildDocument document) {
		Set<Dependency> dependencies = Sets.newHashSet();
		for ( Entry<DependencyType, Set<Dependency>> entry : document.dependencies().entrySet() )
			dependencies.addAll(entry.getValue());

		return dependencies;
	}
}
