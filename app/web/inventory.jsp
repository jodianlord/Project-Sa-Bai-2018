<%-- 
    Document   : inventory
    Created on : 02 Dec, 2018, 6:36:36 AM
    Author     : Jordy
--%>

<%@page import="dao.UserDAO"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="model.Drug"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dao.InventoryDAO"%>
<%@include file="header.jsp" %>


<div class="box box-info box-solid" style="width:50%; margin:0 auto;" >
    <div class="box-header with-border">
        <h3 class="box-title">View/Edit Inventory</h3>
        <!-- /.box-tools -->
    </div>

    </br>

    <!-- /.box-header -->
    <div class="box-body">
        <table class="table" id="issueMedicine">
            <tbody id="medBody">
                <tr>
                    <th>#</th>
                    <th>Medicine</th>
                    <th>Quantity</th>
                </tr>
            </tbody>
        </table>
        <hr>
        <div style="float:left">
            <span class="input-group-btn" style="width:0;padding-right:10px">
                <button id="add" class="btn btn-danger btn-flat">Add Entry</button>
            </span>
            <span class="input-group-btn" style="width:0;padding-right:10px">
                <button id="submit" type="submit" class="btn btn-info btn-flat">Update Inventory</button>
            </span>
        </div>
    </div>
</div>
<script src="js/jquery.min.js"></script>
<script src="js/inventory.js"></script>
<%@include file="footer.jsp" %>