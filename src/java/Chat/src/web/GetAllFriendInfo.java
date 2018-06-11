package web;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.*;

import fastchat.*;
import models.*;

/**
 * Servlet implementation class GetAllFriendInfo
 */
@WebServlet("/GetAllFriendInfo")
public class GetAllFriendInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllFriendInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get all friend information get method");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get all friend information post method");
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String data = request.getParameter("data");
		JSONObject jsonObject = JSONObject.parseObject(data);
		Handle handle = new Handle();
		String result = new String();
		try {
			String username = jsonObject.getString("username");
			List<SimpleUserInfo> friendList = handle.getAllFriendInfo(username);
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("friendcount", String.valueOf(friendList.size()));
			jsonObject2.put("friendlist", friendList);
			result = JSONObject.toJSONString(jsonObject2);
		} catch (Exception e) {
		} finally {
			out.write(result);
			out.flush();  
	        out.close();
		}
	}

}
