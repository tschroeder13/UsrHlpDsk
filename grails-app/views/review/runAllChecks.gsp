<html>
<head>
<title>Review ${ user }</title>
<link href="${resource(dir: 'css', file: 'main.css')}" type="text/css"
	rel="stylesheet">
</head>
<body>
	<%--
	<label>Search for ${user} in ${ attribute }</label>
	<g:each in="${ checks }" var="check">
		<li>${ check.title } ${ check.checkBody }</li>
	</g:each>
--%>
	<br>
	<br>
	<table style="text-align: left; width: 100%;" cellpadding="2"
		cellspacing="2">
		<tbody>
			<tr>
				<td>
					<h1>
						<label>Search for ${attr} = ${user}</label>
					</h1>
				</td>
			</tr>
			<tr align="center">
				<td style="vertical-align: top;">
					<h1><label>Result</label></h1>
				</td>
			</tr>
			<tr>
				<td style="vertical-align: top;">
					<table style="text-align: left; width: 100%;" cellpadding="2"
						cellspacing="2">
						<tbody>
							<g:each in="${ result }">
								<tr>
									<td style="vertical-align: top;"><g:if
											test="${ it[0] == -1}">
											<g:img dir="images" file="false.png" width="48" height="48" />
										</g:if> <g:elseif test="${ it[0] == 0}">
											<g:img dir="images" file="true.png" width="48" height="48" />
										</g:elseif></td>
									<td style="vertical-align: top;">
										${ it[1] }<br>
									</td>
								</tr>

							</g:each>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<g:link controller="home" action="index">Neue Suche</g:link>
				</td>
			</tr>
		</tbody>
	</table>



</body>
</html>