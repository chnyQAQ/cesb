$(document).ready(function() {
	
	$('#searchForm').formSearchPageRows({
		url		:	'/webservices/list-page',
		cm		:	[
						{col: function(endpoint) {
							return $('<a href="' + (contextPath + '/endpoint/'+ endpoint.id + '/logs?endpointPage=' + endpointPage + '&endpointType=webservice') + '">' + endpoint.name + '</a>');
						}},
						{col: 'code'},
						{col: 'component'},
						{col: 'path'},
						{col: 'wsdlURL'},
						{col: 'options'},
						{col: function(endpoint) {
							return $.buttonActions([
								{text: '编辑', click: edit},
								{text: '删除', click: remove}
							], endpoint);
						}}
			 	 	]
	}).trigger('submit');
	
	var refreshRows = function() {
		$('#searchForm').formSearchPageRows('search');
	};
	
	var add = function() {
		$('#dataForm').modalPost('/webservices', function() {
			$.alert('保存成功！', refreshRows);
		});
	};
	$('#addButton').on('click', add);
	
	var edit = function(endpoint) {
		$('#dataForm').modalPut('/webservices/' + endpoint.id, endpoint, function() {
			$.alert('保存成功！', refreshRows);
		});
	};
	
	var remove = function(endpoint) {
		$.confirmDelete('/webservices/' + endpoint.id, function() {
			$.alert('删除成功！', refreshRows);
		});
	};
	
});