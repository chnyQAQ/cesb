(function($) {
	
	var resetSelect = function(select) {
		var starterOption = true;
		if ($(select).attr('data-starter-option') == 'false') {
			starterOption = false;
		}
		if (starterOption) {
			$('option[value!=""]', select).remove();
			if ($('option[value=""]', select).length == 0) {
				$(select).append('<option value="">-</option>');
			}
		} else {
			$(select).empty();
		}
	};
	
	var fillSelect = function(select, dictionaries) {
		$.each(dictionaries, function(i, dictionary) { $(select).append('<option value="'+ dictionary.code +'">'+ dictionary.name +'</option>'); });
		if (typeof($(select).attr('data-value')) != 'undefined') { $(select).val($(select).attr('data-value')); }
	};
	
	var methods = {
			
			init		:	function(_options) {
				return this.each(function() {
					
					var options = $.extend({}, $.fn.dictionary.defaults, _options);
					$(this).data('dictionary_options', options);
					
					var typeCode = $(this).attr('data-dict');
					var tagName = $(this).get(0).tagName;
					
					// 单个下拉
					if (tagName == 'SELECT' || tagName == 'select') {
						
						$.ajax({url: ('/authenticated/dictionary-types/' + typeCode + '/roots')}).done($.proxy(function(dictionaries) {
							resetSelect(this);
							fillSelect(this, dictionaries);
							$(this).trigger('change');
						}, this));
						
					} 
					
					// input
					else if (tagName == 'INPUT' || tagName == 'input') {
						var code = $(this).attr('data-value');
						var trans = $(this).attr('data-trans');
						if (typeof(trans) == 'undefined') { trans = 'names'; }
						if (typeof(code) != 'undefined' && code.length > 0) {
							$.ajax({url: ('/authenticated/dictionary-types/' + typeCode + '/'+ code +'/text?trans=' + trans)}).done($.proxy(function(obj) {
								$(this).val(obj.text);
							}, this));
						}
					}
					
					// 级联或者文本
					else {
						// 级联
						if ($(this).children('select').length > 0) {
							
							var first = $(this).children('select:first');
							
							$(this).children('select').each(function(i, n) {
								if (i > 0) {
									$(n).prop('disabled', true);
									$(n).prev().on('change', function() {
										resetSelect(n);
										if ($(this).val() == '') {
											$(n).trigger('change');
											$(n).prop('disabled', true);
										} else {
											$.ajax({url: ('/authenticated/dictionary-types/' + typeCode + '/' + $(this).val() + '/children')}).done(function(dictionaries) {
												$(n).prop('disabled', false);
												fillSelect(n, dictionaries);
												$(n).trigger('change');
											});
										}
									});
								}
							});
							$.ajax({url: ('/authenticated/dictionary-types/' + typeCode + '/roots')}).done(function(dictionaries) {
								resetSelect(first);
								fillSelect(first, dictionaries);
								$(first).trigger('change');
							});
						}
						
						// 文本
						else {
							// 同时具有data-trans、data-value，需要翻译
							var code = $(this).attr('data-value');
							var trans = $(this).attr('data-trans');
							if (typeof(trans) == 'undefined') { trans = 'names'; }
							if (typeof(code) != 'undefined' && code.length > 0) {
								$.ajax({url: ('/authenticated/dictionary-types/' + typeCode + '/'+ code +'/text?trans=' + trans)}).done($.proxy(function(obj) {
									$(this).text(obj.text);
								}, this));
							}
						}
						
					}
				
				});
			}
	
	};
	
	$.fn.dictionary = function(method) {
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.dictionary');
		}
	};
	
	$.fn.dictionary.defaults = {
			
    };
	
})(jQuery);


