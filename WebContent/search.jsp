<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Contents Recommender</title>

<link rel="stylesheet" type="text/css" href="resources/bodyStyle.css">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script>

function Submit(fname) {
	
	var keyword = document.forms[fname].elements['inputSearch'];
	
	if (keyword.value == "") {
		alert('검색어를 입력해 주세요.');
		keyword.select();
		keyword.focus();
		return false;
	}
	else {
		return true;
	}
}

</script>

</head>
<body>
	<p align="right"><a href="/NotifyTips/AccessPageServlet?page=registerTip">Tip 등록하기</a><br><br>
	</p>
	
	<br><br>

	<form name="search" action="/NotifyTips/SearchServlet?pageNumber=1" method="post" onsubmit="return Submit('search');">
		<table border=0 align="center">
			<tr>
				<td>
					당신이 좋아할 만한 Tip을 검색해보세요! <br>
				</td>
			</tr>
			<tr>
				<td><input type="text" id="inputSearch" name="inputSearch" maxlength="50" style="width: 500px; "></td>
				<td><input type="submit" id="btnSearch" name="btnSearch" value = " 검색하기 "></td>
			</tr>
		</table>
	</form>
</body>
</html>