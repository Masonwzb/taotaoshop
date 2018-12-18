//作者：HXL，如有疑问，可以QQ：991044981


//分页控件单击事件
function pageClick(callBack) {
    $("div.pagin a").click(function () {
        var id = $(this).attr("id");
        if ($("#"+id).html() == pageIndex) {
            return;
        }
        if (id == "prev") {
            pageIndex--;
        }
        else if (id == "next") {
            pageIndex++;
        }
        else {
            pageIndex = $("#"+id).html();
        }
        pageIndex = parseInt(pageIndex);
        if (pageIndex < 1 || pageIndex > pageCount) {
            return;
        }
        callBack();
    });
}

//刷新分页控件
function pageRefresh(callBack) {
    $("#pagedes").html("共<a>" + recordCount + "</a>条数据");
    if (recordCount > 0) {
        if (recordCount % pageSize == 0) {
            pageCount = recordCount / pageSize;
        }
        else {
            pageCount = (recordCount - recordCount % pageSize) / pageSize + 1;
        }
        pageIndex = parseInt(pageIndex);
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        else if (pageIndex > pageCount) {
            pageIndex = pageCount;
        }
        var pagelist = "";
        if (pageIndex == 1) {
            pagelist = "<span  class='prev-disabled'>上一页<b></b></span> " + "<a id='1'>1</a> ";
        }
        else {
            pagelist = "<a id='prev' class='prev' href='http://localhost:8082/order/list.html?pageIndex="+(pageIndex-1)+"&pageSize="+pageSize+"'>上一页<b></b></a> " + "<a id='1' href='http://localhost:8082/order/list.html?pageIndex=1"+"&pageSize="+pageSize+"'>1</a> ";
        }
        if (pageIndex - 2 > 2) {
            pagelist += "<span class='text'>...</span> ";
        }
        if (1 < pageIndex - 2 && pageIndex - 2 < pageCount) {
            pagelist += "<a id='" + (pageIndex - 2) + "' href='http://localhost:8082/order/list.html?pageIndex="+(pageIndex-2)+"&pageSize="+pageSize+"'>" + (pageIndex - 2) + "</a> ";
        }
        if (1 < pageIndex - 1 && pageIndex - 1 < pageCount) {
            pagelist += "<a id='" + (pageIndex - 1) + "' href='http://localhost:8082/order/list.html?pageIndex="+(pageIndex-1)+"&pageSize="+pageSize+"'>" + (pageIndex - 1) + "</a> ";
        }
        if (1 < pageIndex && pageIndex < pageCount) {
            pagelist += "<a id='" + pageIndex + "'>" + pageIndex + "</a> ";
        }
        for (var i = pageIndex + 1; i < pageCount && i <= pageIndex + 2; i++) {
            pagelist += "<a id='" + i + "' href='http://localhost:8082/order/list.html?pageIndex="+ i +"&pageSize="+pageSize+"'>" + i + "</a> ";
        }
        if (pageIndex + 3 < pageCount) {
            pagelist += "<span class='text'>...</span> ";
        }
        if (pageIndex < pageCount) {
            pagelist += "<a id='" + pageCount + "' href='http://localhost:8082/order/list.html?pageIndex="+pageCount+"&pageSize="+pageSize+"'>" + pageCount + "</a> " + "<a id='next' href='http://localhost:8082/order/list.html?pageIndex="+(pageIndex + 1)+"&pageSize="+pageSize+"' class='next'>下一页<b></b></a>";
        }
        else if (pageIndex > 1) {
            pagelist += "<a id='" + pageCount + "'>" + pageCount + "</a> " + "<span class='next-disabled'>下一页<b></b></span>";
        }
        else {
            pagelist += "<span class='next-disabled'>下一页<b></b></span>";
        }
        $("#page").html(pagelist);
        $("#" + pageIndex).addClass("current");
        pageClick(callBack);
    }
    else {
        $("#page").empty();
    }
}



