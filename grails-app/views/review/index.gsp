<html>
<head>
	<title>Check IDM User State</title>
	<link href="${resource(dir: 'css', file: 'main.css')}" type="text/css" rel="stylesheet">
</head>
<body>

<g:form name="checkIDMUser" url="[controller:'review', action:'runAllChecks']">
<br/>
<label>Search in</label>
	<g:select name="searchIn" from="${['FHKennung', 'Matrikelnr.', 'S7-Kennung']}"/> 
<label>for</label>
	<g:textField name="searchFor"/>
	<%--<g:actionSubmit value="Search"/>
--%>
	<g:submitButton name="Check"/>
</g:form>
</body>
</html>