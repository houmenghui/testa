
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <form class="form-inline" id="form" role="form">
        <div class="form-group">
            <label>参考号</label>
            <input type="text" name="referenceNo"  id="referenceNo"
                   placeholder="请输入">
        </div>
        <div class="form-group">
            <label>商户编号</label>
            <input type="text"  name="merchantno"  id="merchantno"
                   placeholder="请输入">
        </div>
        <div class="form-group">
            <label>交易时间</label>
            <input type="text"  id="transDateBegin" name="transDateBegin"
                   placeholder="请输入">~<input type="text" id="transDateEnd" name="transDateEnd"
                                             placeholder="请输入">

        </div>

        <div class="form-group">
            <label>商户编号</label>
            <input type="button" class="btn btn-primary"  id="sub"
                  ng-click="transPage(0)" value="提交">
            <input type="button" class="btn btn-primary"  id="carbin"
                   ng-click="carbin()" value="提交">
        </div>

    </form>
</div>
<a onclick="dianwo()" class="btn btn-default">dianwo</a>
<div style="width: 800px;">
    <table  class="table table-striped">
        <tr>
            <td>id</td>
            <td>金额</td>
            <td>时间</td>
            <td>卡号</td>
            <td>商户号</td>
            <td>参考号</td>
            <td>操作</td>
        </tr>

        <tr ng-repeat="tran in trans">
            <td>{{tran.id}}</td>
            <td>{{tran.trans_amount}}</td>
            <td>{{tran.trans_time}}</td>
            <td>{{tran.account_no}}</td>
            <td>{{tran.merchant_no}}</td>
            <td>{{tran.acq_reference_no}}</td>
            <td><a href="../transInfo/default#/transDetail/{{tran.id}}">详情</a></td>
        </tr>


    </table>



    <script type="text/javascript">
       function dianwo(){
           alert(123)
           $.ajax({
               url:"../transInfo/transList?page=2",
               success:function(data){
                   alert(data[1].id)
                   var da = $.parseJSON(data);
                   console.log(da);
                   console.log(da[1].id);

               }

           })
       }
    </script>

   <%-- <ul class="pagination" >

        <li><a>首页</a></li>
        <li><a >上一页</a></li>
        <li ng-repeat="p in pageCount" ng-class="{active:currPage==p}" ng-click="transPage(p)"><a>{{p}}</a></li>
        <li><a>下一页</a></li>

        <li><a>尾页</a></li>
    </ul>--%>

    <div hello>

    </div>







</div>
