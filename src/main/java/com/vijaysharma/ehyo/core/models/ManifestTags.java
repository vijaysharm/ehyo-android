package com.vijaysharma.ehyo.core.models;

import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.Namespace;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ManifestTags {
	public static class Application {
		public static Application read(Element application, final Namespace namespace) {
			String[] properties = {
				"allowTaskReparenting",
				"allowBackup",
				"backupAgent",
				"debuggable",
				"description",
				"enabled",
				"hasCode",
				"hardwareAccelerated",
				"icon",
				"killAfterRestore",
				"largeHeap",
				"label",
				"logo",
				"manageSpaceActivity",
				"name",
				"permission",
				"persistent",
				"process",
				"restoreAnyVersion",
				"requiredAccountType",
				"restrictedAccountType",
				"supportsRtl",
				"taskAffinity",
				"testOnly",
				"theme",
				"uiOptions",
				"vmSafeMode"
			};
			
			ImmutableMap.Builder<String, String> attributes = ImmutableMap.builder();
			for ( String property : properties ) {
				String value = application.getAttributeValue(property, namespace);
				if ( value != null )
					attributes.put(property, value);
			}
			
			List<Activity> activities = ManifestTags.load(application, "", new Function<Element, Activity>() {
				@Override
				public Activity apply(Element input) {
					return Activity.read(input, namespace);
				}
			});
			
			List<MetaData> metadata = ManifestTags.load(application, "", new Function<Element, MetaData>() {
				@Override
				public MetaData apply(Element input) {
					return MetaData.read(input, namespace);
				}
			});
			
			List<Service> services = ManifestTags.load(application, "", new Function<Element, Service>() {
				@Override
				public Service apply(Element input) {
					return Service.read(input, namespace);
				}
			});
			
			List<Receiver> receiver = ManifestTags.load(application, "", new Function<Element, Receiver>() {
				@Override
				public Receiver apply(Element input) {
					return Receiver.read(input, namespace);
				}
			});
			
			List<String> libraries = ManifestTags.load(application, "", new Function<Element, String>() {
				@Override
				public String apply(Element input) {
					return input.getAttributeValue("name", namespace);
				}
			});
			
			return new Application(attributes.build(), activities, metadata, services, receiver, libraries);
		}
		
		private final List<Activity> activities;
		private final List<MetaData> metadata;
		private final List<Service> service;
		private final List<Receiver> receiver;
		private final List<String> libraries;
		private final Map<String, String> attributes;
		
		private Application(Map<String, String> attributes,
							List<Activity> activities,
							List<MetaData> metadata,
							List<Service> service,
							List<Receiver> receiver,
							List<String> libraries) {
			this.attributes = attributes;
			this.activities = activities;
			this.metadata = metadata;
			this.receiver = receiver;
			this.service = service;
			this.libraries = libraries;
		}
	}
		
	public static class Service {
		public static Service read(Element activity, Namespace namespace) {
			String[] properties = {
				"enabled",
				"exported",
				"icon",
				"isolatedProcess",
				"label",
				"name",
				"permission",
				"process"
			};
			
			ImmutableMap.Builder<String, String> attributes = ImmutableMap.builder();
			for ( String property : properties ) {
				String value = activity.getAttributeValue(property, namespace);
				if ( value != null )
					attributes.put(property, value);
			}
			
			ImmutableList.Builder<MetaData> metadatas = ImmutableList.builder();
			for ( Element metadata : activity.getChildren("meta-data") ) {
				metadatas.add(MetaData.read(metadata, namespace));
			}
			
			ImmutableList.Builder<IntentFilter> filters = ImmutableList.builder();
			for ( Element filter : activity.getChildren("intent-filter") ) {
				filters.add(IntentFilter.read(filter, namespace));
			}
			
			return new Service(attributes.build(), metadatas.build(), filters.build());
		}
		
		private final List<MetaData> metadatas;
		private final List<IntentFilter> filters;
		private final Map<String, String> attributes;
		
		private Service(Map<String, String> attributes,
						List<MetaData> metadatas,
						List<IntentFilter> filters) {
			this.attributes = attributes;
			this.metadatas = metadatas;
			this.filters = filters;
		}
	}
	
	public static class Receiver {
		public static Receiver read(Element activity, Namespace namespace) {
			String[] properties = {
				"enabled",
				"exported",
				"icon",
				"label",
				"name",
				"permission",
				"process"
			};
			
			ImmutableMap.Builder<String, String> attributes = ImmutableMap.builder();
			for ( String property : properties ) {
				String value = activity.getAttributeValue(property, namespace);
				if ( value != null )
					attributes.put(property, value);
			}
			
			ImmutableList.Builder<MetaData> metadatas = ImmutableList.builder();
			for ( Element metadata : activity.getChildren("meta-data") ) {
				metadatas.add(MetaData.read(metadata, namespace));
			}
			
			ImmutableList.Builder<IntentFilter> filters = ImmutableList.builder();
			for ( Element filter : activity.getChildren("intent-filter") ) {
				filters.add(IntentFilter.read(filter, namespace));
			}
			
			return new Receiver(attributes.build(), metadatas.build(), filters.build());
		}
		
		private final List<MetaData> metadatas;
		private final List<IntentFilter> filters;
		private final Map<String, String> attributes;
		
		private Receiver(Map<String, String> attributes,
						 List<MetaData> metadatas,
						 List<IntentFilter> filters) {
			this.attributes = attributes;
			this.metadatas = metadatas;
			this.filters = filters;
		}
	}
	
	public static class Activity {
		public static Activity read(Element activity, Namespace namespace) {
			String[] properties = {
				"allowTaskReparenting",
				"alwaysRetainTaskState",
				"clearTaskOnLaunch",
				"configChanges",
				"enabled",
				"excludeFromRecents",
				"exported",
				"finishOnTaskLaunch",
				"hardwareAccelerated",
				"icon",
				"label",
				"launchMode",
				"multiprocess",
				"name",
				"noHistory",
				"parentActivityName",
				"permission",
				"process",
				"screenOrientation",
				"stateNotNeeded",
				"taskAffinity",
				"theme",
				"uiOptions",
				"windowSoftInputMode"
			};
			
			ImmutableMap.Builder<String, String> attributes = ImmutableMap.builder();
			for ( String property : properties ) {
				String value = activity.getAttributeValue(property, namespace);
				if ( value != null )
					attributes.put(property, value);
			}
			
			ImmutableList.Builder<MetaData> metadatas = ImmutableList.builder();
			for ( Element metadata : activity.getChildren("meta-data") ) {
				metadatas.add(MetaData.read(metadata, namespace));
			}
			
			ImmutableList.Builder<IntentFilter> filters = ImmutableList.builder();
			for ( Element filter : activity.getChildren("intent-filter") ) {
				filters.add(IntentFilter.read(filter, namespace));
			}
			
			return new Activity(attributes.build(), metadatas.build(), filters.build());
		}
		
		private final List<MetaData> metadatas;
		private final List<IntentFilter> filters;
		private final Map<String, String> attributes;
		
		private Activity(Map<String, String> attributes,
						 List<MetaData> metadatas,
						 List<IntentFilter> filters) {
			this.attributes = attributes;
			this.metadatas = metadatas;
			this.filters = filters;
		}
	}
	public static class MetaData {
		public static MetaData read(Element element, Namespace namespace) {
			ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
			String[] expected = { "name", "resource", "value" };
			for ( String property : expected ) {
				String value = element.getAttributeValue(property, namespace);
				if ( value != null )
					properties.put(property, value);
			}
			
			return new MetaData(properties.build());
		}
		
		private final Map<String, String> attributes;
		private MetaData(Map<String, String> attributes) {
			this.attributes = attributes;
		}
	}
	
	public static class IntentFilter {
		private static IntentFilter read(Element element, Namespace namespace) {
			ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
			String[] expected = { "icon", "label", "priority" };

			for ( String property : expected ) {
				String value = element.getAttributeValue(property, namespace);
				if ( value != null )
					properties.put(property, value);
			}
			
			ImmutableList.Builder<Category> category = ImmutableList.builder();
			for ( Element filter : element.getChildren("category") ) {
				category.add(Category.read(filter, namespace));
			}
			
			ImmutableList.Builder<Data> data = ImmutableList.builder();
			for ( Element filter : element.getChildren("data") ) {
				data.add(Data.read(filter, namespace));
			}
			
			ImmutableList.Builder<IntentAction> action = ImmutableList.builder();
			for ( Element filter : element.getChildren("data") ) {
				action.add(IntentAction.read(filter, namespace));
			}
			
			return new IntentFilter(properties.build(), action.build(), category.build(), data.build());
		}
		
		private final Map<String, String> attributes;
		private final List<IntentAction> actions;
		private final List<Category> categories;
		private final List<Data> data;

		private IntentFilter(Map<String, String> attributes, List<IntentAction> actions, List<Category> categories, List<Data> data) {
			this.attributes = attributes;
			this.actions = actions;
			this.categories = categories;
			this.data = data;
		}
	}
	
	public static class IntentAction {
		private static IntentAction read(Element element, Namespace namespace) {
			ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
			String[] expected = { "name" };

			for ( String property : expected ) {
				String value = element.getAttributeValue(property, namespace);
				if ( value != null )
					properties.put(property, value);
			}
			
			return new IntentAction(properties.build());
		}
		
		private final Map<String, String> attributes;
		private IntentAction(Map<String, String> attributes) {
			this.attributes = attributes;
		}
	}
	
	public static class Category {
		private static Category read(Element element, Namespace namespace) {
			ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
			String[] expected = { "name" };

			for ( String property : expected ) {
				String value = element.getAttributeValue(property, namespace);
				if ( value != null )
					properties.put(property, value);
			}
			
			return new Category(properties.build());
		}
		
		private final Map<String, String> attributes;
		private Category(Map<String, String> attributes) {
			this.attributes = attributes;
		}
	}
	
	public static class Data {
		private static Data read(Element element, Namespace namespace) {
			ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
			String[] expected = {
				"scheme",
				"host",
				"port",
				"path",
				"pathPattern",
				"pathPrefix",
				"mimeType"
			};

			for ( String property : expected ) {
				String value = element.getAttributeValue(property, namespace);
				if ( value != null )
					properties.put(property, value);
			}
			
			return new Data(properties.build());
		}
		
		private final Map<String, String> attributes;
		private Data(Map<String, String> attributes) {
			this.attributes = attributes;
		}
	}
	
	static <T> List<T> load(Element element, String attribute, Function<Element, T> transform) {
		ImmutableList.Builder<T> metadatas = ImmutableList.builder();
		for ( Element elem : element.getChildren(attribute) ) {
			metadatas.add(transform.apply(elem));
		}
		
		return metadatas.build();
	}
 }
