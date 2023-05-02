$(function(){
	$("#publishBtn").click(publish);
});

function publish() {

	$("#publishModal").modal("hide");

	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJOSN(data);
			//提示框要显示的数据
			$("#hintModal").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//2秒后隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//如果添加成功，那么刷新页面
				if (data.code == 200){
					window.location.reload();
				}
			}, 2000);
		}
	);

	$("#hintModal").modal("show");
	setTimeout(function(){
		$("#hintModal").modal("hide");
	}, 2000);
}