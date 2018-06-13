package web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class CorsFilter
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    /**
     * Default constructor. 
     */
    public CorsFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here

		// pass the request along the filter chain
		try {  
            HttpServletRequest httpRequest = (HttpServletRequest) request;  
            HttpServletResponse httpResponse = (HttpServletResponse) response;  
  
            // øÁ”Ú  
            String origin = httpRequest.getHeader("Origin");  
            if (origin == null) {  
                httpResponse.addHeader("Access-Control-Allow-Origin", "*");  
            } else {  
                httpResponse.addHeader("Access-Control-Allow-Origin", origin);  
            }  
            httpResponse.addHeader("Access-Control-Allow-Headers", "Origin, x-requested-with, Content-Type, Accept,X-Cookie");  
            httpResponse.addHeader("Access-Control-Allow-Credentials", "true");  
            httpResponse.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS,DELETE");  
            if ( httpRequest.getMethod().equals("OPTIONS") ) {  
                httpResponse.setStatus(HttpServletResponse.SC_OK);  
                return;  
            }  
            chain.doFilter(request, response);  
        } catch (Exception e) {  
            e.printStackTrace(); 
        }
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
