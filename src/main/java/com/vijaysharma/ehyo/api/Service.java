package com.vijaysharma.ehyo.api;

import java.util.Collection;
import java.util.List;

import retrofit.RestAdapter;

import com.google.common.collect.ImmutableList;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class Service {
	private final Collection<Plugin> plugins;
	private final List<ProjectBuild> projectBuilds;
	private final OptionSelectorFactory selectorFactory;
	private final TemplateFactory templateFactory;
	
	public Service(Collection<Plugin> plugins, 
				   List<ProjectBuild> projectBuilds,
				   TemplateFactory templateFactory,
				   OptionSelectorFactory selectorFactory) {
		this.plugins = plugins;
		this.projectBuilds = projectBuilds;
		this.selectorFactory = selectorFactory;
		this.templateFactory = templateFactory;
	}

	public Collection<Plugin> getPlugins() {
		return plugins;
	}

	public List<ProjectBuild> getProjectBuilds() {
		return projectBuilds;
	}
	
	public <T> OptionSelector<T> createSelector(String header, Class<T> clazz) {
		return selectorFactory.create(header, clazz);
	}

	public List<ProjectManifest> getManifests() {
		ImmutableList.Builder<ProjectManifest> manifests = ImmutableList.builder();
		for ( ProjectBuild build : getProjectBuilds() ) {
			manifests.addAll(build.getManifests());
		}

		return manifests.build();
	}
	
	public List<BuildConfiguration> getBuildConfigurations() {
		ImmutableList.Builder<BuildConfiguration> configs = ImmutableList.builder();
		for( ProjectBuild build : getProjectBuilds() ) {
			configs.addAll(build.getBuildConfigurations());
		}

		return configs.build();
	}

	public Template loadTemplate(String templatePath) {
		return templateFactory.create(templatePath);
	}

	public MavenService getMavenService() {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    	.setEndpoint("http://search.maven.org")
	    	.build();
	
		return restAdapter.create(MavenService.class);
	}
}
