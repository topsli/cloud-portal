<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%--@elvariable id="credentials" type="de.papke.cloud.portal.model.CredentialsModel"--%>

<jsp:include page="header.jsp" />

<div id="wrapper">

	<jsp:include page="navigation.jsp" />

	<div id="page-wrapper">
		<div class="row">
			<div class="col-lg-12">
			    <h4 class="page-header">Credentials Admin > Vsphere > List</h4>
				<form id="main-form" method="get" action="<c:url value="/credentials" />"
					role="form">
					<table
						class="table table-striped table-bordered table-hover dataTable no-footer dtr-inline"
						id="datatable" role="grid"
						aria-describedby="dataTables-example_info" style="width: 100%;"
						width="100%">
						<thead>
							<tr role="row">
								<th class="sorting_asc" tabindex="0"
									aria-controls="dataTables-example" rowspan="1" colspan="1"
									style="width: 280px;" aria-sort="ascending"
									aria-label="Rendering engine: activate to sort column descending">ID</th>
								<th class="sorting" tabindex="0"
									aria-controls="dataTables-example" rowspan="1" colspan="1"
									style="width: 333px;"
									aria-label="Browser: activate to sort column ascending">Group</th>
								<th class="sorting" tabindex="0"
									aria-controls="dataTables-example" rowspan="1" colspan="1"
									style="width: 244px;"
									aria-label="Engine version: activate to sort column ascending">vCenter Hostname</th>
								<th class="sorting" tabindex="0"
									aria-controls="dataTables-example" rowspan="1" colspan="1"
									style="width: 183px;"
									aria-label="CSS grade: activate to sort column ascending">vCenter Username</th>
								<th class="sorting" tabindex="0"
									aria-controls="dataTables-example" rowspan="1" colspan="1"
									style="width: 183px;"
									aria-label="CSS grade: activate to sort column ascending">vCenter Password</th>
								<th class="sorting" tabindex="0"
									aria-controls="dataTables-example" rowspan="1" colspan="1"
									style="width: 183px;"
									aria-label="CSS grade: activate to sort column ascending">Actions</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${credentials.credentialsList}" var="cred">
								<tr class="gradeA odd" role="row">
									<td class="sorting_1"><c:out value="${cred.id}" /></td>
									<td><c:out value="${cred.group}" /></td>
									<td><c:out value="${cred.secretMap['vCenterHostname']}" /></td>
									<td><c:out value="${cred.secretMap['vCenterUsername']}" /></td>
									<td><c:out value="${cred.secretMap['vCenterPassword']}" /></td>
									<td>
									   <button id="delete/action/vsphere/${cred.id}" type="submit" class="btn btn-danger btn-circle"><i class="fa fa-times"></i></button>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</form>
			</div>
		</div>
	</div>
</div>

<jsp:include page="footer.jsp" />