package bgn.source.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import bgn.source.notification.config.SwaggerSorterFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

class SwaggerSorterFilterTest {

	private final SwaggerSorterFilter filter = new SwaggerSorterFilter();

	@Test
	void doFilter_nonSwaggerRequest_passesThrough() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/users");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		verify(chain).doFilter(request, response);
	}

	@Test
	void doFilter_swaggerInitializerRequest_injectsOperationsSorter() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/swagger-initializer.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		doAnswer(inv -> {
			ContentCachingResponseWrapper wrapper = inv.getArgument(1);
			wrapper.getWriter().write("SwaggerUIBundle({url:\"/api-docs\"})");
			wrapper.getWriter().flush();
			return null;
		}).when(chain).doFilter(any(), any());

		filter.doFilter(request, response, chain);

		assertThat(response.getContentAsString()).contains("operationsSorter:");
	}

}
