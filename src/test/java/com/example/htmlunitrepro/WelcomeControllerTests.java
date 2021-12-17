package com.example.htmlunitrepro;

import java.util.Arrays;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(WelcomeController.class)
public class WelcomeControllerTests {

	@Autowired
	private WebClient webClient;

	@Autowired
	private MockMvc mockMvc;

	/**
	 * This test is using MockMvc.
	 * The request body is parsed in {@link MockHttpServletRequestBuilder} using a {@link FormHttpMessageConverter}
	 * in order to add form params as Servlet request params.
	 */
	@Test
	void mockMvcPost() throws Exception {
		final String content = EntityUtils
				.toString(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("subject", "subject test"),
						new BasicNameValuePair("message", "message test"))));

		mockMvc.perform(MockMvcRequestBuilders.post("/submit").content(content)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(MockMvcResultMatchers.content().string("subject test message test"));
	}

	/**
	 * This test is using HtmlUnit with an HTML form.
	 * The {@link WebRequest} has an encoding type of "application/x-www-form-urlencoded" and its
	 * {@code webRequest.requestParameters} has 3 values: {@code "subject=subject test", "message=message test", "submit="}.
	 * The {@code webRequest.requesBody}  is {@code null}.
	 */
	@Test
	void formPost() throws Exception {
		HtmlPage page = webClient.getPage("/form");
		HtmlForm form = page.getFormByName("test");

		HtmlTextInput subject = form.getInputByName("subject");
		subject.type("subject test");
		HtmlTextInput message = form.getInputByName("message");
		message.type("message test");
		HtmlButton button = form.getButtonByName("submit");
		Page result = button.click();
		assertThat(result.getWebResponse().getContentAsString()).isEqualTo("subject test message test");
	}

	/**
	 * This test is using HtmlUnit with an JQuery Ajax POST.
	 * The {@link WebRequest} has an encoding type of "application/x-www-form-urlencoded" and its
	 * {@code webRequest.requestParameters} is empty.
	 * The {@code webRequest.requesBody}  is {@code subject=subject+test&message=message+test}.
	 * This is inconsistent with the other test cases where form inputs are parsed as request parameters by HtmlUnit.
	 */
	@Test
	void ajaxPost() throws Exception {
		HtmlPage page = webClient.getPage("/ajax");
		HtmlButton button = page.getFirstByXPath("//button[contains(text(), 'Test post')]");
		button.click();
		webClient.waitForBackgroundJavaScript(10000);
		HtmlElement span = page.getHtmlElementById("result");
		assertNotNull(span);
		assertEquals("subject test message test", span.asNormalizedText());
	}


	/**
	 * This test is using HtmlUnit with an JQuery Ajax POST with a multipart FormData request.
	 * The {@link WebRequest} has an encoding type of "multipart/form-data" and its
	 * {@code webRequest.requestParameters} has 2 values:  {@code "subject=subject test", "message=message test"}.
	 * The {@code webRequest.requesBody}  is {@code null}.
	 * This form is parsed by HtmlUnit and makes the input available as request parameters.
	 * @see <a href="https://github.com/HtmlUnit/htmlunit/blob/5f04c8377fab271a2cbe4bc98569b19c2059097d/src/main/java/com/gargoylesoftware/htmlunit/javascript/host/xml/XMLHttpRequest.java#L639-L641">XMLHttpRequest</a>
	 */
	@Test
	void ajaxformDataPost() throws Exception {
		HtmlPage page = webClient.getPage("/ajaxFormData");
		HtmlButton button = page.getFirstByXPath("//button[contains(text(), 'Test post')]");
		button.click();
		webClient.waitForBackgroundJavaScript(10000);
		HtmlElement span = page.getHtmlElementById("result");
		assertNotNull(span);
		assertEquals("subject test message test", span.asNormalizedText());
	}

}
