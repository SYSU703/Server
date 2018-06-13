package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import fastchat.Handle;
import models.CompleteUserInfo;
import models.SimpleUserInfo;

/**
 * Servlet implementation class GetUserInfo
 */
@WebServlet("/GetCompleteUserInfo")
public class GetCompleteUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCompleteUserInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get user information get method");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("get user information post method");
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
		String result = new String();
		try {
			String username = jsonObject.getString("username");
			CompleteUserInfo userInfoList = handle.getCompleteUserInfo(username);
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2 = (JSONObject) JSONObject.toJSON(userInfoList);
			result = JSONObject.toJSONString(jsonObject2);
		} catch (Exception e) {
		} finally {
			out.write(result);
			out.flush();  
	        out.close();
		}
	}

}
