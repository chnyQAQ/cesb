(function($) {
	
	var methods = {
			
			init		:	function(_options) {
				return this.each(function() {
					
					var options = $.extend({}, $.fn.dropdownSearchCascade.defaults, _options);
					$(this).data('dropdownSearchCascade_options', options);
					
					var randomNum = function() {
					    var result = "";
					    for(var i = 0; i < 10; i++) {
					    	result += Math.floor(Math.random() * 10);
					    }
					    return result;
					};
					
					var $dropdownMenu = $('<div class="dropdown-menu p-0">').css({'width': options.totalWidth}).insertAfter(this);
					var $tabsContainer = $('<div class="tabs-container"></div>').appendTo($dropdownMenu);
					var $navTabs = $('<ul class="nav nav-tabs"></ul>').appendTo($tabsContainer);
					var $navItemRight = $('<li class="nav-item-right"></li>').appendTo($navTabs);
					var $cleanButton = $('<button type="button" class="btn btn-link">清空</button>').appendTo($navItemRight);
					
					var $tabContent = $('<div class="tab-content"></div>').appendTo($tabsContainer);
					
					$tabsContainer.on('click', function(e) {
						e.stopPropagation();
					});
					
					var hideDropdown = function() {
						$dropdownMenu.parent().dropdown('toggle');
					};
					
					// 初始化相关变量
					var initIndex = 0;
					var initValues = [];
					
					var selectCallback = function(currentObj, selectedObjs) {
						var array = [];
						$('.item.active', $tabsContainer).each(function(i, n) {
							array.push($(n).data('obj'));
						});
						options.select(array);
					};
					
					var appendTab = function(obj, e) {
						var url = options.url(obj);
						
						if (url === false) {
							hideDropdown();
							return;
						}
						
						$.ajax({type: 'get', url: url}).done(function(items) {
							
							if (!items || items.length <= 0) {
								if (!(e && e.namespace && e.namespace == 'dropdownSearchCascade')) {
									hideDropdown();
								}
								return;
							}
							
							var randomId = '_' + randomNum();
							var $navItem = $('<li class="nav-item"></li>').appendTo($navTabs);
							var $navLink = $('<a class="nav-link" data-toggle="tab" href="#'+ randomId +'"><span>请选择</span> <i class="fas fa-angle-down"></i></a>').appendTo($navItem);
							var $tabPane = $('<div class="tab-pane" id="'+ randomId +'"></div>').appendTo($tabContent);
							var $wrapper = $('<div class="d-flex flex-wrap"></div>').appendTo($tabPane);
							$navLink.tab('show').on('click', function(e) { e.preventDefault(); $(this).tab('show'); });
							
							$.each(items, function(i, obj) {
								var id = $.isFunction(options.id) ? options.id(obj) : eval('obj.' + options.id);
								var text = $.isFunction(options.text) ? options.text(obj) : eval('obj.' + options.text);
								var $div = $('<div></div>').css({'width': options.itemWidth}).appendTo($wrapper);
								$('<span data-id="'+ id +'" class="item">'+ text +'</span>').data({'obj': obj}).appendTo($div).on('click.dropdownSearchCascade', function(e) {
									// 已选中，点击无效
									if ($(this).hasClass('active')) {
										$navItem.next().children('.nav-link').tab('show');
										return;
									}
									// 选中状态 
									$('.item', $wrapper).removeClass('active');
									$(this).addClass('active');
									// 更新title
									$navLink.children('span').text(text.length > 5 ? (text.substring(0, 5) + '...') : text).attr({'title': (text.length > 5 ? text : '')});
									// 删除后面的tab
									$navItem.nextAll().each(function(j, n) {
										$($(n).children('.nav-link').attr('href')).remove();
										$(n).remove();
									});
									// 触发select事件
									selectCallback();
									// 添加下一级的tab
									appendTab(obj, e);
								});
							});
							
							// 初始化执行
							if (initIndex >= initValues.length) {
								return;
							}
							var target = $('[data-id="'+ initValues[initIndex++] +'"]', $tabPane).trigger('click.dropdownSearchCascade');
							if (target.length == 0) { initIndex = initValues.length; }
						});
						
					};
					
					var removeTabs = function() {
						$navTabs.children('.nav-item').remove();
						$tabContent.empty();
					};
					
					var refreshTabs = function() {
						// 初始化准备
						initIndex = 0;
						initValues = options.init();
						if (!$.isArray(initValues)) {
							initValues = [];
						}
						// 从新构建tab
						removeTabs();
						appendTab();
					};
					
					$cleanButton.on('click', function() {
						options.clean();
						hideDropdown();
					});
					
					$(this).parent('.dropdown').on('show.bs.dropdown', refreshTabs).on('hidden.bs.dropdown', removeTabs);
				
				});
			}
	};
	
	$.fn.dropdownSearchCascade = function(method) {
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.dropdownSearchCascade');
		}
	};
	
	$.fn.dropdownSearchCascade.defaults = {
			id			:	'code',
			text		:	'name',
			url			:	'/dropdownSearchCascadeUrl',
			init		:	$.noop,
			select		:	$.noop,
			clean		:	$.noop,
			totalWidth	:	650,
			itemWidth	:	150
    };
	
})(jQuery);


