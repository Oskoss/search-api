package com.example.appengine.search;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Field.FieldType;
import com.google.appengine.api.search.GetIndexesRequest;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.Schema;
import com.google.appengine.api.search.SearchServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    name = "searchSchema",
    description = "Search: List the schema for a document.",
    urlPatterns = "/search/schema"
)
public class SchemaServlet extends HttpServlet {

  private static final String SEARCH_INDEX = "schemaIndex";

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    Document doc =
        Document.newBuilder()
            .setId("theOnlyCar")
            .addField(Field.newBuilder().setName("maker").setText("Toyota"))
            .addField(Field.newBuilder().setName("price").setNumber(300000))
            .addField(Field.newBuilder().setName("color").setText("lightblue"))
            .addField(Field.newBuilder().setName("model").setText("Prius"))
            .build();
    try {
      Utils.indexADocument(SEARCH_INDEX, doc);
    } catch (InterruptedException e) {
      // ignore
    }
    // [START list_schema]
    GetResponse<Index> response =
        SearchServiceFactory.getSearchService()
            .getIndexes(GetIndexesRequest.newBuilder().setSchemaFetched(true).build());

    // List out elements of each Schema
    for (Index index : response) {
      Schema schema = index.getSchema();
      for (String fieldName : schema.getFieldNames()) {
        List<FieldType> typesForField = schema.getFieldTypes(fieldName);
        // Just printing out the field names and types
        for (FieldType type : typesForField) {
          out.println(index.getName() + ":" + fieldName + ":" + type.name());
        }
      }
    }
    // [END list_schema]
  }
}