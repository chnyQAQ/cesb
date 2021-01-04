(function($) {

	//图片上传
	$.fn.uploadFile = function(op){
		var $wrap = $(this);
		var option = {
			url : '/authenticated/temp-file-upload',
			callback : $.noop,
			multiple : true,
			type : '*',//image(图片),texts(文本型文件，包括word,excel,powerpoint,pdf,txt), archive(压缩包，rar,zip,tar,gzip,7-zip)
			formData : {
				customType : null, //文件类型，并非文件后缀名
				nameprefix : null, //文件前缀
				maxNum : 5,
				maxSize			:	4 * 1024 * 1024,
	        	allowedTypes	:	'*',
	        	isCompress : null
			}
		};
		$.extend(true, option, op);
		
		var isDefineAllowTypes = (option.formData.allowedTypes && option.formData.allowedTypes != '*');
		
		if(option.type.indexOf('image') != -1 && !isDefineAllowTypes){
			option.formData.allowedTypes = 'jpg,jpeg,png,gif,bmp,tiff,psd,svg';
		} 
		if(option.type.indexOf('texts') != -1 && !isDefineAllowTypes){
			option.formData.allowedTypes += ' doc,docx,xls,xlsx,ppt,pptx,txt,pdf';
		}
		if(option.type.indexOf('archive') != -1 && !isDefineAllowTypes){
			option.formData.allowedTypes += ' rar,zip,tar,gzip,7-zip';
		}
		if(option.type.indexOf('apk') != -1 && !isDefineAllowTypes){
			option.formData.allowedTypes += ' apk,APK';
		}
		
		if(option.type == 'image' && option.formData.isCompress == null ){
			option.formData.isCompress = true;
		}else{
			option.formData.isCompress = false;
		}
		var fileNames = [];
		
		//文件预览列
		var $fileQueueList = null;
		if($wrap.find('.fileQueueList').length > 0){
			$fileQueueList = $wrap.find('.fileQueueList');
		}else{
			$fileQueueList = $('<div class="fileQueueList"></div>').appendTo($wrap);
		}
		
		//已添加的文件数量
		var $fileExistCount = null;
		if($wrap.find('[name=fileExistCount]').length > 0){
			$fileExistCount = $wrap.find('[name=fileExistCount]');
		}else{
			$fileExistCount = $('<input type="hidden" name="fileExistCount"/>').appendTo($wrap).val(0);
		}
		
		var $progress = $('<div id="progress" style="display:none;"></div>').appendTo($wrap);
		var $progressBar = $('<div class="bar" style="width: 0%; height:18px; background: #0EC30E; text-align: center; color: #fff;"></div>').appendTo($progress);
		//构建上传图片按钮
		function uploadButton(cb){
			var fa = 'fa fa-file';
			var text = '选择文件';
			if(option.type == 'image'){
				fa = 'fa fa-image';
				text = '选择图片';
			}
			var $filePicker = $('<div style="margin-top: -5px; margin-left: 0px; line-height: 30px;">'+ text +'</div>')
							.append(' <i class="'+ fa +'"></i>')
							.append('<input class="fileuploadBtn" type="file" name="file" title="'+ text +'">')
							.addClass('btn btn-sm btn-default btn-outline-light fileinput-button');
			if(option.multiple){
				$filePicker.find('.fileuploadBtn').attr('multiple','multiple');
			}
			$wrap.append($filePicker);
			if(cb){ cb($filePicker); }
        }
        var tempIndex = 0;
		
		//将文件上传至服务器
		function upload(){
			uploadButton(function($filePicker){
				$filePicker.fileupload({
			        dataType	:	'json',
			        url			:	option.url ,
			        formData	:	option.formData,
			        add: function (e, data) {
			        	var files = data.files;
			        	$.each(files, function(i, file){
			        		if(checkCanSave(file, tempIndex)){
			        			tempIndex = tempIndex + 1;
			        			data.submit();
			        		}
			        	});
			        },
			        progressall: function (e, data) {
			        	$progress.show();
			            var progress = parseInt(data.loaded / data.total * 100, 10);
			            $progressBar.html(progress + '%').css('width', progress + '%');
			        },
			        done		:	function (e, data) {
			        	var files = data.files;
			        	$.each(files, function(i, file){
			        		if(parseInt($fileExistCount.val()) == option.formData.maxNum){
	        					tempIndex = 0;
	        				}
			        		if(checkCanSave(file)){
			        			makeThumb(file, function($imgDiv){
			        				saveFile(data.result, $imgDiv);
				        		});
			        			$fileExistCount.val(parseInt($fileExistCount.val()) + 1);
			        		}else{
			        			return false;
			        		}
			        	});
			        	$progress.hide();
			        	if ($fileExistCount.val() >= option.formData.maxNum) {
			        		$wrap.find('.fileinput-button').hide();
			        	}
			        	option.callback(data);
			        }
			    });
			});
        }
        function checkCanSave(file, i){
			var canSave = true;
			//判断类型是否匹配
			var suffix = file.name.substr(file.name.lastIndexOf('.') + 1);
			
			if(option.formData.allowedTypes != '*' && option.formData.allowedTypes.indexOf(suffix.toLowerCase()) == -1){
				$.alert('上传文件类型不匹配');
				canSave = false;
				return canSave;
			}
			if(option.formData.maxSize < file.size){
				$.alert('文件过大');
				canSave = false;
				return canSave;
			}
//			if($.inArray(file.name, fileNames) != -1){
//				$.alert('该文件已上传');
//				canSave = false;
//				return canSave;
//			}
			if(parseInt($fileExistCount.val()) >= option.formData.maxNum || (i != undefined && i > option.formData.maxNum)){
				$.alert('已达到上传的最大数量');
				canSave = false;
				return canSave;
			}
			return canSave;
        }
        //保存返回文件值
		function saveFile(file, $imgDiv){
			fileNames.push(file.name);
			var preFix = option.formData.nameprefix;
			if(preFix){
        		var $fileName = $('<input type="hidden" name="' + preFix + '[' + $fileExistCount.val() + '][fileName]" value="'+ file.name +'"/>').appendTo($imgDiv);
                var $filePath = $('<input type="hidden" name="' + preFix + '[' + $fileExistCount.val() + '][filePath]" value="'+ file.path +'"/>').appendTo($imgDiv);
                var $fileType = $('<input type="hidden" name="' + preFix + '[' + $fileExistCount.val() + '][fileType]" value="'+ file.name.substr(file.name.lastIndexOf('.') + 1) + '"/>').appendTo($imgDiv);
                if (option.formData.customType != null && option.formData.customType != '') {
                	var $customType = $('<input type="hidden" name="' + preFix + '[' + $fileExistCount.val() + '][customType]" value="'+ option.formData.customType + '"/>').appendTo($imgDiv);
                }
                fileValuesSort();
			}else{
        		var $fileName = $('<input type="hidden" name="fileName" value="'+ file.name +'"/>').appendTo($imgDiv);
                var $filePath = $('<input type="hidden" name="filePath" value="'+ file.path +'"/>').appendTo($imgDiv);
                var $fileType = $('<input type="hidden" name="fileType" value="'+ file.name.substr(file.name.lastIndexOf('.') + 1) + '"/>').appendTo($imgDiv);
                if (option.formData.customType != null && option.formData.customType != '') {
                	 var $customType = $('<input type="hidden" name="customType" value="'+ option.formData.customType + '"/>').appendTo($imgDiv);
                }
        	}
        }
        //文件名、文件类型重新排序
		function fileValuesSort(){
			var preFix = option.formData.nameprefix;
			if(preFix){
				$('input[name$="[fileName]"]', $fileQueueList).each(function(i){
					$(this).attr('name', preFix + '[' + i + '][fileName]');
				});
				$('input[name$="[filePath]"]', $fileQueueList).each(function(i){
					$(this).attr('name', (preFix + '[' + i + '][filePath]'));
				});
				$('input[name$="[fileType]"]', $fileQueueList).each(function(i){
					$(this).attr('name', (preFix + '[' + i + '][fileType]'));
				});
				$('input[name$="[customType]"]', $fileQueueList).each(function(i){
					$(this).attr('name', (preFix + '[' + i + '][customType]'));
				});
			}
        }
        //图片预览
		function makeThumb(file, cb){
			var $imgDiv = $('<div class="upload-file"></div>').appendTo($fileQueueList);
			var $divPanelP = $('<div style="position:relative;"></div>').appendTo($imgDiv);
			var $divPanel = $('<div class="file-delete"></div>').appendTo($divPanelP)
							.append('<span class="delete-img">删除</span>');
			$imgDiv.on('mouseenter', function(){
				$divPanel.stop().animate({height: 30});
			}).on('mouseleave', function(){
				$divPanel.stop().animate({height: 0});
			});
			
			$divPanel.on('click', 'span', function(){
				tempIndex = 0;
				$imgDiv.remove();
				$fileExistCount.val(parseInt($fileExistCount.val())-1);
				fileNames.splice(jQuery.inArray(file.name,fileNames),1); 
				fileValuesSort();
				if(parseInt($fileExistCount.val()) == 0){
					$progress.hide();
				}
        		$wrap.find('.fileinput-button').show();
			});
			if (typeof (FileReader) === 'undefined') {
				$imgDiv.append('<span>您的浏览器暂不支持上传前预览</span>');
			} else {
				if (!/image\/\w+/.test(file.type)) {
					var suffix = file.name.substr(file.name.lastIndexOf('.') + 1);
					var $suffixDiv = $('<div class="file-name">'+ (file.name.length > 22? file.name.substr(0,22) + '...' : file.name) +'</div>').appendTo($imgDiv);
					$imgDiv.append('<div style="font-size:26px;text-align:center;">'+ suffix +'</div>');
				} else{
					var reader = new FileReader();
					reader.onload = function () {  
						var result = this.result;
						$('<img class="thrumb" width="110px" height="110px">').attr('src',result).appendTo($imgDiv);
					};
					reader.readAsDataURL(file);
				}
			}
			if(cb){cb($imgDiv);}
		}
		upload();
	};
	
	$.fn.editFile = function(op) {
		var $wrap = $(this);
		var option = {
			downloadUrl : '', // /xxx/download/{fileId}
			nameprefix: null, // 文件名前缀
			maxNum : null,
			files : null,  /* {id : '', filePath : '', fileName : '' //, downloadUrl : ''} */
			customType : null // 文件类型
		};
		$.extend(option, op);
		var $fileExistCount = null;
		if ($wrap.find('[name=fileExistCount]').length > 0) {
			$fileExistCount = $wrap.find('[name=fileExistCount]');
		} else {
			$fileExistCount = $('<input type="hidden" name="fileExistCount"/>').appendTo($wrap)
		}
		var $fileList = null;
		if ($wrap.find('.fileQueueList').length > 0) {
			$fileList = $wrap.find('.fileQueueList');
		} else {
			$fileList = $('<div class="fileQueueList"></div>').appendTo($wrap);
		}
		$('.upload-file', $fileList).remove();
		var fileCount = 0;
		if (option.files) {
			var custonFileList = [];
			if (option.customType != null && option.customType != '') {
				$.each(option.files, function(i, file) {
					if (option.customType == file.customType) {
						custonFileList.push(file);
                    }
                });
			} else {
				custonFileList = option.files;
			}
			$.each(custonFileList, function(i, file) {
				fileCount++;
				var $fileDiv = $('<div class="upload-file"></div>').appendTo($fileList);
				//
				var $delDiv = $('<div style="position: relative;"></div>').appendTo($fileDiv);
				var $fileDel = $('<div class="file-delete"><span class="delete-img">删除</span></div>').appendTo($delDiv);
				$fileDiv.on('mouseenter', function(){
					$fileDel.stop().animate({height: 30});
				}).on('mouseleave', function(){
					$fileDel.stop().animate({height: 0});
				});
				$delDiv.on('click', 'span', function(){
					$fileDiv.remove();
					var total = $fileExistCount.val();
					$fileExistCount.val((total-1));
					if(option.nameprefix){
						$('input[name$="[fileName]"]', $fileList).each(function(i){
							$(this).attr('name', option.nameprefix + '[' + i + '][fileName]');
						});
						$('input[name$="[filePath]"]', $fileList).each(function(i){
							$(this).attr('name', (option.nameprefix + '[' + i + '][filePath]'));
						});
						$('input[name$="[fileType]"]', $fileList).each(function(i){
							$(this).attr('name', (option.nameprefix + '[' + i + '][fileType]'));
						});
						$('input[name$="[customType]"]', $fileList).each(function(i){
							$(this).attr('name', (option.nameprefix + '[' + i + '][customType]'));
						});
					}
	        		$wrap.find('.fileinput-button').show();
				});
				var suffix = file.fileName.substr(file.fileName.lastIndexOf('.') + 1);
				if ('jpg,jpeg,png,gif,bmp,tiff,psd,svg'.indexOf(suffix.toLowerCase()) != -1) {
					var srcPath = '';
					if (option.downloadUrl == null || option.downloadUrl == '') {
						srcPath = contextPath + file.downloadUrl;
					} else {
						srcPath = contextPath + option.downloadUrl + '/' + file.id;
					}
					$fileDiv.append('<img class="thrumb" width="110px" height="110px" src="' + srcPath + '">');
				} else {
					$('<div class="file-name">'+ (file.fileName.length > 22? file.fileName.substr(0,22) + '...' : file.fileName) +'</div>').appendTo($fileDiv);
					$fileDiv.append('<div style="font-size:26px;text-align:center;">'+suffix+'</div>');
				}
				//
				if (option.nameprefix == null || option.nameprefix == '') {
					$fileDiv.append('<input type="hidden" name="fileName" value="' + file.fileName + '">');
					$fileDiv.append('<input type="hidden" name="filePath" value="' + file.filePath + '">');
					$fileDiv.append('<input type="hidden" name="fileType" value="' + file.fileType + '">');
					if (option.customType != null && option.customType != '') {
						$fileDiv.append('<input type="hidden" name="customType" value="' + option.customType + '">');
	                }
				} else {
					$fileDiv.append('<input type="hidden" name="'+option.nameprefix+'['+i+'][fileName]" value="' + file.fileName + '">');
					$fileDiv.append('<input type="hidden" name="'+option.nameprefix+'['+i+'][filePath]" value="' + file.filePath + '">');
					$fileDiv.append('<input type="hidden" name="'+option.nameprefix+'['+i+'][fileType]" value="' + file.fileType + '">');
					if (option.customType != null && option.customType != '') {
						$fileDiv.append('<input type="hidden" name="'+option.nameprefix+'['+i+'][customType]" value="'+option.customType+'">');
	                }
				}
			});
		}
		$fileExistCount.val(fileCount);
		if (option.maxNum != null && $fileExistCount.val() >= option.maxNum) {
    		$wrap.find('.fileinput-button').hide();
    	} else {
    		$wrap.find('.fileinput-button').show();
    	}
	};
	
	// 显示文件
	$.fn.showFile = function(op) {
		var $wrap = $(this);
		var option = {
			downloadUrl : '', // /xxx/download/{fileId}
			files : null,  /* {id : '', filePath : '', fileName : '' //, downloadUrl : ''} */
			customType : null // 文件类型
		};
		$wrap.addClass('fileQueueList');
		$.extend(option, op);
		if (option.files) {
			var custonFileList = [];
			if (option.customType != null && option.customType != '') {
				$.each(option.files, function(i, file) {
					if (option.customType == file.customType) {
						custonFileList.push(file);
                    }
                });
			} else {
				custonFileList = option.files;
			}
			$.each(custonFileList, function(i, file) {
				var $fileDiv = $('<div class="upload-file"></div>').appendTo($wrap);
				var srcPath = '';
				if (option.downloadUrl == null || option.downloadUrl == '') {
					srcPath = contextPath + file.downloadUrl;
				} else {
					srcPath = contextPath + option.downloadUrl + '/' + file.id;
				}
				var $a = $('<a href="'+srcPath+'" title="'+file.fileName+'"></a>');
				var suffix = file.fileName.substr(file.fileName.lastIndexOf('.') + 1);
				if ('jpg,jpeg,png,gif,bmp,tiff,psd,svg'.indexOf(suffix.toLowerCase()) != -1) {
					$a.append('<img src="'+srcPath+'" style="width: 110px; height: 110px;" title="'+file.fileName+'">');
					$a.addClass('fancybox');
					$a.attr('data-fancybox-group','gallery');
					$a.appendTo($fileDiv);
				} else {
					$a.text(file.fileName.length > 22? file.fileName.substr(0,22) + '...' : file.fileName);
					var $otherFile = $('<div class="otherfile"></div>').appendTo($fileDiv);
					$a.appendTo($otherFile);
					if (!typeof(file.fileType) == 'undefined')
					$otherFile.append('<div class="file-extensions">'+file.fileType+'</div>');
				}
			});
			$('.fancybox').fancybox();
		}
	};
	
	
})(jQuery);