package com.example.appengine.search;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

/**
 * A utility class for the search API sample.
 */
public class Utils {

  /**
   * Put a given document into an index with the given indexName.
   *
   * @param indexName The name of the index.
   * @param document A document to add.
   * @throws InterruptedException When Thread.sleep is interrupted.
   */
  public static void indexADocument(String indexName, Document document)
      throws InterruptedException {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
    Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

    final int maxRetry = 3;
    int attempts = 0;
    int delay = 2;
    while (true) {
      try {
        index.put(document);
      } catch (PutException e) {
        if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
            && ++attempts < maxRetry) { // retrying
          Thread.sleep(delay * 1000);
          delay *= 2; // easy exponential backoff
          continue;
        } else {
          throw e; // otherwise throw
        }
      }
      break;
    }
  }
}
