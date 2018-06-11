package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import fastchat.*;

/**
 * Servlet implementation class ModifyGroup
 */
@WebServlet("/ModifyGroup")
public class ModifyGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModifyGroup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("modify group information get method");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("modify group information post method");
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String data = request.getParameter("data");
		JSONObject jsonObject = JSONObject.parseObject(data);
		Handle handle = new Handle();
		try {
			int groupid = Integer.parseInt(jsonObject.getString("groupid"));
			String announcement = jsonObject.getString("announcement");
			String groupname = jsonObject.getString("groupname");
			if (handle.modifyGroup(groupid, announcement, groupname)) {
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
