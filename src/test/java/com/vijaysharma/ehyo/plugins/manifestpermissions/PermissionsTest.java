package com.vijaysharma.ehyo.plugins.manifestpermissions;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.UsageException;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class PermissionsTest {
	private PermissionRegistry registry;
	private Permissions command;
	private Service service;
	
	@Captor
	ArgumentCaptor<Set<String>> captor;
	
	@Before
	public void before() {
		registry = Mockito.mock(PermissionRegistry.class);
		service = Mockito.mock(Service.class);
		command = new Permissions(registry);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_args_are_emtpy() {
		List<String> args = Lists.newArrayList();
		command.execute(args, service);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_args_dont_contain_add_or_remove() {
		List<String> args = Lists.newArrayList("test");
		command.execute(args, service);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_add_argument_has_no_value() {
		List<String> args = Lists.newArrayList("--add");
		command.execute(args, service);
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_permission_is_not_found_on_add() {
		List<String> args = Lists.newArrayList("--add", "blah");
		command.execute(args, service);
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_all_manifests_contain_privilege_on_add() {
		String permissionName = "permission1";
		ProjectManifest manifest = mock(ProjectManifest.class);
		List<ProjectManifest> manifests = Lists.newArrayList(manifest);
		OptionSelector<String> permissionSelector = mock(OptionSelector.class);
		OptionSelector<ProjectManifest> manifestSelector = mock(OptionSelector.class);
		Set<String> permissions = Sets.newHashSet(permissionName);
		List<String> listPermissions = Lists.newArrayList(permissionName);
		
		when(service.createSelector(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(permissionSelector);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(ProjectManifest.class))).thenReturn(manifestSelector);
		when(service.getManifests()).thenReturn(manifests);
		when(registry.find(Mockito.anyString())).thenReturn(permissions);
		when(manifest.getPermissions()).thenReturn(permissions);
		when(permissionSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listPermissions);
		
		List<String> args = Lists.newArrayList("--add", "internet");
		command.execute(args, service);
	}
	
	@Test
	public void execute_calls_add_permissions() {
		String permissionName = "permission1";
		ProjectManifest manifest = mock(ProjectManifest.class);
		OptionSelector<String> permissionSelector = mock(OptionSelector.class);
		OptionSelector<ProjectManifest> manifestSelector = mock(OptionSelector.class);
		List<ProjectManifest> manifests = Lists.newArrayList(manifest);
		Set<String> setPermissions = Sets.newHashSet(permissionName);
		Set<String> emptyPermissions = Sets.newHashSet();
		List<String> listPermissions = Lists.newArrayList(permissionName);
		
		when(service.createSelector(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(permissionSelector);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(ProjectManifest.class))).thenReturn(manifestSelector);
		when(service.getManifests()).thenReturn(manifests);
		when(registry.find(Mockito.anyString())).thenReturn(setPermissions);
		when(manifest.getPermissions()).thenReturn(emptyPermissions);
		when(permissionSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listPermissions);
		when(manifestSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(manifests);
		List<String> args = Lists.newArrayList("--add", "internet");
		command.execute(args, service);
		
		verify(manifest).addPermissions(captor.capture());
		Set<String> value = captor.getValue();
		assertEquals(1, value.size());
		assertEquals(true, value.contains(permissionName));
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_permission_is_not_found_on_remove() {
		List<String> args = Lists.newArrayList("--remove", "blah");
		command.execute(args, service);
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_all_manifests_contain_privilege_on_remove_with_argument() {
		String permissionName = "permission1";
		ProjectManifest manifest = mock(ProjectManifest.class);
		OptionSelector<String> permissionSelector = mock(OptionSelector.class);
		OptionSelector<ProjectManifest> manifestSelector = mock(OptionSelector.class);
		List<ProjectManifest> manifests = Lists.newArrayList(manifest);
		Set<String> setPermissions = Sets.newHashSet(permissionName);
		Set<String> emptyPermissions = Sets.newHashSet();
		List<String> listPermissions = Lists.newArrayList(permissionName);
		
		when(service.createSelector(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(permissionSelector);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(ProjectManifest.class))).thenReturn(manifestSelector);
		when(service.getManifests()).thenReturn(manifests);
		when(registry.find(Mockito.anyString())).thenReturn(setPermissions);
		when(manifest.getPermissions()).thenReturn(emptyPermissions);
		when(permissionSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listPermissions);
		when(manifestSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(manifests);
		
		List<String> args = Lists.newArrayList("--remove", "internet");
		command.execute(args, service);
	}
	
	@Test
	public void execute_calls_removePermissions_on_remove_with_argument() {
		String permissionName = "permission1";
		ProjectManifest manifest = mock(ProjectManifest.class);
		OptionSelector<String> permissionSelector = mock(OptionSelector.class);
		OptionSelector<ProjectManifest> manifestSelector = mock(OptionSelector.class);
		List<ProjectManifest> manifests = Lists.newArrayList(manifest);
		Set<String> setPermissions = Sets.newHashSet(permissionName);
		List<String> listPermissions = Lists.newArrayList(permissionName);
		
		when(service.createSelector(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(permissionSelector);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(ProjectManifest.class))).thenReturn(manifestSelector);
		when(service.getManifests()).thenReturn(manifests);
		when(registry.find(Mockito.anyString())).thenReturn(setPermissions);
		when(manifest.getPermissions()).thenReturn(setPermissions);
		when(permissionSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listPermissions);
		when(manifestSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(manifests);
		
		List<String> args = Lists.newArrayList("--remove", "internet");
		command.execute(args, service);
		
		verify(manifest).removePermissions(captor.capture());
		Set<String> value = captor.getValue();
		assertEquals(1, value.size());
		assertEquals(true, value.contains(permissionName));		
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_no_manifests_have_permissions() {
		ProjectManifest manifest = mock(ProjectManifest.class);
		List<ProjectManifest> manifests = Lists.newArrayList(manifest);
		Set<String> emptyPermissions = Sets.newHashSet();
		
		when(manifest.getPermissions()).thenReturn(emptyPermissions);
		when(service.getManifests()).thenReturn(manifests);
		
		List<String> args = Lists.newArrayList("--remove");
		command.execute(args, service);
	}
	
	@Test
	public void execute_calls_removePermissions_on_remove_without_argument() {
		String permissionName = "permission1";
		ProjectManifest manifest = mock(ProjectManifest.class);
		OptionSelector<String> permissionSelector = mock(OptionSelector.class);
		OptionSelector<ProjectManifest> manifestSelector = mock(OptionSelector.class);
		List<ProjectManifest> manifests = Lists.newArrayList(manifest);
		Set<String> setPermissions = Sets.newHashSet(permissionName);
		List<String> listPermissions = Lists.newArrayList(permissionName);
		
		when(service.createSelector(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(permissionSelector);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(ProjectManifest.class))).thenReturn(manifestSelector);
		when(service.getManifests()).thenReturn(manifests);
		when(registry.find(Mockito.anyString())).thenReturn(setPermissions);
		when(manifest.getPermissions()).thenReturn(setPermissions);
		when(permissionSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(listPermissions);
		when(manifestSelector.select(Mockito.anyList(), Mockito.anyBoolean())).thenReturn(manifests);
		
		List<String> args = Lists.newArrayList("--remove");
		command.execute(args, service);
		
		ArgumentCaptor<String> permissionCaptor = ArgumentCaptor.forClass(String.class);
		verify(manifest).removePermission(permissionCaptor.capture());
		assertEquals(permissionName, permissionCaptor.getValue());
	}
}
