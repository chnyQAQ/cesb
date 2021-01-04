$(document).ready(function() {

	$('#searchForm').formSearchPageRows({
		url		:	'/routes/list-page',
		cm		:	[
						{col: function(route) { return $('<a href="' + (contextPath + '/routes/' + route.id + '/logs') + '">' + route.name + '</a>') }},
						{col: 'code'},
						{col: function(route){
							return $('<center>' + (route.loaded == false ? '已停止' : '运行中') + '</center>');
						}},
						{col: function(route){
							return $('<center>' + (route.enabled == false ? '禁用' : '启用') + '</center>');
						}},
						{col: function(route) {
							var $toolbar = $('<div class="btn-toolbar d-inline-flex float-right"></div>');
							var $group = $('<div class="btn-group btn-group-sm"></div>');
							if (route.loaded) {
								var $buttonLoad = $('<button type="button" class="btn btn-xs btn-secondary" disabled>启动</button>');
								$buttonLoad.on('click', function() {
									loadRoute(route);
								});
								var $buttonUnload = $('<button type="button" class="btn btn-xs btn-secondary mr-1" id="routeUnload'+ route.code +'">停止</button>');
								$buttonUnload.on('click', function() {
									unloadRoute(route);
								});
								var $buttonDesign = $('<a target="_blank" title="请停止路由后操作！" class="btn btn-xs btn-secondary mr-1 disabled" href="'+ (contextPath + '/routes/' + route.id) +'">设计</a>');
								$group.append($buttonLoad);
								$group.append($buttonUnload);
								$group.append($buttonDesign);
							} else {
								var $buttonLoad = $('<button type="button" class="btn btn-xs btn-secondary">启动</button>');
								$buttonLoad.on('click', function() {
									loadRoute(route);
								});
								var $buttonUnload = $('<button type="button" class="btn btn-xs btn-secondary mr-1" disabled>停止</button>');
								$buttonUnload.on('click', function() {
									unloadRoute(route);
								});
								var $buttonDesign = $('<a target="_blank" class="btn btn-xs btn-secondary mr-1" href="'+ (contextPath + '/routes/' + route.id) +'">设计</a>');
								$group.append($buttonLoad);
								$group.append($buttonUnload);
								$group.append($buttonDesign);
							}

							var $buttonEdit = $('<button type="button" class="btn btn-xs btn-secondary">编辑</button>');
							$buttonEdit.on('click', function() {
								edit(route);
							});
							var $buttonRemove = $('<button type="button" class="btn btn-xs btn-secondary">删除</button>');
							$buttonRemove.on('click', function() {
								remove(route);
							});
							$group.append($buttonEdit);
							$group.append($buttonRemove);
							return $toolbar.append($group);
						}}
			 	 	]
	}).trigger('submit');
	
	var refreshRows = function() {
		$('#searchForm').formSearchPageRows('search');
	};
	
	var add = function() {
		$('#dataForm').modalPost('/routes', function() {
			$.alert('保存成功！', refreshRows);
		});
	};
	$('#addButton').on('click', add);
	
	var loadRoute = function(route) {
		$.ajax({
			type	:	'put',
			url		:	'/routes/'+ route.id +'/loading'
		}).done(function() {
			$.alert('操作成功！', refreshRows);
		});
	};

	var unloadRoute = function(route) {
		$.ajax({
			type	:	'put',
			url		:	'/routes/'+ route.id +'/unloading'
		}).done(function() {
			$.alert('操作成功！', refreshRows);
		});
	};

	var edit = function(route) {
		$('#dataForm').modalPut('/routes/' + route.id, route, function() {
			$.alert('保存成功！', refreshRows);
		});
	};
	
	var remove = function(route) {
		$.confirmDelete('/routes/' + route.id, function() {
			$.alert('删除成功！', refreshRows);
		});
	};
});