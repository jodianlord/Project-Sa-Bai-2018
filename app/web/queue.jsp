<%-- 
    Document   : queue
    Created on : 12 Dec, 2018, 9:46:36 AM
    Author     : Jordy
--%>

<%@page import="dao.UserDAO"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="model.Drug"%>
<%@page import="java.util.ArrayList"%>
<%@page import="dao.InventoryDAO"%>
<%@include file="header.jsp" %>


<div class="box box-info box-solid" style="width:50%; margin:0 auto; background-color:white" >
    <div class="box-header with-border">
        <h3 class="box-title">Queue</h3>
        <!-- /.box-tools -->
    </div>

    </br>

    <!-- /.box-header -->
    <div class="box-body">
        <table class="table" id="issueMedicine">
            <tbody id="medBody">
                <tr>
                    <th>#</th>
                    <th>Patient ID</th>
                    <th>Patient Name</th>
                    <th>Timestamp</th>
                    <th>Status</th>
                    <th>Call</th>
                </tr>
            </tbody>
        </table>
        <hr>
    </div>
</div>
<script src="js/jquery.min.js"></script>
<script src="js/queue.js"></script>
<%@include file="footer.jsp" %>