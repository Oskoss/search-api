package com.example.appengine.search;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.when;

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

public class SchemaServletTest {

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  @Mock
  private HttpServletRequest mockRequest;
  @Mock
  private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private SchemaServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    helper.setUp();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    servletUnderTest = new SchemaServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_successfulyInvoked() throws Exception {
    servletUnderTest.doGet(mockRequest, mockResponse);
    String content = responseWriter.toString();
    assertWithMessage("SchemaServlet response").that(content).contains("schemaIndex:maker:TEXT");
    assertWithMessage("SchemaServlet response").that(content).contains("schemaIndex:price:NUMBER");
    assertWithMessage("SchemaServlet response").that(content).contains("schemaIndex:color:TEXT");
    assertWithMessage("SchemaServlet response").that(content).contains("schemaIndex:model:TEXT");
  }
}
