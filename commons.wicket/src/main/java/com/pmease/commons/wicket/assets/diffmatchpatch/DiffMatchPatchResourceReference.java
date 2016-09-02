package com.pmease.commons.wicket.assets.diffmatchpatch;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class DiffMatchPatchResourceReference extends JavaScriptResourceReference {

	private static final long serialVersionUID = 1L;

	public DiffMatchPatchResourceReference() {
		super(DiffMatchPatchResourceReference.class, "diff_match_patch.js");
	}

}
