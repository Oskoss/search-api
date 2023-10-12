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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Code snippet for deleting documents from an Index.
 */
@SuppressWarnings("serial")
@WebServlet(
    name = "searchDelete",
    description = "Search: Delete a document from the index",
    urlPatterns = "/search/delete"
)
public class DeleteServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(DeleteServlet.class.getSimpleName());

  private static final String SEARCH_INDEX = "searchIndexForDelete";

  private Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName(SEARCH_INDEX).build();
    Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
    return index;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // Put one document to avoid an error
    Document document =
        Document.newBuilder().addField(Field.newBuilder().setName("f").setText("v")).build();
    try {
      Utils.indexADocument(SEARCH_INDEX, document);
    } catch (InterruptedException e) {
      // ignore
    }
    // [START delete_documents]
    try {
      // looping because getRange by default returns up to 100 documents at a time
      while (true) {
        List<String> docIds = new ArrayList<>();
        // Return a set of doc_ids.
        GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).build();
        GetResponse<Document> response = getIndex().getRange(request);
        if (response.getResults().isEmpty()) {
          break;
        }
        for (Document doc : response) {
          docIds.add(doc.getId());
        }
        getIndex().delete(docIds);
      }
    } catch (RuntimeException e) {
      LOG.log(Level.SEVERE, "Failed to delete documents", e);
    }
    // [END delete_documents]
    PrintWriter out = resp.getWriter();
    out.println("Deleted documents.");
  }
}
