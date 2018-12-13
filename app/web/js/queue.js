/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function(){
    $.ajax({
        type: "GET",
        url: "./QueueServlet",
        success: function(resp){
            console.log(resp);
            for(var i = 0; i < resp.length; i++){
                obj = resp[i];
                $("#medBody").append("<tr><td>" + (i + 1) + "</td><td>" + obj.patientID + "</td><td>" +
                        obj.name + "</td><td>" + obj.timestamp + "</td><td>" + obj.status + "</td><td>" +
                        "<button type='submit' class='btn btn-info btn-flat' onClick=callPatient(" + obj.visitID + ")>Call Patient</button></td></tr>");
            }
        }, error(xhr){
            console.log("fail");
        }
    });
})

function callPatient(visitID){
    console.log(visitID);
    $.ajax({
        type: "POST",
        url: "./QueueServlet",
        data: {
            visitID: visitID
        },
        success: function(resp){
            
        }, error(xhr){
            
        }
    })
}