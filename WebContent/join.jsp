<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Notify Tips</title>

<link rel="stylesheet" type="text/css" href="resources/bodyStyle.css">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script>

var usernameChecked = false;

function haveBlank(str) {
	
	for(i=0; i<str.length; i++)
		if(str.charAt(i) == ' ')
			return true;
	
	return false;
}

function checkEmail(str) {
	
	for(i=0; i<str.length; i++)
		if(str.charAt(i) == '@')
			return true;
	
	return false;
}

function checkUsername(str) {
	
	for(i=0; i<str.length; i++)
		if( !(str.charAt(i) >= '0' && str.charAt(i) <= '9')
				&& !(str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')
				&& !(str.charAt(i) >= 'a' && str.charAt(i) <= 'z') )
			return false;
	
	return true;
}

function checkBirthday(str) {
	
	for(i=0; i<str.length; i++)
		if( !(str.charAt(i) >= '0' && str.charAt(i) <= '9') )
			return false;
	
	return true;
}

function checkNickname(str) {
	
	for(i=0; i<str.length; i++)
		if( (str.charAt(i) >= '!' && str.charAt(i) <= '@')
				|| (str.charAt(i) >= '[' && str.charAt(i) <= '.')
				|| (str.charAt(i) >= '{' && str.charAt(i) <= '~')
				|| (str.charAt(i) == ' ') )
			return false;
	
	return true;
}

function Submit(fname) {
	
	var username = document.forms[fname].elements['username'];
	var password1 = document.forms[fname].elements['password1'];
	var password2 = document.forms[fname].elements['password2'];
	var nickname = document.forms[fname].elements['nickname'];
	
	var email1 = document.forms[fname].elements['email1'];
	var email2 = document.forms[fname].elements['email2'];
	
	var keywords = document.forms[fname].elements['keywords'];
	
	if (username.value == ""){
		alert('아이디를 입력해 주세요.');
		username.select();
		username.focus();
		return false;
	}
	else if (password1.value == ""){
		alert('비밀번호를 입력해 주세요.');
		password1.select();
		password1.focus();
		return false;
	}
	else if (nickname.value == ""){
		alert('닉네임을 입력해 주세요.');
		nickname.select();
		nickname.focus();
		return false;
	}
	else if (email1.value == "" || email2.value == ""){
		alert('이메일을 입력해 주세요.');
		email1.select();
		email1.focus();
		return false;
	}
	else if (keywords.value == ""){
		alert('선호하는 키워드를 입력해 주세요.');
		keywords.select();
		keywords.focus();
		return false;
	}
	else if (password1.value != password2.value){
		alert('비밀번호가 일치하지 않습니다.');
		password2.select();
		password2.focus();
		return false;
	}
	else if ( haveBlank(password1.value) || haveBlank(email1.value) || haveBlank(email2.value) ){
		alert('비밀번호랑 이메일에는 띄어쓰기가 들어가면 안됩니다.');
		return false;
	}
	else if (checkUsername(username.value) == false) {
		alert('아이디에는 숫자, 영문자 이외에는 들어갈 수 없습니다.');
		username.select();
		username.focus();
		return false;
	}
	else if (checkNickname(nickname.value) == false) {
		alert('닉네임에는 특수문자랑 띄어쓰기가 들어가면 안됩니다.');
		nickname.select();
		nickname.focus();
		return false;
	}
	else if (usernameChecked == false) {
		alert('아이디 중복확인을 해주세요.');
		username.select();
		username.focus();
		return false;
	}
	else {
		alert('정상적으로 가입 되었습니다!');
		username.disabled = false;
		return true;
	}

}

$(document).ready(function(){
	$("#checkUsernameBtn").click(function(){
		
		if($("#username").val() == ""){
			alert('아이디를 입력해 주세요');
			$("#username").select();
			$("#username").focus();
		}
		else {
	    	$.get("/NotifyTips/DuplicateCheckServlet", {username:$("#username").val()}, function(isduplicate){
	    		if(isduplicate == 'error'){
					alert('Error: 서버 상의 문제로 아이디 중복확인이 진행되지 못하였습니다');
				}
	    		else if(isduplicate == 'true'){
					alert('중복되는 아이디입니다');
					$("#username").select();
					$("#username").focus();
				}
				else {
					alert('사용 가능한 아이디입니다');
					$("#username").prop('disabled', true);
					$("#checkUsernameBtn").prop('disabled', true);
					$("#modifyUsername").prop('disabled', false);
					usernameChecked = true;
				}
			});
		}
	});
	
	$("#modifyUsername").click(function(){
		
		$("#username").prop('disabled', false);
		$("#checkUsernameBtn").prop('disabled', false);
		$("#modifyUsername").prop('disabled', true);
		usernameChecked = false;
	});
});

</script>

</head>
<body>
	<h2>회원가입</h2><br>
	
	<form name="join" action="/NotifyTips/JoinServlet" method="post" onsubmit="return Submit('join');">
		<table border=0 align="center">
			<tr>
				<td>아이디: </td>
				<td>
					<input type="text" id="username" name="username" maxlength="15">
					<input type="button" id="checkUsernameBtn" value="중복확인">
					<input type="button" id="modifyUsername" value="수정하기" disabled>
				</td>
			</tr>
			<tr>
				<td>비밀번호: </td>
				<td><input type="password" id="password1" name="password1" maxlength="20"></td>
			</tr>
			<tr>
				<td>비밀번호 확인: </td>
				<td><input type="password" id="password2" name="password2" maxlength="20"></td>
			</tr>
			<tr>
				<td>닉네임: </td>
				<td>
					<input type="text" id="nickname" name="nickname" maxlength="15">
				</td>
			</tr>
			<tr>
				<td>이메일: </td>
				<td>
					<input type="text" id="email1" name="email1" maxlength="20"> @ 
					<input type="text" id="email2" name="email2" maxlength="40">
				</td>
			</tr>
			<tr>
				<td>선호하는 키워드: </td>
				<td>
					<input type="text" id="keywords" name="keywords" style="width: 500px; ">
				</td>
			</tr>
			<tr>
				<td>알림 주기: </td>
				<td>
					<select name="period" id="period">
					    <option value="" selected="selected">선택하세요</option>
					    <option value="86400000">1일</option>
					    <option value="172800000">2일</option>
					    <option value="6048000000">1주일</option>
					    <option value="60000">1분</option>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="submit" value="가입하기"> <input type="reset" value="초기화"></td>
			</tr>
		</table>
	</form>
</body>
</html>