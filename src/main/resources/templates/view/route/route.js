$(document).ready(function() {
	var graph;
	var endpointMap = new Map();
	var initGraph = function (){
	    // 判断浏览器是否支持
	    if (!mxClient.isBrowserSupported()){
	        // mxUtils报错提示
	        mxUtils.error('浏览器不支持mxGraph!', 200, false);
	    } else {
			// Creates the div for the graph
			var container = $('#jgraphDiv').get(0);
			var model = new mxGraphModel();
			graph = new mxGraph(container, model);
			graph.setConnectable(true);
			graph.setMultigraph(false);
			 // 禁止游离线条
			graph.setDisconnectOnMove(false);
			graph.setAllowDanglingEdges(false);
			// Enables moving of vertex labels
			graph.vertexLabelsMovable = false;
			graph.setCellsEditable(false);
			
			//
			graph.multiplicities.push(new mxMultiplicity(
					   false, 'Target', null, null, 1, 1, ['Source'],
					   'Target Must Have 1 Source',
					   'Target Must Connect From Source'));
			var listener = function(sender, evt){
				graph.validateGraph();
			};
			graph.getModel().addListener(mxEvent.CHANGE, listener);

			initGraphConfiguration();
			// 初始化图形
			initRouteXMLGraph();
			// 初始化endpoint
			initEndpointList();
			// 初始化保存
			initSave();
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
		
		// 连接事件
		graph.connectionHandler.addListener(mxEvent.CONNECT, function(handler, evt) {
			var edge = evt.properties.cell;
			// 起点
			var source = edge.source;
			// 目标点
			var target = edge.target.value;
			target.setAttribute('previousId', source.id);
		});
		
		// 覆盖创建cell name方法
		graph.convertValueToString = function(cell) {
			if (mxUtils.isNode(cell.value)) {
				if (cell.value.nodeName.toLowerCase() == 'endpoint') {
					if (currentEndpointMap != undefined && currentEndpointMap != null && currentEndpointMap[cell.getAttribute('endpointId', '')] != null) {
						return currentEndpointMap[cell.getAttribute('endpointId', '')].name;
					}
					return endpointMap.get(cell.getAttribute('endpointId', '')).name;
				}
			}
			return '';
		};
		
		// 校验连接线
		graph.multiplicities.push(new mxMultiplicity(
				   false, 'endpoint', null, null, 0, 1, ['endpoint'],
				   '端点只能有一个入口',
				   ''));
		/*
		// 调整布局
		var btn = $('#changeLayoutBtn').get(0);
		var parent = graph.getDefaultParent();
//		var layout = new mxCompactTreeLayout(graph);
		var layout = new mxHierarchicalLayout(graph);
		mxEvent.addListener(btn, mxEvent.CLICK, function(evt) {
			layout.execute(parent);
		});
		*/
		
		// 删除
		var keyHandler = new mxKeyHandler(graph);
		keyHandler.bindKey(46, function(evt) {
			removeCells(graph.getSelectionCells());
		});
		graph.addListener(mxEvent.CLICK, function(sender, evt){
			if (evt.properties.cell && !evt.properties.cell.isEdge()) {
				$('#removeBtn').prop('disabled', false);
			} else {
				$('#removeBtn').prop('disabled', true);
			}
		});
		$('#removeBtn').on('click', function() {
			removeCells(graph.getSelectionCells());
		});
		var removeCells = function(cells) {
			if (graph.isEnabled()) {
				$.each(cells, function(i, cell){
					if (!cell.isEdge()) {
						graph.removeCells([cell], true);
					}
				});
			}
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
	
	
	var initEndpointList = function() {
		var insertEndpoint = function(type, list, style) {
			if (list != null) {
				var $li = $('<li class="nav-item"></li>').appendTo($("#endpointListUl"));
				var $a = $('<a class="nav-link" href="#nav-endpoint-' + type + '" ><span>' + type + '</span></a>').appendTo($li);
				var $div = $('<div id="nav-endpoint-' + type + '" class="collapse"></div>').appendTo($li);
				var $ul = $('<ul class="nav flex-column pl-4 pb-4" id="#nav-endpoint-' + type + '"></ul>').appendTo($div);
				$.each(list, function(i, endpoint){
					if (i == 0) {
						$a.attr('aria-expanded', false).attr('data-toggle','collapse');
					}
					$('<li class="nav-item"><span class="nav-link" title="'+endpoint.url+'">'+endpoint.name+'</span></li>').on('click', function(){
						addEndpointToGraph(type, endpoint, style);
					}).appendTo($ul);
					endpointMap.set(endpoint.id, endpoint);
				});
			}
			
		};
		var addEndpointToGraph = function(type, endpoint, style) {
			// graph 范围信息
			var bounds = graph.getGraphBounds();
			var x = 20;
			var y = bounds.height > 30 ? (bounds.height + bounds.y + 20) : 20;
			var parent = graph.getDefaultParent();
			var doc = mxUtils.createXmlDocument();
			var uId = Math.uuid();
			var xmlEndpoint = doc.createElement('endpoint');
			xmlEndpoint.setAttribute('id', uId);
			xmlEndpoint.setAttribute('routeId', currentRoute.id);
			xmlEndpoint.setAttribute('endpointType', type);
			xmlEndpoint.setAttribute('endpointId', endpoint.id);
			xmlEndpoint.setAttribute('previousId', '');
			xmlEndpoint.setAttribute('expression', '');
			xmlEndpoint.setAttribute('endpointName', endpoint.name);
			graph.insertVertex(parent, uId, xmlEndpoint, x, y, 120, 50, style);
		};
		$('#searchForm').formSearch({
			url : '/endpoint/list-search',
			success : function(endpointMap) {
				$("#endpointListUl").empty();
				$.each(endpointMap, function(key, value) {
					insertEndpoint(key, value, 'boxstyle');
				});
				$('a[data-toggle="collapse"]', '#endpointListUl').on('click', function() {
					if ($($(this).attr('href')).hasClass('show')) {
						$(this).parent().removeClass('active');
					} else {
						$(this).parent().addClass('active');
					}
				});
			}
		}).trigger('submit');
	};

	// 保存
	var initSave = function() {
		$('#saveBtn').on('click', function(){
			var xml = new mxCodec().encode(graph.getModel());
			var saveRoute = currentRoute;
			saveRoute.routeXML = xml.outerHTML;
			var endpointListObj = getEndpointList();
			saveRoute.routeEndpoints = endpointListObj.endpointList;
			if (endpointListObj.noPreviousNum < 1) {
				$.alert('保存失败！', '路由中必须有一个起点');
			} else if (endpointListObj.noPreviousNum > 1) {
				$.alert('保存失败！', '路由中只能有一个起点');
			} else {
				$('#saveBtn').prop('disabled', true);
				$.ajax({
					type : 'put',
					url : '/routes/'+currentRoute.id+'/design',
					data: saveRoute
				}).done(function(data) {
					$.alert('保存成功！');
					currentRoute = data;
					initRouteXMLGraph();
				}).always(function() {
					$('#saveBtn').prop('disabled', false);
				});
			}
		});
	};
	
	// 获取xml中的endpointlist
	var getEndpointList = function() {
		var endpointList = [];
		var endpointMap = new HashMap();
		$.each(graph.getChildCells(), function(i, cell) {
			if (cell.value) {
				var endpoint = {
						'id' : cell.id,
						'routeId' : cell.value.getAttribute('routeId'),
						'endpointType' : cell.value.getAttribute('endpointType'),
						'endpointId' : cell.value.getAttribute('endpointId'),
						'previousId' : cell.value.getAttribute('previousId', ''),
						'expression' : cell.value.getAttribute('expression', ''),
						'endpointName' : cell.value.getAttribute('endpointName')
				};
				endpointList.push(endpoint);
				endpointMap.put(endpoint.id, endpoint);
			}
		});
		var noPreviousNum = 0;
		$.each(endpointList, function(i, ep) {
			if (ep.previousId != '') {
				var previous = endpointMap.get(ep.previousId);
				if (!previous) {
					ep.previousId = '';
				}
			}
			if (ep.previousId == '') {
				noPreviousNum++;
			}
		});
		console.info(endpointList);
		return {endpointList : endpointList, noPreviousNum : noPreviousNum };
	};
	
	initGraph();
});