package web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fastchat.*;
import models.CompleteUserInfo;
import models.SimpleGroupInfo;

import com.alibaba.fastjson.JSONObject;

/**
 * Servlet implementation class SearchGroup
 */
@WebServlet("/GetSimpleGroupInfo")
public class GetSimpleGroupInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSimpleGroupInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get simple group information doget");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get simple group information post method");
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String data = request.getParameter("data");
		JSONObject jsonObject = JSONObject.parseObject(data);
		Handle handle = new Handle();
		String result = new String();
		try {
			int groupid = Integer.valueOf(jsonObject.getString("groupid"));
			SimpleGroupInfo groupInfoList = handle.getSimpleGroupInfo(groupid);
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2 = (JSONObject) JSONObject.toJSON(groupInfoList);
			result = JSONObject.toJSONString(jsonObject2);
		} catch (Exception e) {
		} finally {
			out.write(result);
			out.flush();  
	        out.close();
		}
	}

}
