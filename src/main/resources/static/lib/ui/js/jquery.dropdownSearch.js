(function($) {
	
	var methods = {
			
			init		:	function() {
				
				var url = '/dropdown-search';
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
				var options = $.extend({}, $.fn.dropdownSearch.defaults, _options);
				
				
				return this.each(function() {
					
					var $dropdownMenu = $('<div class="dropdown-menu p-0">').css({'minWidth': '220px'}).insertAfter(this);
					var $inputWrapper = $('<div class="bg-light border-bottom p-2">').appendTo($dropdownMenu);
					var $input = $('<input type="text" class="form-control" placeholder="搜索">').css({'maxWidth': '100%', 'width': '100%'}).appendTo($inputWrapper);
					
					var $searching = $('<button class="dropdown-item disabled" type="button" disabled="disabled">查询中...</button>');
					var $noResult = $('<button class="dropdown-item disabled" type="button" disabled="disabled">没有匹配的结果...</button>');
					var $moreResult = $('<button class="dropdown-item disabled" type="button" disabled="disabled">...</button>');
					
					var $buttonWrapper = $('<div class="text-center m-0 my-2 mx-2"></div>').appendTo($dropdownMenu);
					var $button = $('<button type="button" class="btn btn-block btn-secondary">清空</button>').appendTo($buttonWrapper);
					
					var doEmpty = function() {
						$('.dropdown-item', $dropdownMenu).remove();
					};
					
					var request = null;
					
					var doSearch = function() {
						doEmpty();
						$searching.insertBefore($buttonWrapper);
						
						if (request != null) {
							request.abort();
						}
						
						request = $.ajax({
							type	:	'get',
							url		:	$.isFunction(url) ? url() : url,
							data	:	{search: $input.val()}
						}).done(function(list) {
							doEmpty();
							if (typeof(list) == 'undefined' || list == null || !$.isArray(list) || list.length == 0) {
								$noResult.insertBefore($buttonWrapper);
							} else {
								$.each(list, function(i, obj) {
									var content = '';
									if ($.isFunction(options.text)) {
										content = options.text(obj);
									} else {
										content = eval('obj.' + options.text);
									}
									$('<button class="dropdown-item dropdown-item-valid" type="button"></button>').append(content).insertBefore($buttonWrapper).on('click', function() {
										action(obj);
									});
									if ((i + 1) >= 10) {
										$moreResult.insertBefore($buttonWrapper);
										return false;
									}
								});
							}
						}).always(function() {
							request = null;
						});
						
					};
					
					$(this).parent('.dropdown').on('show.bs.dropdown', function() {
						$input.val('');
						doSearch();
						$button.off('click').on('click', function() {
							action(null);
						});
					}).on('shown.bs.dropdown', function() {
						$input.trigger('focus');
					});
					
					
					$input.on('keyup', function(e) {
						if(e.keyCode && e.keyCode == 40) {
							if ($('.dropdown-item-valid', $dropdownMenu).length > 0) {
								$('.dropdown-item-valid:first', $dropdownMenu).trigger('focus');
							}
						}
					}).on('input', function(e) {
						if ($input.val() != '') {
							doSearch();
						}
					});
				
				});
			}
	
	};
	
	$.fn.dropdownSearch = function(method) {
		return methods.init.apply(this, arguments);
	};
	
	$.fn.dropdownSearch.defaults = {
			text			:	'name'
    };
	
})(jQuery);


