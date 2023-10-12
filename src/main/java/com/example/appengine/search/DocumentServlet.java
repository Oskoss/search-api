package com.example.appengine.search;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet for creating Search API Document.
 */
@SuppressWarnings("serial")
@WebServlet(
    name = "searchCreate",
    description = "Search: Create the Search API document.",
    urlPatterns = "/search"
)
public class DocumentServlet extends HttpServlet {

  /**
   * Code snippet for creating a Document.
   *
   * @return Document Created document.
   */
  public Document createDocument() {
    // [START create_document]
    User currentUser = UserServiceFactory.getUserService().getCurrentUser();
    String userEmail = currentUser == null ? "" : currentUser.getEmail();
    String userDomain = currentUser == null ? "" : currentUser.getAuthDomain();
    String myDocId = "PA6-5000";
    Document doc =
        Document.newBuilder()
            // Setting the document identifer is optional.
            // If omitted, the search service will create an identifier.
            .setId(myDocId)
            .addField(Field.newBuilder().setName("content").setText("the rain in spain"))
            .addField(Field.newBuilder().setName("email").setText(userEmail))
            .addField(Field.newBuilder().setName("domain").setAtom(userDomain))
            .addField(Field.newBuilder().setName("published").setDate(new Date()))
            .build();
    // [END create_document]
    return doc;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    Document document =
        Document.newBuilder()
            .addField(Field.newBuilder().setName("coverLetter").setText("CoverLetter"))
            .addField(Field.newBuilder().setName("resume").setHTML("<html></html>"))
            .addField(Field.newBuilder().setName("fullName").setAtom("Foo Bar"))
            .addField(Field.newBuilder().setName("submissionDate").setDate(new Date()))
            .build();
    // [START access_document]
    String coverLetter = document.getOnlyField("coverLetter").getText();
    String resume = document.getOnlyField("resume").getHTML();
    String fullName = document.getOnlyField("fullName").getAtom();
    Date submissionDate = document.getOnlyField("submissionDate").getDate();
    // [END access_document]
    out.println("coverLetter: " + coverLetter);
    out.println("resume: " + resume);
    out.println("fullName: " + fullName);
    out.println("submissionDate: " + submissionDate.toString());
  }
}
