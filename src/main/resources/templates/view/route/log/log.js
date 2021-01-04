$(document).ready(function() {
	var $form = $("<form></form>");
	$form.formSearchRows({
		url		:	'/routes/logs/' + routeLog.id + '/list-all',
		table   :   '#routeEndpointLogTable',
		cm		:	[

						{col: 'endpointCode'},
						{col: 'endpointName'},
						{col: 'endpointType'}
						/*
						{col: function(routeEndpointLog){
							return new Date(routeEndpointLog.beginTime).format('yyyy-MM-dd hh:mm:ss.S') ;
						}},
						{col: function(routeEndpointLog){
							return new Date(routeEndpointLog.endTime).format('yyyy-MM-dd hh:mm:ss.S') ;
						}},
						{col: function(routeEndpointLog){
							var duration = new Date(routeEndpointLog.endTime).getTime() - new Date(routeEndpointLog.beginTime).getTime();
							return $('<center>' + (duration > 0 ? duration : "1") + '</center>');
						}},
						{col: function(routeEndpointLog){
							return $('<center>' + (routeEndpointLog.hasException ? "是" : "否") + '</center>');
						}},
						{col: function(routeEndpointLog){
							return $('<div title="'+routeEndpointLog.exceptionType+'">'+(routeEndpointLog.exceptionType ? routeEndpointLog.exceptionType : '<center>-</center>')+'</div>');
						}},
						{col: function(routeEndpointLog){
							return $('<div title="'+routeEndpointLog.exceptionMessage+'">'+(routeEndpointLog.exceptionMessage ? routeEndpointLog.exceptionMessage : '<center>-</center>')+'</div>');
						}}*/
			 	 	]
	}).trigger('submit');
	
	var refreshRows = function() {
		$('#searchForm').formSearchPageRows('search');
	};

	var graph;
	initGraph = function (){
		// 判断浏览器是否支持
		if (!mxClient.isBrowserSupported()){
			// mxUtils报错提示
			mxUtils.error('浏览器不支持mxGraph!', 200, false);
		} else {
			// Creates the div for the graph
			var container = $('#jgraphDiv').get(0);
			var model = new mxGraphModel();
			graph = new mxGraph(container, model);
			// 禁止移动
			graph.setCellsMovable(false);
			// 禁止连接点悬空
			graph.setAllowDanglingEdges(false);
			// 禁止改变大小
			graph.setCellsResizable(false);

			// 禁用浏览器默认的右键菜单栏
			mxEvent.disableContextMenu(container);

			initGraphConfiguration();
			// 初始化图形
			initRouteXMLGraph();
		}
	};
	var initGraphConfiguration = function() {
		// 自定义样式
		var style = {};
		style[mxConstants.STYLE_SHAPE] = 'box';
		style[mxConstants.STYLE_FILLCOLOR] = '#0084ff';
		style[mxConstants.STYLE_STROKECOLOR] = '#0084ff';
		style[mxConstants.STYLE_FONTSIZE] = '16';
		style[mxConstants.STYLE_FONTCOLOR] = '#ffffff';
		graph.getStylesheet().putCellStyle('boxstyle', style);
		style = {};
		style[mxConstants.STYLE_SHAPE] = 'line';
		style[mxConstants.STYLE_FONTSIZE] = '16';
		style[mxConstants.STYLE_PERIMETER_SPACING]= 4;
		style[mxConstants.STYLE_STROKECOLOR] = '#0084ff';
		style[mxConstants.STYLE_STROKEWIDTH]= 4;
		style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR]= '#ffffff';
		graph.getStylesheet().putCellStyle('linestyle', style);

		// 覆盖创建cell name方法
		graph.convertValueToString = function(cell) {
			if (mxUtils.isNode(cell.value)) {
				if (cell.value.nodeName.toLowerCase() == 'endpoint') {
					return endpointMap[cell.getAttribute('endpointId', '')].name;
				}
			}
			return '';
		};
	};

	// 加载route保存的xml图形
	var initRouteXMLGraph = function() {
		graph.removeCells(graph.getChildCells(), true);
		if (currentRoute.routeXML != null || currentRoute.routeXML != '') {
			// 重新加载图形
			var xmlDocument = mxUtils.parseXml(currentRoute.routeXML);
			var decoder = new mxCodec();
			var node = xmlDocument.documentElement;
			decoder.decode(node, graph.getModel());
		}
	};

	initGraph();
});