package com.vijaysharma.ehyo.api;

import retrofit.http.GET;
import retrofit.http.Query;

public interface MavenService {
	@GET("/solrsearch/select?wt=json")
	QueryByNameResponse searchByName(@Query("q") String library);
}
