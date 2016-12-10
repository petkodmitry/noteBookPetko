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
<%--<FORM name="addEmailForm"
      method="POST"
      action="">
    <table border="0">
        <tr>
            <td align="right">Имя контакта</td>
            <td><INPUT type="text"
                       name="name"
                       title="Введите имя"
                       value="${regData.getName()}"></td>
        </tr>
        <tr>
            <td align="right">E-mail контакта</td>
            <td><INPUT type="text"
                       name="email"
                       title="Введите E-mail"
                       value="${regData.getEmail()}"></td>
        </tr>
        <tr style="height: 25px">
            <td>
            </td>
            <td align="right"><INPUT type="submit" formaction="addEmail" title="Добавить E-mail" value="Добавить"></td>
        </tr>
    </table>
    <br>
</FORM>--%>
<form method="post" enctype="multipart/form-data">
    <%--<fieldset>--%>
    <input id="sendTo" name="sendTo" title="sendTo" type="text" value="${email}" readonly/>
    <BR>
    <input id="theme" name="theme" title="theme" type="text" maxlength="30"/>
    <BR>
    <textarea id="body" name="body" title="body" maxlength="500" rows="15" cols="100"></textarea>
    <%--<input title="body" type="text" maxlength="100" style="height: 20%; width: 50%"/>--%>
    <%--</fieldset>--%>
    <BR>
    <%--<input type="file" id="upload" name="upload" title="upload" style="visibility: hidden; width: 1px; height: 1px" multiple/>--%>
    <%--<a href="" onclick="document.getElementById('upload').click(); return false">Add file(s)</a>--%>
    <input type="file" id="upload" name="upload" title="upload" multiple>
    <BR><BR>
    <input type="submit" formaction="sendMessage" title="Отправить" value="Отправить">
</form>
<%--<form method="post" enctype="multipart/form-data">--%>
<%--<input type="file" id="upload" name="upload" title="upload" multiple>--%>
<%--</form>--%>

<%--<a href="sendMessage">Отправить</a>--%>

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
