(function($) {
	
	var methods = {
			
			init		:	function(obj, cb) {
				return this.each(function() {
					
					var arrayInputIndexRecorder = {};
					
					if (obj && $.isPlainObject(obj)) {
						$('[name]', this).each(function() {
							
							var value = undefined;
							var name = $(this).attr('name');
							try {
								
								if (name.indexOf('[') > 0) {
									
									// name不唯一的表单组件，数组
									
									var actualName = name.substr(0, name.indexOf('['));
									
									var index = null;
									if (typeof(arrayInputIndexRecorder[actualName]) == 'undefined') {
										arrayInputIndexRecorder[actualName] = 0;
									}
									index = arrayInputIndexRecorder[actualName];
									
									var array = obj[actualName];
									if (typeof(array) == 'undefined') {
										array = eval('obj.' + actualName);
									}
									value = array[index];
									
									if (typeof(value) != 'undefined') {
										if (value === true) {
											value = 'true';
										} else if (value === false) {
											value = 'false';
										}
										$(this).val(value);
										$(this).attr('data-value', value);
										$(this).trigger('change');
									}
									
									// index 加 1
									arrayInputIndexRecorder[actualName] = index + 1;
									
								} else {
									
									// name唯一的表单组件
									value = obj[name];
									if (typeof(value) == 'undefined') {
										value = eval('obj.' + name);
									}
									
									if (typeof(value) != 'undefined') {
										if (value === true) {
											value = 'true';
										} else if (value === false) {
											value = 'false';
										}
										$(this).val(value);
										$(this).attr('data-value', value);
										$(this).trigger('change');
									}
									
								}
								
								
								if ($(this).attr('data-dict')) {
									var tagName = $(this).get(0).tagName;
									if (!(tagName == 'SELECT' || tagName == 'select' || ($(this).children('select').length > 0))) {
										$(this).dictionary();
									}
								}
								
							} catch (e) {
								console.warn('formFill will ignore name: ' + name + ', error: ' + e);
							}
						});
					}
				
				});
			}
	
	};
	
	$.fn.formFill = function(method) {
		return methods.init.apply(this, arguments);
	};
	
})(jQuery);


