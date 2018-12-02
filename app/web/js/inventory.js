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
            
        }, error: function(xhr){

        }
    });
});