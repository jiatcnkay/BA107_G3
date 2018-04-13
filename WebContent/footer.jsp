<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="BIG5"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="android.com.friends_list.model.*"%>
<%@ page import="android.com.member.model.*"%>
<%@ page import="android.com.talk.model.*"%>
<%
	MemberVO memSelf = (MemberVO) session.getAttribute("memSelf");
	FriendsService friSvc = new FriendsService();
	if (memSelf == null) {
		memSelf = null;
	} else {

		List<MemberVO> friends = friSvc.getMemFri(memSelf);
		session.setAttribute("friends", friends);
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>footer</title>

</head>
<body onload="connect();">
	<!-- chat -->
	<div id="index-chat" class="container">
		<div class="row index-chat-title">
			<div class="col-xs-12 col-sm-12">
				<p>Toast.Chat</p>
			</div>
		</div>
		<div class="row index-chat-person">
			<div class="col-xs-12 col-sm-12" style="padding-right: 0px">
				<p>好 友 名 單</p>
				<div class="pre-scrollable" style="height: 110px">

					<c:forEach var="friends" items="${friends}">
						<div id="${friends.mem_no}">
							<img src="<%=request.getContextPath()%>/front_end/res/img/footer/close.jpg"> 
							<a href="#" onclick="getTalk('${friends.mem_no}')">${friends.mem_name}</a>
						</div>
					</c:forEach>

				</div>
			</div>
		</div>
		<div class="row index-chat-message">
			<!-- 		聊天內容                         -->
			<div id="friName">&nbsp</div><!-- 目前聊天的好友 -->
			<div class="col-xs-12 col-sm-12 pre-scrollable" id="chat-message">
			
dsf
			</div>
			<div class="col-xs-12 col-sm-12" style="background-color: #FFF;">
				<input type="text" placeholder="type message..." id="inputMessage">
				<span class="fa fa-commenting-o"></span>
			</div>
		</div>
		<button id="chatSwitchOpen" type="button">
			<span class="fa fa-users fa-3x"></span>
		</button>
		<button id="chatSwitchClose" type="button">
			<span class="fa fa-arrow-circle-right fa-3x"></span>
		</button>
	</div>

	<script>
		
	
	</script>
	<!-- //chat -->


	<!-- FOOTER -->
	<div class="footer">
		<div class="col-xs-12 col-sm-4">
			<p>Contact us</p>
			<p>0800-080-978</p>
			<p>toast@gmail.com</p>
		</div>
		<div class="col-xs-12 col-sm-4">
			<p>Service</p>
			<p>關於我們</p>
			<p>常見問題</p>
		</div>
		<div class="col-xs-12 col-sm-4">
			<p>Follow us</p>
		</div>
		<div class="col-xs-12 col-sm-12">
			<p>Copyright Toast All Rights Reserved</p>
		</div>

		<div class="gotop ons" id="gototop">
			<a href="#"><img
				src="<%=request.getContextPath()%>/front_end/res/img/footer/upload.png"></a>
		</div>
	</div>
	<!-- FOOTER END-->
	<script>
	$(document).ready(function(){
		//滾輪置底
		var dh = $("div #chat-message").height();
		$("div #chat-message").scrollTop(dh);
	});
	
	var MyPoint = "/FriendWS/${memSelf.mem_no}";
	var host = window.location.host;
	var path = window.location.pathname;
	var webCtx = path.substring(0,path.indexOf('/',1));
	var endPointURL = "ws://"+window.location.host+webCtx+MyPoint;

	var webSocket;
	
	function connect(){
		webSocket = new WebSocket(endPointURL);
		
		webSocket.onopen = function(event){
		};
		
		webSocket.onmessage = function(event){
			
			var jsonObj = JSON.parse(event.data);
			
			/**********************上下線處理************************/
			if (jsonObj.type == 'sendEveryFri') { //接收好友上線通知
				var friId = jsonObj.memberNO;
				$("#"+friId+">img").attr("src","<%=request.getContextPath()%>/front_end/res/img/footer/online.jpg");
            } 
			if (jsonObj.type == 'leave') { //接收好友離線通知
				var friId = jsonObj.memberNO;
				$("#"+friId+">img").attr("src","<%=request.getContextPath()%>/front_end/res/img/footer/close.jpg");
            } 
			if (jsonObj.type == 'sendSelf') { //上線接收在線好友清單
				var onlineFri = jsonObj.onlineFri;
				onlineFri.forEach(function(fri){
					$("#"+fri+">img").attr("src","<%=request.getContextPath()%>/front_end/res/img/footer/online.jpg");
							});
				}
			/********************************************************/
			
			if (jsonObj.type == 'getMessage') { //接收與好友的歷史訊息
				var message = jsonObj.message;
				
			
			
				alert(message);
            } 

			};

			webSocket.onclose = function(event) {
			}

		}

		function sendMessage() {

		}
		
		function getTalk(friNo){
			var friName = $('#friName');//目前訊息框上的好友名稱
			var getName = $("#"+friNo+">a").text();//點擊好友名稱
			alert(getName);
			if(friName!=getName){
				friName.text(getName);
				$("#chat-message").empty();
				var jsonObj = {
					"type" : "getOneTalk",
					"friNo" : friNo,
				};
				webSocket.send(JSON.stringify(jsonObj));
				
				
			}else{
				return;
			}		
		}
	</script>


</body>
</html>