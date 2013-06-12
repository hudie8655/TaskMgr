package com.taskmgr;

import android.content.SearchRecentSuggestionsProvider;

public class MySearchSuggestion extends SearchRecentSuggestionsProvider {
	final static String AUTORITY="alan.search.autority";
	final static int MODE=DATABASE_MODE_QUERIES;
	public MySearchSuggestion() {
	 setupSuggestions(AUTORITY, MODE);
	}
	
}
