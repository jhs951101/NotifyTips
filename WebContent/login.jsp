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

function Submit(fname) {
	
	var username = document.forms[fname].elements['username'];
	var password = document.forms[fname].elements['password'];
	
	if (username.value == "") {
		alert('아이디를 입력해 주세요.');
		username.select();
		username.focus();
		return false;
	}
	else if (password.value == "") {
		alert('비밀번호를 입력해 주세요.');
		password.select();
		password.focus();
		return false;
	}
	else {
		return true;
	}
}

</script>

</head>
<body>
	<form name="login" action="/NotifyTips/LoginServlet" method="post" onsubmit="return Submit('login');">
	<br><br>
		<table border=0 align="center">
			<tr>
				<td colspan="2"><h2>로그인</h2></td>
			</tr>
			<tr>
				<td>아이디: </td>
				<td><input type="text" id="username" name="username" maxlength="20"></td>
			</tr>
			<tr>
				<td>비밀번호: </td>
				<td><input type="password" id="password" name="password" maxlength="20"></td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="submit" id="loginBtn" value="로그인"> <input type="reset" value="초기화"></td>
			</tr>
			<tr>
				<td colspan="2">
					<a href="join.jsp">회원가입</a>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>