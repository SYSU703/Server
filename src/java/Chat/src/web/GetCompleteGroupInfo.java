package web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import fastchat.Handle;
import models.CompleteGroupInfo;
import models.SimpleGroupInfo;

/**
 * Servlet implementation class GetCompleteGroupInfo
 */
@WebServlet("/GetCompleteGroupInfo")
public class GetCompleteGroupInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCompleteGroupInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get complete group information doget");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get complete group information post method");
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
			CompleteGroupInfo groupInfoList = handle.getCompleteGroupInfo(groupid);
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2 = (JSONObject) JSONObject.toJSON(groupInfoList);
			result = JSONObject.toJSONString(jsonObject2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.write(result);
			out.flush();  
	        out.close();
		}
	}

}
