$(document).ready(function() {
	
	var addWorkerCard = function(status) {
		var $card = $('<div id="' + status.key + '" class="card card-worker m-2"></div>').attr({'order': status.order});
		var $cardHeader = $('<div class="card-header"></div>').appendTo($card);
		var $cardText = $('<span class="w-100 d-inline-block text-nowrap text-truncate"><i class="fas fa-cog"></i> ' + status.key + '（' + status.name + '）</span>').attr({'title': (status.key + '（' + status.name + '）')}).appendTo($cardHeader);
		
		var $listGroup = $('<ul class="list-group list-group-flush"></ul>').appendTo($card);
		// card 插入到合适的位置
		var $target = null;
		$('#workers > .card-worker').each(function(i, n) {
			if (parseInt($card.attr('order')) < parseInt($(n).attr('order'))) {
				$target = $(n);
				return false;
			}
		});
		if ($target == null) {
			$card.appendTo('#workers');
		} else {
			$card.insertBefore($target);
		}
		// 添加新的card同时，添加第一个节点
		addWorkerNode(status, $card);
	};
	
	var addWorkerNode = function(status, $card) {
		var $node = $('<li class="list-group-item d-flex"></li>').attr({'node': status.node});
		var $nodeDiv = $('<div>' + status.node + '</div>').appendTo($node);
		var $emptyDiv = $('<div class="mr-auto"></div>').appendTo($node);
		var $busyDiv = $('<div class="mr-2 busy"><img alt="" src="' + contextPath + '/view/workers/img/gear.gif" width="16px"></div>').appendTo($node).hide();
		var $modeDiv = $('<div class="mr-2"></div>').appendTo($node);
		var $modeBtn = $('<button type="button" class="btn btn-default btn-sm mode"></button>').appendTo($modeDiv);
		var $holdedDiv = $('<div></div>').appendTo($node);
		var $holdedBtn = $('<button type="button" class="btn btn-default btn-sm holded" disabled="disabled"></button>').appendTo($holdedDiv);
		$modeBtn.on('click', function() {
			var switchModeRequest = {
				type	:	'switch_mode',
				node	:	status.node,
				key		:	status.key
			};
			ws.send(JSON.stringify(switchModeRequest));
		});
		$holdedBtn.on('click', function() {
			var holded = $(this).attr('holded');
			var holdedRequest = {
				type	:	'',
				node	:	status.node,
				key		:	status.key
			};
			if (holded == 'true') {
				holdedRequest.type = 'unhold';
			} else if (holded == 'false') {
				holdedRequest.type = 'hold';
			}
			if (holdedRequest.type) {
				ws.send(JSON.stringify(holdedRequest));
			}
		});
		// node 插入到合适的位置
		var $ul = $('ul', $card);
		var $target = null;
		$($ul).children('li').each(function(i, n) {
			if ($node.attr('node') < $(n).attr('node')) {
				$target = $(n);
				return false;
			}
		});
		if ($target == null) {
			$node.appendTo($ul);
		} else {
			$node.insertBefore($target);
		}
		
		// 添加新的node同时，更新该node
		updateWorkerNode(status, $node);
	};
	
	var updateWorkerNode = function(status, $node) {
		// busy
		if (status.busy) {
			$('.busy', $node).show();
		} else {
			$('.busy', $node).hide();
		}
		// mode
		if (status.mode == 'auto') {
			$('.mode', $node).removeClass('btn-default').removeClass('btn-light').addClass('btn-primary').text('自动');
			$('.holded', $node).prop({'disabled': true});
		} else if (status.mode == 'manual') {
			$('.mode', $node).removeClass('btn-default').removeClass('btn-primary').addClass('btn-light').text('手动');
			$('.holded', $node).prop({'disabled': false});
		} else {
			alert('未知的mode！');
		}
		// holded
		if (status.holded) {
			$('.holded', $node).removeClass('btn-default').removeClass('btn-light').addClass('btn-outline-warning').text('已占有').attr({'holded': 'true'});
		} else {
			$('.holded', $node).removeClass('btn-default').removeClass('btn-outline-warning').addClass('btn-light').text('未占有').attr({'holded': 'false'});
		}
	};
	
	var handleWorkerStatus = function(status) {
		var $card = $('#' + status.key);
		if ($card.length == 0) {
			addWorkerCard(status);
		} else {
			var $node = $('[node="' + status.node + '"]', $card);
			if ($node.length == 0) {
				addWorkerNode(status, $card);
			} else {
				updateWorkerNode(status, $node);
			}
		}
	};
	
	
	var url = "ws://" + window.location.hostname + ":" + window.location.port + contextPath + '/websocket/workers';
	var ws = null;
	
	var bindSockEvent = function(sock) {
		sock.onopen = function() {
			$('.msg-connected').removeClass('d-none');
			$('.msg-disconnected').addClass('d-none');
			ws = sock;
		};
		sock.onmessage = function(e) {
			handleWorkerStatus(JSON.parse(e.data));
		};
		sock.onclose = function() {
			$('.msg-connected').addClass('d-none');
			$('.msg-disconnected').removeClass('d-none');
			$('#workers').empty();
			ws = null;
		};
	};
	
	var connect = function() {
		if (ws == null) {
			bindSockEvent(new WebSocket(url));
		}
	};
	setInterval(connect, 10 * 1000);
	connect();
	
});