package com.vijaysharma.ehyo.plugins.templates.android;

import java.util.List;

import joptsimple.internal.Strings;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TemplateRegistry {
	private static List<TemplateItem> TEMPLATES = Lists.newArrayList(
		new TemplateItem("/templates/activities", "MapFragmentMasterDetail"),
		new TemplateItem("/templates/activities", "SherlockBlankActivity"),
		new TemplateItem("/templates/activities", "SherlockMasterDetailFlow"),
		new TemplateItem("/templates/activities", "SlidingPaneMasterDetailFlow"),
		new TemplateItem("/templates/activities", "TVLeftNavBarActivity"),

		new TemplateItem("/templates/activities", "BlankActivity"),
		new TemplateItem("/templates/activities", "EmptyActivity"),
		new TemplateItem("/templates/activities", "FullscreenActivity"),
//			new TemplateItem("/templates/activities", "GoogleMapsActivity"), // Does not work "projectOut"
		new TemplateItem("/templates/activities", "GooglePlayServicesActivity"),
		new TemplateItem("/templates/activities", "LoginActivity"),
//			new TemplateItem("/templates/activities", "MasterDetailFlow"), // Does not work "appCompat" expected boolean, but is string.
		new TemplateItem("/templates/activities", "SettingsActivity"),

		new TemplateItem("/templates/other", "AppWidget"),
		new TemplateItem("/templates/other", "BlankFragment"),
		new TemplateItem("/templates/other", "BroadcastReceiver"),
		new TemplateItem("/templates/other", "ContentProvider"),
//			new TemplateItem("/templates/other", "CustomView"),	// Doesn't work isLibraryProject && isGradle
		new TemplateItem("/templates/other", "Daydream"),
		new TemplateItem("/templates/other", "IntentService"),
		new TemplateItem("/templates/other", "ListFragment"),
		new TemplateItem("/templates/other", "Notification"),
		new TemplateItem("/templates/other", "PlusOneFragment"),
		new TemplateItem("/templates/other", "Service"),

		new TemplateItem("/templates/other", "EfficientListAdapter"),
		new TemplateItem("/templates/other", "ParcelableType")
	);
	
	public List<TemplateItem> find() {
		return find(null);
	}
	
	public List<TemplateItem> find(String name) {
		ImmutableList.Builder<TemplateItem> template = ImmutableList.builder();
		
		if ( Strings.isNullOrEmpty(name ) ) {
			template.addAll(TEMPLATES);
		} else {
			for ( TemplateItem item : TEMPLATES ) {
				if ( item.getName().toLowerCase().contains(name.toLowerCase()) ) {
					template.add(item);
				}
			}
		}
		
		return template.build();
	}
	
	static class TemplateItem {
		private final String path;
		private final String name;
		public TemplateItem( String path, String name ) {
			this.path = path;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public String getFullPath() {
			return Joiner.on("/").join(path, name);
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
