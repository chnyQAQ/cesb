$(document).ready(function() {
	
	$('#searchForm').formSearchPageRows({
		url		:	'/files/list-page',
		cm		:	[
						{col: function(endpoint) {
							return $('<a href="' + (contextPath + '/endpoint/'+ endpoint.id + '/logs?endpointPage=' + endpointPage + '&endpointType=file') + '">' + endpoint.name + '</a>');
						}},
						{col: 'code'},
						{col: 'component'},
						{col: 'path'},
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
		$('#dataForm').modalPost('/files', function() {
			$.alert('保存成功！', refreshRows);
		});
	};
	$('#addButton').on('click', add);
	
	var edit = function(endpoint) {
		$('#dataForm').modalPut('/files/' + endpoint.id, endpoint, function() {
			$.alert('保存成功！', refreshRows);
		});
	};
	
	var remove = function(endpoint) {
		$.confirmDelete('/files/' + endpoint.id, function() {
			$.alert('删除成功！', refreshRows);
		});
	};
	
});