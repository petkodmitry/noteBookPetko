<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="error.jsp" %>
<HTML>
<HEAD><TITLE>Список адресов e-mail</TITLE></HEAD>
<BODY><H3>Таблица: e-mail</H3>
<HR>
<a href="add">Добавить E-mail адрес</a><BR>
<c:if test="${addForm != null}">
    <jsp:include page="error.jsp"/>
</c:if>
<H4>Найдено записей: ${totalToShow} из ${total}</H4>

<c:choose>
    <c:when test="${requestScope.get('page') == null}">
        <c:set var="page" value="1"/>
        ${requestScope.putIfAbsent("page", "1")}
    </c:when>
    <c:otherwise>
        <c:set var="page" value="${requestScope.get('page')}"/>
    </c:otherwise>
</c:choose>

<%--<form method="post" action="controller?cmd=showUsers&page=${page}">--%>
<form method="post" action="main?page=${page}">
    <table>
        <tr>
            <td>количество элементов на странице:</td>
            <td>
                <select title="perPageSelection" name="perPage">
                    <option value="2" ${max == 2 ? 'selected="selected"' : ''}>2</option>
                    <option value="5" ${max == 5 ? 'selected="selected"' : ''}>5</option>
                    <option value="10" ${max == 10 ? 'selected="selected"' : ''}>10</option>
                    <option value="20" ${max == 20 ? 'selected="selected"' : ''}>20</option>
                    <option value="50" ${max == 50 ? 'selected="selected"' : ''}>50</option>
                    <option value="100" ${max == 100 ? 'selected="selected"' : ''}>100</option>
                </select>
            </td>
            <td>
                <INPUT type="submit" title="OK" value="OK">
            </td>
        </tr>
    </table>
</form>

<table border="1" frame="void">
    <tbody>
    <%--Sorting row of the table--%>
    <tr>
        <td><a href="main?page=1&sortBy=emailId&orderType=${"emailId".equals(sortBy) ?
                    ("asc".equals(orderType) ? "desc" : "asc") : "asc"}">ID</a>
            <c:if test="${'asc'.equals(orderType) && 'emailId'.equals(sortBy)}">↓</c:if>
            <c:if test="${'desc'.equals(orderType) && 'emailId'.equals(sortBy)}">↑</c:if>
        </td>
        <td><a href="main?page=1&sortBy=name&orderType=${"name".equals(sortBy) ?
                    ("asc".equals(orderType) ? "desc" : "asc") : "asc"}">Имя</a>
            <c:if test="${'asc'.equals(orderType) && 'name'.equals(sortBy)}">↓</c:if>
            <c:if test="${'desc'.equals(orderType) && 'name'.equals(sortBy)}">↑</c:if>
        </td>
        <td><a href="main?page=1&sortBy=email&orderType=${"email".equals(sortBy) ?
                    ("asc".equals(orderType) ? "desc" : "asc") : "asc"}">E-Mail</a>
            <c:if test="${'asc'.equals(orderType) && 'email'.equals(sortBy)}">↓</c:if>
            <c:if test="${'desc'.equals(orderType) && 'email'.equals(sortBy)}">↑</c:if>
        </td>
        <td>Удалить запись</td>
        <td>Написать письмо</td>
    </tr>
    <%--Filtering row of the table--%>
    <tr>
        <%--<td>
            <form style="height: 5px" method="POST" action="controller">
                <input style="width: 50px" type="text" title="установить фильтр" name="userIdFilter"
                       value="${filters.get("userId")}"/>
                <input type="submit" formaction="controller?cmd=showUsers&page=1" hidden="hidden"/>
                <a href="controller?cmd=showUsers&page=${filters.get("userId") == null ? page : 1}&filterRemove=userId">X</a>
            </form>
        </td>--%>
        <td>
            <%--<form style="height: 5px" method="POST" action="main?filterSet=emailId">--%>
            <form style="height: 5px" method="POST" action="">
                <input style="width: 50px" type="text" title="установить фильтр" name="filterText"
                       value="${filters.get("emailId")}"/>
                <input type="submit" formaction="main?page=1&filterSet=emailId" hidden="hidden"/>
                <a href="main?page=${filters.get("emailId") == null ? page : 1}&filterRemove=emailId">X</a>
            </form>
        </td>
        <td>
            <form style="height: 5px" method="POST" action="">
                <input style="width: 50px" type="text" title="установить фильтр" name="filterText"
                       value="${filters.get("name")}"/>
                <input type="submit" formaction="main?page=1&filterSet=name" hidden="hidden"/>
                <a href="main?page=${filters.get("name") == null ? page : 1}&filterRemove=name">X</a>
            </form>
        </td>
        <td>
            <form style="height: 5px" method="POST" action="">
                <input style="width: 50px" type="text" title="установить фильтр" name="filterText"
                       value="${filters.get("email")}"/>
                <input type="submit" formaction="main?page=1&filterSet=email" hidden="hidden"/>
                <a href="main?page=${filters.get("email") == null ? page : 1}&filterRemove=email">X</a>
            </form>
        </td>
        <td></td>
        <td></td>
    </tr>
    <c:forEach items="${emailsList}" var="email">
        <tr>
            <td><c:out value="${email.getEmailId()}"/></td>
            <td><c:out value="${email.getName()}"/></td>
            <td><c:out value="${email.getEmail()}"/></td>
            <td><a href="javascript:void(0)"
                   onclick="confirm('Удалить?') ? window.location = 'delete?id='+${email.getEmailId()} : ''">Удалить</a>
            </td>
            <td><a href="javascript:void(0)"
                   onclick="confirm('Написать письмо?') ? window.location = 'send?id='+${email.getEmailId()} : ''">Написать</a>
                   <%--onclick="confirm('Отправить?') ? openDialog() : ''">Отправить</a>--%>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<script>
    function openDialog() {
        document.getElementById("file1").click();
        window.location = 'send?id='+${email.getEmailId()};
    }
</script>

<BR>

<c:set var="aMod" value="${totalToShow mod max}"/>
<c:choose>
    <c:when test="${aMod == 0}">
        <c:set var="endPage" value="${(totalToShow / max)}"/>
    </c:when>
    <c:otherwise>
        <c:set var="endPage" value="${((totalToShow - aMod) / max) + 1}"/>
    </c:otherwise>
</c:choose>

<c:if test="${endPage != 1}">
    <c:forEach begin="${1}" end="${endPage}" var="page1">
        <c:choose>
            <c:when test="${(endPage <= 10) || ((page1 == 1) || (page1 == endPage)
            || ((page + 3) > page1 && (page - 3) < page1))}">
                <c:if test="${page1 < 10}">
                    <c:set var="page2" value="${'0'.concat(page1)}"/>
                </c:if>
                <c:if test="${page1 > 9}">
                    <c:set var="page2" value="${page1}"/>
                </c:if>
                <c:choose>
                    <c:when test="${page == page1}">
                        &nbsp;&nbsp;&nbsp;&nbsp;${page2}
                    </c:when>
                    <c:otherwise>
                        &nbsp;&nbsp;&nbsp;<a href="main?page=${page1}"> ${page2} </a>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${(page1 == 2 && (page - 3) > 1) || ((page1 == endPage - 1) && (page + 3) < endPage)}">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...
                    </c:when>
                    <c:otherwise>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</c:if>

<HR>
<BR><a href="main">На главную</a><BR>
<c:if test="${requestScope['info'] != null}">
    <BR>${info}<BR>
</c:if>
<c:if test="${requestScope['errorMessage'] != null}">
    <BR>Ошибка: ${errorMessage}
</c:if>
</BODY>
</HTML>
