package com.vijaysharma.ehyo.plugins.search.models;

public class QueryByNameResponse {
	private QueryResults response;
	public QueryResults getResponse() {
		return response;
	}
}
/**
 RESULT FOR: http://search.maven.org/solrsearch/select?q=guice&rows=20&wt=json
{
    "responseHeader": {
        "status": 0,
        "QTime": 1,
        "params": {
            "spellcheck": "true",
            "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
            "sort": "score desc,timestamp desc,g asc,a asc",
            "indent": "off",
            "q": "guice",
            "qf": "text^20 g^5 a^10",
            "spellcheck.count": "5",
            "wt": "json",
            "rows": "20",
            "version": "2.2",
            "defType": "dismax"
        }
    },
    "response": {
        "numFound": 303,
        "start": 0,
        "docs": [
            {
                "id": "com.google.inject:guice",
                "g": "com.google.inject",
                "a": "guice",
                "latestVersion": "4.0-beta4",
                "repositoryId": "central",
                "p": "jar",
                "timestamp": 1395352292000,
                "versionCount": 8,
                "text": [
                    "com.google.inject",
                    "guice",
                    "-sources.jar",
                    "-javadoc.jar",
                    "-test-sources.jar",
                    ".jar",
                    "-tests.jar",
                    "-site.jar",
                    "-no_aop.jar",
                    "-classes.jar",
                    ".pom"
                ],
                "ec": [
                    "-sources.jar",
                    "-javadoc.jar",
                    "-test-sources.jar",
                    ".jar",
                    "-tests.jar",
                    "-site.jar",
                    "-no_aop.jar",
                    "-classes.jar",
                    ".pom"
                ]
            },
        ]
    },
    "spellcheck": {
        "suggestions": []
    }
} 
 */