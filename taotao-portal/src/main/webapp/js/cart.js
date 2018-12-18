var TTCart = {
	load : function(){ // 加载购物车数据
		
	},
	itemNumChange : function(){
		$(".increment").click(function(){//＋
			var _thisInput = $(this).siblings("input");
			_thisInput.val(eval(_thisInput.val()) + 1);
			$.post("/cart/add/"+_thisInput.attr("itemId") + ".html?num=1",function(data){
				TTCart.refreshTotalPrice();
			});
		});
		$(".decrement").click(function(){//-
			var _thisInput = $(this).siblings("input");
			if(eval(_thisInput.val()) == 1){
				return ;
			}
			_thisInput.val(eval(_thisInput.val()) - 1);
			$.post("/cart/add/"+_thisInput.attr("itemId") + ".html?num=-1",function(data){
				TTCart.refreshTotalPrice();
			});
		});
		$(".quantity-form .quantity-text").rnumber(1);//限制只能输入数字
		$(".quantity-form .quantity-text").change(function(){
			var _thisInput = $(this);
			$.post("/service/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val(),function(data){
				TTCart.refreshTotalPrice();
			});
		});
	},
	refreshTotalPrice : function(){ //重新计算总价
		var total = 0;
		$(".quantity-form .quantity-text").each(function(i,e){
			var _this = $(e);
			if($("input[type='checkbox'][name='checkItems']").eq(i).prop("checked")){
				total += (eval(_this.attr("itemPrice")) * 10000 * eval(_this.val())) / 10000;
			}	
		});
		$(".totalSkuPrice").html(new Number(total/100).toFixed(2)).priceFormat({ //价格格式化插件
			 prefix: '￥',
			 thousandsSeparator: ',',
			 centsLimit: 2
		});
	},
	selectAll : function(){//全选
		$("#toggle-checkboxes_up").click(function() {
			if ($("#toggle-checkboxes_up").prop("checked")) {  //全选
		        $("#toggle-checkboxes_down").prop("checked", true);  
		        var a = $("input[name='checkItems']");
		        for( var i=0; i<a.length; i++ ){
		        	a[i].checked = true;
		        }
		    } else {  //全不选
		        $("#toggle-checkboxes_down").prop("checked", false);  
		        var a = $("input[name='checkItems']");
		        for( var j=0; j<a.length; j++ ){
		        	a[j].checked = false;
		        }
		    }  
		    TTCart.refreshTotalPrice();
		});
	},
	selectAll2 : function(){
		$("#toggle-checkboxes_down").click(function() {
			if ($("#toggle-checkboxes_down").prop("checked")) {  //全选
		        $("#toggle-checkboxes_up").prop("checked", true);  
		        var a = $("input[name='checkItems']");
		        for( var i=0; i<a.length; i++ ){
		        	a[i].checked = true;
		        }
		    } else {  //全不选
		        $("#toggle-checkboxes_up").prop("checked", false);  
		        var a = $("input[name='checkItems']");
		        for( var j=0; j<a.length; j++ ){
		        	a[j].checked = false;
		        }
		    }  
		    TTCart.refreshTotalPrice();
		});
	},
	setSelectAll : function(){//子复选框的事件
		$("input[type='checkbox'][name='checkItems']").click(function() {
			var chsub = $("input[type='checkbox'][name='checkItems']").length; //获取subcheck的个数  
		    var checkedsub = $("input[type='checkbox'][name='checkItems']:checked").length; //获取选中的subcheck的个数  
		    if (checkedsub == chsub) {  
		        $("#toggle-checkboxes_up").prop("checked", true);  
				$("#toggle-checkboxes_down").prop("checked", true);  
		    }
		    else{
		    	$("#toggle-checkboxes_up").prop("checked", false);
				$("#toggle-checkboxes_down").prop("checked", false);
			}
			TTCart.refreshTotalPrice();
		});
	}
};

$(function(){
	TTCart.load();
	TTCart.itemNumChange();
	TTCart.selectAll();
	TTCart.selectAll2();
	TTCart.setSelectAll();
});

function goToOrder(){
		var _pcheckbox = $("input[type='checkbox'][name='checkItems']:checked");
		var ids = [];
		_pcheckbox.each(function(i, element) {
			var _this = $(element);
			ids.push(_this.val().slice(0, -2));
		});
		if(ids.length == 0){
    		alert('未选中商品!');
    	}else{
    		ids = ids.join(",");
    		var itemIds = {"ids":ids};
//    		location.href = "/order/order-cart.html?ids="+itemIds;
			$.post("/cart/checkedItemId.html", itemIds,function(data){
				if(data.status == 200){
					$(".checkboxChanges").html("数据已返回！");
				}
			});
			location.href = "http://localhost:8082/order/order-cart.html";
		}
}
