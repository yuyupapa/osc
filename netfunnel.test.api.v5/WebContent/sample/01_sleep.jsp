<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.concurrent.TimeUnit"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../js/netfunnel.js" charset="UTF-8"></script>
<script type="text/javascript">
function complete(){
    NetFunnel_Complete({action_id:'sleep'},function(ev, ret){    
        alert('경고창');    
    }); 
}
</script>
<title>WAS SLEEP</title>
</head>
<body>
<%
	/*
	개요 : 파라미터로 넘겨받은 시간만큼 WAS에서 sleep
	용도 : Thread Pool의 개수 확인 시 사용
	*/
 	long startTime = System.currentTimeMillis();
	long sleepMiliSec = Long.parseLong(request.getParameter("sleepMiliSec"));

	try {
		//TimeUnit.MILLISECONDS.sleep(1000);
		TimeUnit.MILLISECONDS.sleep(sleepMiliSec);
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	
	long executeMs = System.currentTimeMillis() - startTime;
%>
<%= "WAS 처리시간 : " + executeMs + "ms" %>
</body>
</html>