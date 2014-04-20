package com.vijaysharma.ehyo.plugins.search;

import com.vijaysharma.ehyo.plugins.search.models.QueryByNameResponse;

import retrofit.http.GET;
import retrofit.http.Query;

public interface MavenService {
	@GET("/solrsearch/select?wt=json")
	QueryByNameResponse searchByName(@Query("q") String library);
}
