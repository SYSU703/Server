package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import fastchat.Handle;

/**
 * Servlet implementation class QuitGroup
 */
@WebServlet("/QuitGroup")
public class QuitGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QuitGroup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("quit group get method");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("quit group post method");
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		BufferedReader br = request.getReader();
		String str, wholeStr = "";
		while((str = br.readLine()) != null){
			wholeStr += str;
		}
		String data = wholeStr;
		JSONObject jsonObject = JSONObject.parseObject(data);
		Handle handle = new Handle();
		try {
			String username = jsonObject.getString("username");
			int groupid = Integer.valueOf(jsonObject.getString("groupid"));
			 if (handle.quitGroup(username, groupid)) {		
				String result = "{\"result\":\"success\"}";
				out.write(result);
			} else {
				String result = "{\"result\":\"fail\"}";
				out.write(result);
			}
		} catch (Exception e) {
			String result = "{\"result\":\"fail\"}";
			out.write(result);
		} finally {
			out.flush();  
	        out.close();
		}
	}

}
