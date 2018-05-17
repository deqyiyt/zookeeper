var setting = {
	edit : {
		enable : true
	},
	data : {
		simpleData : {
			enable : true
		}
	},
	callback : {
		onClick : onClick
	}
};

function initTree() {
	saveTreeStatus();
	if($(".zk-host").val()) {
		$.ajax({
			data:{
				host:$(".zk-host").val()
				,path:$(".zk-root").val()
			},
			type: 'POST',
			dataType : "json",
			url: "tree.json",
			success:function(data){
				if(data.ResultCode && data.Content && data.Content.length > 0) {
					$.fn.zTree.init($("#treeDemo"), setting, data.Content);
					rander();
					$.cookie("zk_manager_host", $(".zk-host").val());
					$.cookie("zk_manager_path", $(".zk-root").val());
				} else {
					$('#treeDemo').html("没查询到数据");
				}
			}
		});
	}
}
function rander() {
	var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
	var cookie = $.cookie("zk_manager_tree");
	if(cookie){
		var oldData = JSON.parse(cookie);
		var nodes = treeObj.getNodes();
		randerTree(oldData, nodes, treeObj);
	}
}
function randerTree(oldData, nodes, zTree) {
	for(var i=0;i<nodes.length;i++){
		var data = oldData[nodes[i].name];
		if(data) {
			if(data.open) {
				zTree.expandNode(nodes[i], true);
			}
			if(data.selection) {
				zTree.selectNode(nodes[i], true);
				onClick(null,null,nodes[i],null);
			}
		}
		if(nodes[i].children) {
			randerTree(oldData, nodes[i].children, zTree);
		}
	}
}
function saveTreeStatus() {
	var treeObj = $.fn.zTree.getZTreeObj("treeDemo");
	if(treeObj) {
		var nodes = treeObj.getNodes();
		var oldData = {};
		array = readTree(oldData, nodes);
		nodes = treeObj.getSelectedNodes(true);
		if(nodes && nodes.length > 0) {
			for(var i=0;i<nodes.length;i++){
				oldData[nodes[i].name] = {
					"selection":true
				}
			}
		}
		$.cookie("zk_manager_tree", JSON.stringify(array));
	}
}
function readTree(oldData, nodes) {
	for(var i=0;i<nodes.length;i++){
		if(nodes[i].open) {
			oldData[nodes[i].name] = {
				"open":true
			}
		}
		if(nodes[i].children) {
			oldData = readTree(oldData, nodes[i].children);
		}
	}
	return oldData;
}
$(function() {
	$(".zk-host").val($.cookie("zk_manager_host") || "");
	$(".zk-root").val($.cookie("zk_manager_path") || "");
	
	initTree();
	
	$('.update-zk').click(function() {
		var path = $('.zk-path').val();
		if (path == null || path == "") {
			alert("目录为空");
			return;
		}
		if (confirm("确定要修改？")) {
			var data = $('.zk-data').val();
			$.ajax({
				url : 'update.json',
				type: 'POST',
				dataType : "json",
				data : {
					"path" : path,
					"data" : data
				},
				success : function(data) {
					if (data != null) {
						if (data.ResultCode) {
							alert("修改成功");
							initTree();
						} else {
							alert("修改失败");
						}
					} else {
						alert("修改失败");
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("修改失败");
				}
			});
		}
	});

	$('.set-zkhost').click(function() {
		var path = $('.zk-host').val();
		var data = $('.zk-root').val();
		if (path == null || path == "") {
			alert("zk地址为空");
			return;
		}
		if (data == null || data == "") {
			alert("根节点为空");
			return;
		}
		if (confirm("确定要更换？")) {
			initTree();
		}
	});

	$('.create-path').click(function() {
		var path = $('.zk-path').val();
		if (path == null || path == "") {
			alert("目录为空");
			return;
		}
		var data = $('.zk-data').val();
		$.ajax({
			url : 'add.json',
			dataType : 'json',
			type : 'post',
			data : {
				"path" : path,
				"data":data
			},
			success : function(data) {
				if (data != null) {
					if (data.ResultCode) {
						alert(data.Content);
						initTree();
					} else {
						alert("创建失败");
					}
				} else {
					alert("创建失败");
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("创建失败");
			}
		});
	});

	$('.del-path').click(function() {
		var path = $('.zk-path').val();
		if (path == null || path == "") {
			alert("请选择要删除的目录");
			return;
		}
		if (confirm("确定要删除？")) {
			$.ajax({
				url : 'remove.json',
				dataType : 'json',
				type : 'post',
				data : {
					"path" : path
				},
				success : function(data) {
					if (data != null) {
						if (data.ResultCode) {
							alert(data.Content);
							initTree();
						} else {
							alert("删除失败");
						}
					} else {
						alert("删除失败");
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("删除失败");
				}
			});
		}

	});
	$('.import-data').click(function() {
		var path = $('.zk-path').val();
		$.ajaxFileUpload({
			url : "import.json",
			secureuri : false,
			fileElementId : 'uploadify',
			dataType : 'json',
			success : function(data, status) {
				if (data != null) {
					if (data.ResultCode) {
						alert(data.Content);
						initTree();
					} else {
						alert("上传失败");
					}
				} else {
					alert("上传失败");
				}
			},
			error : function(data, status, e) {
				alert(e);
			}
		});
	});
	$('.export-data').click(function() {
		var path = $('.zk-path').val();
		if (path == null || path == "") {
			alert("请选择要导出的目录");
			return;
		}
		if (confirm("确定要导出？")) {
			window.open("export.json?path=" + path);
		}
	});
});
function onClick(event, treeId, treeNode, clickFlag) {
	$(".zk-path").val(treeNode.name);
	$.ajax({
		data:{
			"path" : treeNode.name
		},
		type: 'POST',
		dataType : "json",
		url: "child.json",
		success:function(data){
			$(".zk-data").val(data.Content);
		}
	});
};

