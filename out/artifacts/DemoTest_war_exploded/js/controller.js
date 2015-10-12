
<!--controller 与服务端数据交换 视图渲染-->
function queryTrans($http,$scope){

	$scope.pageCount = [];
	$scope.currPage = 1;


	$http.get("../transInfo/transCount").success(function (data) {
		var array = [1];
		for(var i = 1 ; i <=  data;i ++){
			array[i -1] = i;
		}
		$scope.pageCount  = array;


	})

	$scope.carbin = function () {
		$http.get("../transInfo/carBin");
	}
	$scope.transPage = function(p){
		$scope.currPage = p;
		var data = $("#form").serialize();

		var url = "?"+data;
		if(p > 0){
			$scope.currPage = p;
			url += "&page="+$scope.currPage;
		}

		$http(
			{
				url:"../transInfo/transList"+url,
				method:"get"
			}
		).success(function (datas) {
			$scope.trans=datas;
		})

	}
	$scope.transPage();
}

function transDetail($scope,$http,$routeParams){
	$scope.id = $routeParams.id;
	$http.get("../transInfo/transDetail?id="+$scope.id).success(function (data){
		$scope.tranDetail = data;
	})
}





