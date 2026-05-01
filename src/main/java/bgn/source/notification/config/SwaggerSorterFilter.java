package bgn.source.notification.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SwaggerSorterFilter implements Filter {

	private static final String SORTER = "function(a,b){" + "var order={get:0,post:1,put:2,patch:3,delete:4};"
			+ "var am=a.get?a.get('method'):a.method;" + "var bm=b.get?b.get('method'):b.method;"
			+ "var ap=a.get?a.get('path'):a.path;" + "var bp=b.get?b.get('path'):b.path;"
			+ "var diff=(order[am]!==undefined?order[am]:99)-(order[bm]!==undefined?order[bm]:99);"
			+ "return diff!==0?diff:ap.localeCompare(bp);" + "}";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (!httpRequest.getRequestURI().endsWith("swagger-initializer.js")) {
			chain.doFilter(request, response);
			return;
		}

		ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, wrapper);

		String original = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
		String modified = original.replace("SwaggerUIBundle({", "SwaggerUIBundle({operationsSorter:" + SORTER + ",");

		byte[] bytes = modified.getBytes(StandardCharsets.UTF_8);
		((HttpServletResponse) response).setContentLength(bytes.length);
		((HttpServletResponse) response).getOutputStream().write(bytes);
	}

}
