package com.example.appengine.search;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.when;

import com.google.appengine.api.search.Document;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DocumentServletTest {

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DocumentServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    helper.setUp();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    servletUnderTest = new DocumentServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_successfulyInvoked() throws Exception {
    servletUnderTest.doGet(mockRequest, mockResponse);
    String content = responseWriter.toString();
    assertWithMessage("DocumentServlet response: coverLetter")
        .that(content)
        .contains("coverLetter: CoverLetter");
    assertWithMessage("DocumentServlet response: resume")
        .that(content)
        .contains("resume: <html></html>");
    assertWithMessage("DocumentServlet response: fullName")
        .that(content)
        .contains("fullName: Foo Bar");
    assertWithMessage("DocumentServlet response: submissionDate")
        .that(content)
        .contains("submissionDate: ");
  }

  @Test
  public void createDocument_withSignedInUser() throws Exception {
    String email = "tmatsuo@example.com";
    String authDomain = "example.com";
    helper.setEnvEmail(email);
    helper.setEnvAuthDomain(authDomain);
    helper.setEnvIsLoggedIn(true);
    Document doc = servletUnderTest.createDocument();
    assertWithMessage("content")
        .that(doc.getOnlyField("content").getText())
        .contains("the rain in spain");
    assertWithMessage("email").that(doc.getOnlyField("email").getText()).isEqualTo(email);
  }

  @Test
  public void createDocument_withoutSignedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);
    Document doc = servletUnderTest.createDocument();
    assertWithMessage("content")
        .that(doc.getOnlyField("content").getText())
        .contains("the rain in spain");
    assertWithMessage("email").that(doc.getOnlyField("email").getText()).isEmpty();
  }
}
