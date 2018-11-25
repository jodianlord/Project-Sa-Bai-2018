/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var streamObj;

document.getElementById("startstream").onclick = function () {
    document.getElementById("startstream").style.visibility = "hidden"; //make invisible
    var video = document.createElement("video"),
            vendorUrl = window.URL || window.webkitURL;
    video.id = "video";
    video.classList.add("col-md-6");
    video.classList.add("col-sm-6");
    video.classList.add("mb");
    document.getElementById("media").innerHTML = "";
    document.getElementById("media").appendChild(video);
    
    navigator.getMedia = navigator.getUserMedia ||
            navigator.webkitGetUserMedia ||
            navigator.mozGetUserMedia ||
            navigator.msGetUserMedia;
    navigator.getMedia({
        video: true,
        audio: false
    }, function (stream) {
        streamObj = stream
        video.srcObject = stream;
        video.play();
    }, function (error) {
        //An error occured
        //error.code
    });

    document.getElementById("capture").style.margin = "-50px 20px 0px 15px";
    document.getElementById("capture").style.visibility = "visible"; //make visible

}

document.getElementById("capture").onclick = function () {
    //var vis = document.getElementById("startstream").style.visibility;
    //console.log(vis);

    canvas = document.createElement("canvas");
    canvas.id = "canvas";
    context = canvas.getContext("2d");
    canvas.width = 200;
//    canvas.style.width = '100%';
    canvas.height = canvas.width * .75;
    context.drawImage(document.getElementById("video"), 0, 0, 200, 200*0.75);
    document.getElementById("media").innerHTML = "";
    document.getElementById("media").appendChild(canvas);
    streamObj.getTracks().forEach(function (track) {
        track.stop();
    });
    
    //document.getElementById("capture").style.margin = "-50px 20px 0px 15px";
    document.getElementById("capture").style.visibility = "hidden"; //make invisible
    
    document.getElementById("captureagain").style.margin = "-60px 0px 0px 0px";
    //document.getElementById("camerapanel").style.height = "45em";
    document.getElementById("captureagain").style.visibility = "visible"; //make visible
    document.getElementById("submitpic").style.visibility = "visible";
}

document.getElementById("captureagain").onclick = function () {
    document.getElementById("captureagain").style.visibility = "hidden";
    document.getElementById("capture").style.visibility = "visible"; //make invisible
    var video = document.createElement("video"),
            vendorUrl = window.URL || window.webkitURL;
    video.id = "video";
    video.classList.add("col-md-6");
    video.classList.add("col-sm-6");
    video.classList.add("mb");
    document.getElementById("media").innerHTML = "";
    document.getElementById("media").appendChild(video);

    navigator.getMedia = navigator.getUserMedia ||
            navigator.webkitGetUserMedia ||
            navigator.mozGetUserMedia ||
            navigator.msGetUserMedia;
    navigator.getMedia({
        video: true,
        audio: false
    }, function (stream) {
        streamObj = stream
        video.srcObject = stream;
        video.play();
    }, function (error) {
        //An error occured
        //error.code
    });

}
