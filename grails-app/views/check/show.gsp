
<%@ page import="de.fhdortmund.UsrHlpDsk.checkUser.Check" %>
<!doctype html>
<html>
	<head>
		<syntax:resources name="code" languages="['Groovy', 'Java']" controls="false" />
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'check.label', default: 'Check')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-check" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-check" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list check">
			
				<g:if test="${checkInstance?.title}">
				<li class="fieldcontain">
					<span id="title-label" class="property-label"><g:message code="check.title.label" default="Title" /></span>
					
						<span class="property-value" aria-labelledby="title-label"><g:fieldValue bean="${checkInstance}" field="title"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${checkInstance?.checkBody}">
				<li class="fieldcontain">
					<span id="checkBody-label" class="property-label"><g:message code="check.checkBody.label" default="Check Body" /></span>
						<%-- 
						<span class="property-value" aria-labelledby="checkBody-label"><g:fieldValue bean="${checkInstance}" field="checkBody"/></span>
						--%>
						<span class="property-value" aria-labelledby="checkBody-label">
						<syntax:format name="code" language="groovy" gutter="false">						
${fieldValue(bean: checkInstance, field: 'checkBody')}
						</syntax:format>
						</span>
				</li>
				</g:if>
			
				<g:if test="${checkInstance?.instruction}">
				<li class="fieldcontain">
					<span id="instruction-label" class="property-label"><g:message code="check.instruction.label" default="Instruction" /></span>
					
						<span class="property-value" aria-labelledby="instruction-label"><g:fieldValue bean="${checkInstance}" field="instruction"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${checkInstance?.weight}">
				<li class="fieldcontain">
					<span id="weight-label" class="property-label"><g:message code="check.weight.label" default="Weight" /></span>
					
						<span class="property-value" aria-labelledby="weight-label"><g:fieldValue bean="${checkInstance}" field="weight"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${checkInstance?.queries}">
				<li class="fieldcontain">
					<span id="queries-label" class="property-label"><g:message code="check.queries.label" default="Queries" /></span>
					
						<g:each in="${checkInstance.queries}" var="q">
						<span class="property-value" aria-labelledby="queries-label"><g:link controller="query" action="show" id="${q.id}">${q?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${checkInstance?.id}" />
					<g:link class="edit" action="edit" id="${checkInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
