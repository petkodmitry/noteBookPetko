<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="error.jsp" %>
<html>
<head><title>Отправить сообщение</title>
</head>
<body><H3>Форма для отправки сообщения:</H3>
<HR>

<form method="post" enctype="multipart/form-data">
    <input id="sendTo" name="sendTo" title="sendTo" type="text" value="${email}" readonly/>
    <BR>
    <input id="subject" name="subject" title="subject" type="text" maxlength="30"/>
    <BR>
    <textarea id="body" name="body" title="body" maxlength="500" rows="15" cols="100"></textarea>
    <BR>
    <input type="file" id="upload" name="upload" title="upload" multiple>
    <BR><BR>
    <input type="submit" formaction="sendMessage" title="Отправить" value="Отправить">
</form>

<BR><a href="main">Назад</a><BR>
<HR>
<c:if test="${requestScope['info'] != null}">
    ${info}<BR>
</c:if>
<c:if test="${requestScope['errorMessage'] != null}">
    Ошибка: ${errorMessage}
</c:if>
</body>
</html>
