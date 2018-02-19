/* 
 * This JavaScript file provides methods for clearing the
 * canvas and for rendering Red Box Man.
 */

var canvas;
var gc;
var canvasWidth;
var canvasHeight;
var imageLoaded;
var mousePositionRendering;
var redBoxManImage;
var mouseX;
var mouseY;
var imagesRedBoxManLocations;
var shapesRedBoxManLocations;

function Location(initX, initY) {
    this.x = initX;
    this.y = initY;
}

function init() {
    // GET THE CANVAS SO WE CAN USE IT WHEN WE LIKE
    canvas = document.getElementById("red_box_man_canvas");
    gc = canvas.getContext("2d");
        
    // MAKE SURE THE CANVAS DIMENSIONS ARE 1:1 FOR RENDERING
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;
    canvasWidth = canvas.width;
    canvasHeight = canvas.height;
    
        // FOR RENDERING TEXT
    gc.font="32pt Arial";

    // LOAD THE RED BOX MAN IMAGE SO WE CAN RENDER
    // IT TO THE CANVAS WHENEVER WE LIKE
    redBoxManImage = new Image();
    redBoxManImage.onload = function() {
	imageLoaded = true;
    }
    redBoxManImage.src = "./images/RedBoxMan.png";
    
    // BY DEFAULT WE'LL START WITH MOUSE POSITION RENDERING ON
    mousePositionRendering = true;
    
    // HERE'S WHERE WE'LL PUT OUR RENDERING COORDINATES
    imagesRedBoxManLocations = new Array();
    shapesRedBoxManLocations = new Array();
}

function processMouseClick(event) {
    updateMousePosition(event);
    var location = new Location(mouseX, mouseY);
    if (event.shiftKey) {
	shapesRedBoxManLocations.push(location);
	render();
    }
    else if (event.ctrlKey) {
	if (imageLoaded) {
	    imagesRedBoxManLocations.push(location);
	    render();
	}
    }
    else {
	clear();
    }
}

function clearCanvas() {
    gc.clearRect(0, 0, canvasWidth, canvasHeight);
}

function clear() {
    shapesRedBoxManLocations = [];
    imagesRedBoxManLocations = [];
    clearCanvas();
}

function updateMousePosition(event) {
    var rect = canvas.getBoundingClientRect();
    mouseX = event.clientX - rect.left;
    mouseY = event.clientY - rect.top;
    render();
}

function renderShapesRedBoxMan(location) {
    var headColor = "#DD0000";
    var outlineColor = "#000000";
    var headW = 115;
    var headH = 88;
    
    // DRAW HIS RED HEAD
    gc.fillStyle = headColor;
        gc.setFill(Paint.valueOf(headColor));
	gc.fillRect(location.getX(), location.getY(), headW, headH);
        gc.fillStyle = Yellow;
        gc.fillRect(location.getX()+20, location.getY()+10, 30, 30);
        gc.fillRect(location.getX()+70, location.getY()+10, 30, 30);
        gc.fillStyle = Black;
        gc.fillRect(location.getX()+30, location.getY()+20, 10, 10);
        gc.fillRect(location.getX()+80, location.getY()+20, 10, 10);
        gc.fillRect(location.getX()+25, location.getY()+60, 80, 10);
        gc.fillRect(location.getX()+20, location.getY()+headH, headW-30, headH-60);
        gc.fillRect(location.getX()+30, location.getY()+headH+headH-60,65, 20);
        gc.fillRect(location.getX()+20, location.getY()+headH+headH-60+20, 15, 15);
        gc.fillRect(location.getX()+95, location.getY()+headH+headH-60+20, 15, 15);
         gc.beginPath();
    gc.strokeStyle = outlineColor;
    gc.lineWidth = 1;
    gc.rect(location.x, location.y, headW, headH);
    gc.stroke();
    // AND THEN DRAW THE REST OF HIM
}

function renderImageRedBoxMan(location) {
    gc.drawImage(redBoxManImage, location.x, location.y);
}

function renderMousePositionInCanvas(event) {
    if (mousePositionRendering) {
	gc.strokeText("(" + mouseX + "," + mouseY + ")", 10, 50);    
    }
}

function toggleMousePositionRendering() {
    mousePositionRendering = !mousePositionRendering;
}

function render() {
    clearCanvas();
    for (var i = 0; i < shapesRedBoxManLocations.length; i++) {
	var location = shapesRedBoxManLocations[i];
	renderShapesRedBoxMan(location);
    }
    for (var j = 0; j < imagesRedBoxManLocations.length; j++) {
	var location = imagesRedBoxManLocations[j];
	renderImageRedBoxMan(location);
    }
    renderMousePositionInCanvas();
}