/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    $.ajax({
        url: "./InventoryServlet",
        type: "GET",
        contentType: "application/json",
        success: function(resp){
            //console.log(JSON.stringify(resp));
            //console.log(resp.length);
            for(var i = 0; i < resp.length; i++){
                //console.log(resp[i]);
                var obj = resp[i];
                var medName = Object.keys(obj)[0];
                var quantity = obj[medName];
                //console.log(obj);
                var toAppend = "<tr><td>" + (i + 1) + "</td><td>" + medName + "</td><td>";
                toAppend += "<input type=\"number\" value=\"" + quantity + "\"></td></tr>"
                $("#medBody").append(toAppend);
            }
            var addNew = "<tr id=\"newrow\"><td>" + (resp.length + 1) + "</td><td><input type=\"text\" placeholder=\"Input medicine name here\"></td><td><input type=\"number\" placeholder=\"0\"></td></tr>";
            $("#medBody").append(addNew);
            
        }, error: function(xhr){

        }
    });
});

$("#add").click(function(){
    var med = null;
    var quant = null;
    $("#newrow").find('td input').each(function () {
        console.log(this.value);
        if(med == null){
            med = this.value;
        }else{
            quant = this.value;
        }
    });

    $("#newrow").remove();
    var count = $('tbody tr').length;
    console.log(count);
    var toAppend = "<tr><td>" + count + "</td><td>" + med + "</td><td>";
    toAppend += "<input type=\"number\" value=\"" + quant + "\"></td></tr>"
    $("#medBody").append(toAppend)

    var addNew = "<tr id=\"newrow\"><td>" + (count + 1) + "</td><td><input type=\"text\" placeholder=\"Input medicine name here\"></td><td><input type=\"number\" placeholder=\"0\"></td></tr>";
    $("#medBody").append(addNew);
});

$("#submit").click(function(){
    console.log("up yours");
    var updateObj = [];
    $("#medBody").find('tr').each(function(){
        var id = null;
        var med = null;
        var quantity = null;
        $(this).find('td').each(function(){
            if(id == null){
                id = $(this).html();
            }else if(med == null){
                med = $(this).html();
                $(this).find("input").each(function () {
                    med = this.value;
                });
            }else{
                $(this).find("input").each(function(){
                    quantity = this.value;
                });
            }
        });
        if(id != null && med.length != 0){
            var medObj = {};
            medObj.id = id;
            medObj.name = med;
            medObj.quantity = quantity;
            updateObj.push(medObj);
        }
    });
    $.ajax({
        url: "./InventoryServlet",
        type: "GET",
        contentType: "application/json",
        success: function (resp) {
            var insertArr = null;
            var changeArr = null;
            if(updateObj.length > resp.length){
                insertArr = [];
                for(var i = (updateObj.length - 1); i > (resp.length - 1); i--){
                    insertArr.push(updateObj[i]);
                }
            }

            for(var i = 0; i < resp.length; i++){
                var obj = resp[i];
                var medName = Object.keys(obj)[0];
                var quantity = obj[medName];

                if(quantity != updateObj[i].quantity){
                    if(changeArr == null){
                        changeArr = [];
                    }
                    changeArr.push(updateObj[i]);
                }
            }
            var sendObj = {};
            sendObj.insert = insertArr;
            sendObj.change = changeArr;
            console.log(JSON.stringify(sendObj));

            $.ajax({
                url: "./InventoryServlet",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(sendObj),
                success: function(){
                    window.location.href = window.location.href;
                }, error: function(xhr){
                    alert("Update failed!");
                }
            })

        }, error: function (xhr) {

        }
    });
    console.log(updateObj);
});