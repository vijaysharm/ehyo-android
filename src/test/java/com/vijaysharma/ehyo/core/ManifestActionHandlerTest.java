package com.vijaysharma.ehyo.core;

import static com.google.common.collect.Sets.newHashSet;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

@RunWith(MockitoJUnitRunner.class)
public class ManifestActionHandlerTest {
	private ManifestActionHandler handler;
	private AndroidManifestDocument document;
	private ManifestActions action;
	
	@Captor
	private ArgumentCaptor<Set<String>> captor;
	
	@Before
	public void before() {
		document = mock(AndroidManifestDocument.class);
		action = mock(ManifestActions.class);
		handler = new ManifestActionHandler();
	}
	
	@Test
	public void modify_adds_permissions_when_not_in_document() {
		String addedPermission = "permission1";
		Set<String> inDoc = newHashSet();
		Set<String> added = newHashSet(addedPermission);
		Set<String> removed = newHashSet();
		
		when(document.getPermissions()).thenReturn(inDoc);
		when(action.getAddedPermissions()).thenReturn(added);
		when(action.getRemovedPermissions()).thenReturn(removed);
		
		handler.modify(document, action);
		verify(document, times(1)).addPermission(captor.capture());
		assertEquals(1, captor.getValue().size());
		assertTrue(captor.getValue().contains(addedPermission));
		
		verify(document, times(0)).removePermission(captor.capture());
	}
	
	@Test
	public void modify_doesnt_add_permissions_when_in_document() {
		String addedPermission = "permission1";
		Set<String> inDoc = newHashSet(addedPermission);
		Set<String> added = newHashSet(addedPermission);
		Set<String> removed = newHashSet();
		
		when(document.getPermissions()).thenReturn(inDoc);
		when(action.getAddedPermissions()).thenReturn(added);
		when(action.getRemovedPermissions()).thenReturn(removed);
		
		handler.modify(document, action);
		verify(document, times(0)).addPermission(captor.capture());
		verify(document, times(0)).removePermission(captor.capture());
	}	
	
	@Test
	public void modify_doenst_removes_permissions_when_not_in_document() {
		String removedPermission = "permission1";
		Set<String> inDoc = newHashSet();
		Set<String> added = newHashSet();
		Set<String> removed = newHashSet(removedPermission);
		
		when(document.getPermissions()).thenReturn(inDoc);
		when(action.getAddedPermissions()).thenReturn(added);
		when(action.getRemovedPermissions()).thenReturn(removed);
		
		handler.modify(document, action);
		verify(document, times(0)).addPermission(captor.capture());
		verify(document, times(0)).removePermission(captor.capture());
	}	
	
	@Test
	public void modify_removes_permissions_when_found_in_document() {
		String removedPermission = "permission1";
		Set<String> inDoc = newHashSet(removedPermission);
		Set<String> added = newHashSet();
		Set<String> removed = newHashSet(removedPermission);
		
		when(document.getPermissions()).thenReturn(inDoc);
		when(action.getAddedPermissions()).thenReturn(added);
		when(action.getRemovedPermissions()).thenReturn(removed);
		
		handler.modify(document, action);
		verify(document, times(0)).addPermission(captor.capture());
		verify(document, times(1)).removePermission(captor.capture());
		assertEquals(1, captor.getValue().size());
		assertTrue(captor.getValue().contains(removedPermission));
	}
	
	@Test
	public void modify_wont_modify_document_if_same_permission_is_to_be_added_and_removed() {
		String permission = "permission1";
		Set<String> inDoc = newHashSet(permission);
		Set<String> added = newHashSet(permission);
		Set<String> removed = newHashSet(permission);
		
		when(document.getPermissions()).thenReturn(inDoc);
		when(action.getAddedPermissions()).thenReturn(added);
		when(action.getRemovedPermissions()).thenReturn(removed);
		
		handler.modify(document, action);
		verify(document, times(0)).addPermission(captor.capture());
		verify(document, times(0)).removePermission(captor.capture());
	}
	
	@Test
	public void modify_wont_modify_document_if_same_permission_is_to_be_added_and_removed_and_is_not_in_document() {
		String permission = "permission1";
		Set<String> inDoc = newHashSet();
		Set<String> added = newHashSet(permission);
		Set<String> removed = newHashSet(permission);
		
		when(document.getPermissions()).thenReturn(inDoc);
		when(action.getAddedPermissions()).thenReturn(added);
		when(action.getRemovedPermissions()).thenReturn(removed);
		
		handler.modify(document, action);
		verify(document, times(0)).addPermission(captor.capture());
		verify(document, times(0)).removePermission(captor.capture());
	}		
}
