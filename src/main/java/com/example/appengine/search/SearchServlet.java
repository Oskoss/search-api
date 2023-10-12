package com.example.appengine.search;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(
    name = "search",
    description = "Search: Search for a document",
    urlPatterns = "/search/search"
)
public class SearchServlet extends HttpServlet {

  private static final String SEARCH_INDEX = "searchIndex";

  private Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName(SEARCH_INDEX).build();
    Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
    return index;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    final int maxRetry = 3;
    int attempts = 0;
    int delay = 2;
    while (true) {
      try {
        String queryString = "product = piano AND price.us < 5000";
        Results<ScoredDocument> results = getIndex().search(queryString);

        // Iterate over the documents in the results
        for (ScoredDocument document : results) {
          // handle results
          out.print("maker: " + document.getOnlyField("maker").getText());
          out.println(", price: " + document.getOnlyField("price").getNumber());
        }
      } catch (SearchException e) {
        if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
            && ++attempts < maxRetry) {
          // retry
          try {
            Thread.sleep(delay * 1000);
          } catch (InterruptedException e1) {
            // ignore
          }
          delay *= 2; // easy exponential backoff
          continue;
        } else {
          throw e;
        }
      }
      break;
    }
    // We don't test the search result below, but we're fine if it runs without errors.
    out.println("Search performed");
    Index index = getIndex();
    index.search("rose water");
    index.search("1776-07-04");
    // search for documents with pianos that cost less than $5000
    index.search("product = piano AND price < 5000");
  }
}
