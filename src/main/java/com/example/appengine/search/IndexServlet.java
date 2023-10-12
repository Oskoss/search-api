package com.example.appengine.search;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Code snippet for getting a document from Index.
 */
@SuppressWarnings("serial")
@WebServlet(
    name = "searchIndex",
    description = "Search: Index a new document",
    urlPatterns = "/search/index"
)
public class IndexServlet extends HttpServlet {

  private static final String INDEX = "testIndex";

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    Document document =
        Document.newBuilder()
            .setId("AZ125")
            .addField(Field.newBuilder().setName("myField").setText("myValue"))
            .build();
    try {
      Utils.indexADocument(INDEX, document);
    } catch (InterruptedException e) {
      out.println("Interrupted");
      return;
    }
    out.println("Indexed a new document.");
    // [START get_document]
    IndexSpec indexSpec = IndexSpec.newBuilder().setName(INDEX).build();
    Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

    // Fetch a single document by its  doc_id
    Document doc = index.get("AZ125");

    // Fetch a range of documents by their doc_ids
    GetResponse<Document> docs =
        index.getRange(GetRequest.newBuilder().setStartId("AZ125").setLimit(100).build());
    // [END get_document]
    out.println("myField: " + docs.getResults().get(0).getOnlyField("myField").getText());
  }
}
