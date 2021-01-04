(function($) {
	
	var methods = {
			
			init		:	function() {
				
				var url = '/dropdown-search-tree';
				var _options = {};
				var action = $.noop;
				
				$.each(arguments, function(i, arg) {
					if (i == 0) {
						url = arg;
					} else {
						if ($.isPlainObject(arg)) {
							_options = arg;
						} else if ($.isFunction(arg)) {
							action = arg;
						}
					}
				});
				var options = $.extend({}, $.fn.dropdownSearchTree.defaults, _options);
				
				
				return this.each(function() {
					
					var $dropdownMenu = $('<div class="dropdown-menu p-0">').css({'minWidth': '220px'}).insertAfter(this);
					var $tree = $('<div class="py-3 px-3"></div>').css({'max-height': '500px', 'overflow-y': 'scroll'}).appendTo($dropdownMenu);
					var $buttonWrapper = $('<div class="d-flex justify-content-between m-0 my-2 mx-2"></div>').appendTo($dropdownMenu);
					var $button = $('<button type="button" class="btn btn-secondary btn-block" disabled="disabled">确定</button>').appendTo($buttonWrapper);
					var $clearButton = $('<button type="button" class="btn btn-link" disabled="disabled">清空</button>').appendTo($buttonWrapper);
					
					$tree.on('click', function(e) {
						e.stopPropagation();
					});
					
					var buildTree = function() {
						
						$button.prop('disabled', true).off('click').on('click', function() {
							action(null);
						});
						$clearButton.prop('disabled', false).off('click').on('click', function() {
							action(null);
						});
						
						$tree.jstree('destroy');
						$tree.jstree({
							plugins		:	['sort', 'search', 'types', 'unique'],
							core		:	{
								multiple		:	options.multiple,
								data			:	{
									url: $.isFunction(url) ? url() : url,
									data: function(node) { return {id: node.id}; }
								}
							},
							sort		:	function(id1, id2) {
								var a = $tree.jstree('get_node', id1, false);
								var b = $tree.jstree('get_node', id2, false);
								return a.original[options.sortBy] > b.original[options.sortBy] ? 1 : -1;
							},
							types		:	options.types
						}).on('loaded.jstree', function(e, data) {
							$button.prop('disabled', false);
							if (options.openAll) {
								$tree.jstree('open_all');
							}
						}).on('changed.jstree',function(e, data) {
							$button.off('click').on('click', function() {
								if (options.multiple) {
									var array = [];
									$.each(data.selected, function(i, n) {
										array.push($tree.jstree('get_node', n, false).original);
									});
									action(array);
								} else {
									if (data.selected.length == 0) {
										action(null);
									} else {
										action($tree.jstree('get_node', data.selected[0], false).original);
									}
								}
							});
						});
					};
					
					$(this).parent('.dropdown').on('show.bs.dropdown', buildTree);
				
				});
			}
	
	};
	
	$.fn.dropdownSearchTree = function(method) {
		return methods.init.apply(this, arguments);
	};
	
	
	$.fn.dropdownSearchTree.defaults = {
			sortBy			:	'text',
			types			:	{'folder':	{icon: 'jstree-folder'}, 'file': {icon: 'jstree-file'}},
			multiple		:	false,
			openAll			:	false
    };
	
})(jQuery);


