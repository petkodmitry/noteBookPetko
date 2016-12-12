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

<form method="post">
    <input id="sendTo" name="sendTo" title="sendTo" type="text" value="${email}" readonly/>
    <BR>
    <input id="subject" name="subject" title="subject" type="text" maxlength="30"/>
    <BR>
    <textarea id="body" name="body" title="body" maxlength="500" rows="15" cols="100"></textarea>
    <BR>
    <div style="color: navy">Внимание! При отправке в письмо будут автоматически вложены файлы: zip-архив с исходниками пректа и "PetkoCV.docx"!</div>
    <div style="font-style: oblique; color: navy">P.S. Файл "PetkoCV.docx" должен находится в корневой папке запущенного проекта. Иначе он отправлен не будет</div>
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
