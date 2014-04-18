package com.vijaysharma.ehyo.core.models;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class BuildTypeTest {

	@Test
	public void getCompileString_gets_compile_for_compile_type() {
		assertEquals("compile", BuildType.COMPILE.getCompileString());
	}
	
	@Test
	public void getCompileString_gets_compile_for_androidTest_type() {
		assertEquals("androidTestCompile", BuildType.ANDROID_TEST.getCompileString());
	}
	
	@Test
	public void getCompileString_gets_compile_for_debug_type() {
		assertEquals("debugCompile", BuildType.DEBUG.getCompileString());
	}
	
	@Test
	public void getCompileString_gets_compile_for_release_type() {
		assertEquals("releaseCompile", BuildType.RELEASE.getCompileString());
	}
	
	@Test
	public void getCompileString_gets_compile_for_compile_type_with_flavor() {
		assertEquals("flavor1Compile", BuildType.COMPILE.getCompileString(new Flavor("flavor1")));
	}
	
	@Test
	public void getCompileString_gets_compile_for_androidTest_type_with_flavor() {
		assertEquals("androidTestFlavor1Compile", BuildType.ANDROID_TEST.getCompileString(new Flavor("flavor1")));
	}
	
	@Test
	public void getCompileString_gets_compile_for_debug_type_with_flavor() {
		assertEquals("debugFlavor1Compile", BuildType.DEBUG.getCompileString(new Flavor("flavor1")));
	}
	
	@Test
	public void getCompileString_gets_compile_for_release_type_with_flavor() {
		assertEquals("releaseFlavor1Compile", BuildType.RELEASE.getCompileString(new Flavor("flavor1")));
	}
}
