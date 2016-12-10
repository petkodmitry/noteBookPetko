<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="error.jsp" %>
<html>
<head><title>Новая запись</title>
</head>
<body><H3>Форма для добавления новой записи:</H3>
<HR>
<FORM name="addEmailForm"
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
</FORM>
<BR><a href="main">На главную</a><BR>
<HR>
<c:if test="${requestScope['info'] != null}">
    ${info}<BR>
</c:if>
<c:if test="${requestScope['errorMessage'] != null}">
    Ошибка: ${errorMessage}
</c:if>
</body>
</html>
