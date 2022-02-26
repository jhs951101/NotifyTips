<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Contents Recommender</title>

<link rel="stylesheet" type="text/css" href="resources/bodyStyle.css">

<script>

function Submit(fname) {
	
	var title = document.forms[fname].elements['title'];
	var keyword = document.forms[fname].elements['keyword'];
	var explain = document.forms[fname].elements['explain'];
	
	if(title.value == ""){
		alert('Tip 제목을 입력해주세요.');
		return false;
	}
	else if(keyword.value == ""){
		alert('키워드를 입력해주세요.');
		return false;
	}
	else if(explain.value == ""){
		alert('설명을 입력해주세요.');
		return false;
	}
	else if(explaination.value.length > 65535){
		alert('설명은 최대 65,535자까지 가능합니다.');
		return false;
	}
	else{
		alert('정상적으로 등록되었습니다.\n등록해주셔서 감사합니다!');
		return true;
	}
}

</script>

</head>
<body>

	<h2>팁 추가하기</h2><br><br>
	
	<form name="requestContents" action="/NotifyTips/RegisterTipServlet" method="post" onsubmit="return Submit('requestContents');">
		<table border=0 align="center">
			<tr>
				<td>Tip 제목: </td>
				<td>
					<input type="text" id="title" name="title" maxlength="50" style="width: 511px; ">
				</td>
			</tr>
			<tr>
				<td>키워드: </td>
				<td>
					<input type="text" id="keyword" name="keyword" maxlength="30" style="width: 511px; ">
				</td>
			</tr>
			<tr>
				<td>설명: </td>
				<td>
					<textarea id="explain" name="explain" rows="6" cols="60"></textarea>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="submit" value="추가하기"> <input type="reset" value="재입력"></td>
			</tr>
		</table>
	</form>
</body>
</html>