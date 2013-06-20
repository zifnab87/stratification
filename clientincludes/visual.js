$(function() {

	function newEl(tag){
			return document.createElement(tag);
	}

	function convertPixelsArrayToCanvas(pixelsArray, width, height){
		
		var mCanvas = newEl('canvas');
		mCanvas.width = width;
		mCanvas.height = height;
		
		var mContext = mCanvas.getContext('2d');
		var mImgData = mContext.createImageData(width, height);
		
		var srcIndex=0, dstIndex=0, curPixelNum=0;
		
		for (curPixelNum=0; curPixelNum<width*height;  curPixelNum++)
		{
			mImgData.data[dstIndex] = pixelsArray[srcIndex][0];		// r
			mImgData.data[dstIndex+1] = pixelsArray[srcIndex][1];	// g
			mImgData.data[dstIndex+2] = pixelsArray[srcIndex][2];	// b
			mImgData.data[dstIndex+3] = 255; // 255 = 0xFF - constant alpha, 100% opaque
			srcIndex += 1;
			dstIndex += 4;
		}
		mContext.putImageData(mImgData, 0, 0);

		mCanvas.convertToBase64 = function(){
			return (String)(this.toDataURL());
		}
		return mCanvas;
	}

	function convertBase64toCanvas(base64DataUrl,width,height){
		var start = new Date().getMilliseconds();
		var myImage = new Image();	
		myImage.src = base64DataUrl;
		var mCanvas = newEl("canvas");
		mCanvas.width = width;
		mCanvas.height = height;
		mCanvas.setAttribute('style', "width:"+10*width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*height+"px;"); // make it large enough to be visible
		var mContext = mCanvas.getContext('2d');
		myImage.onload = function() {
          	mContext.drawImage(myImage, 0, 0);
			console.log(convertCanvasToPixelsArray(mCanvas));
        };
        var end = new Date().getMilliseconds();
		console.log((end-start)+"ms");
		console.log(mCanvas);


		//var canvas = newEl('canvas');
		//var ctx = canvas.getContext('2d');
		//ctx.drawImage(myImage, 0, 0);
	
	}

	function convertCanvasToPixelsArray(mCanvas){
		var mContext = mCanvas.getContext('2d');
		console.log(mCanvas.width);
		var mImgData = mContext.getImageData(0,0,mCanvas.width,mCanvas.height);
		
		var srcIndex=0, dstIndex=0, curPixelNum=0;
		var pixelsArray = new Array(mCanvas.width*mCanvas.height);
		for (curPixelNum=0; curPixelNum<mCanvas.width*mCanvas.height;  curPixelNum++)
		{
			pixelsArray[dstIndex] = new Array(3);
			pixelsArray[dstIndex][0] = mImgData.data[srcIndex];	// r
			pixelsArray[dstIndex][1] = mImgData.data[srcIndex+1];	// g
			pixelsArray[dstIndex][2] = mImgData.data[srcIndex+2];	// b
			//mImgData.data[dstIndex+3] = 255; // 255 = 0xFF - constant alpha, 100% opaque
			dstIndex += 1;
			srcIndex += 4;
		}
		return pixelsArray;
	}

	function initialize_tile(config){
		var x = config["x"];
		var y = config["y"];
		var width = config["width"];
		var height = config["height"];
		
		var zoomLevel = config["zoomLevel"];
		var pixelsArray;
		var tile = new Object();
		if (config["pixelsArray"]){
			pixelsArray = config["pixelsArray"];
		}
		else {
			var defaultColor = config["defaultColor"];
			pixelsArray = initialize_pixelsArray({width: width,height: height,defaultColor: defaultColor});
		}
		if (config["id"]){
			id = config["id"];
			tile.id = id;
		}
		
		
		tile.width = width;
		tile.height = height;
		tile.posX = x;
		tile.posY = y;
		tile.zoomLevel = zoomLevel;
		tile.pixelsArray = pixelsArray;

		tile.render = function(){

			var start = new Date().getMilliseconds();
			// 1. - append data as a canvas element
			var mCanvas = convertPixelsArrayToCanvas(this.pixelsArray, this.width, this.height);
			mCanvas.setAttribute('style', "width:"+10*this.width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*this.height+"px;"); // make it large enough to be visible
			document.body.appendChild( mCanvas );
			var end = new Date().getMilliseconds();
			//console.log((end-start)+" ms");
			//console.log("edw"+mCanvas.convertToBase64().length*4+"bytes");
			console.log(mCanvas.convertToBase64());
			

			return mCanvas;
		}

		tile.addSample = function(sample_list,samples_available,which_sample){
			var counter = 0;
			var position = 0;
			var tile_pixels = this.width*this.height;
			var sample_pixels = sample_list.length;
			var sample_offset = which_sample - 1;
			for (var i = 0; i<sample_pixels; i++){
				if (position+sample_offset>tile_pixels){;
					return;
				}
				this.pixelsArray[position+sample_offset] = sample_list[i];
				position += samples_available;
				counter++;
			}
			
		}

		tile.serializePixels = function(){
			//console.log(JSON.stringify(this.pixelsArray).length*4+"bytes");
			return JSON.stringify(this.pixelsArray);
		}

		tile.insert = function(db){
			db.insertTileData(this.posX, this.posY, this.zoomLevel,this.serializePixels());
		}

		tile.updatePixels = function(db){
			//console.log(tile["id"]);
			db.updateTilePixelDataWithId(this["id"], this.serializePixels());
		}

		tile.setId = function(id){
			tile.id = id;
		}

		
		return tile;
		//var likelihood = config["likelihood"];
	}

	function extractTile(dbObj){
			return initialize_tile({id:dbObj["id"],x:dbObj["x"], y:dbObj["y"], zoomLevel:dbObj["zoomLevel"],width:7,height:7, pixelsArray: JSON.parse(dbObj["pixelsArray"]) });
	}


	function initialize_pixelsArray(config){ //if num_of_samples is 1 then there is no sampling
		var width = config["width"];
		var height = config["height"];
		var defaultColor = config["defaultColor"];
		var pixelsArray = new Array(width*height);
		//rgbData.width = width;
		//rgbData.height = height;
		for (var i = 0; i < pixelsArray.length; i++){
			if (!defaultColor){
				pixelsArray[i] = []
			}
			else {
				pixelsArray[i] = defaultColor;
			}
		}
		return pixelsArray;
	}


	$(document).ready(function() {
		var db = createDatabaseIfNotExists(':memory:',2*1024*1024);
		db.dropTilesTableIfExists();
		db.createTilesTableIfNotExists();
		
		var msg;

		var sample = [[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f]]
		
		var tile = initialize_tile({width: 7,height: 7,defaultColor: [], x: 1, y: 2, zoomLevel: 1});
		//tile.insert(db);
		//tile.addSample(sample,6,0);
		//tile.insert(db);
		tile.insert(db);



		db.fetchTileWithId(1,extractTile,true);
		tile.setId(1);
		tile.addSample(sample,6,1);
		tile.updatePixels(db);
		db.fetchTileWithId(1,extractTile,true);
		tile.addSample(sample,6,2);
		tile.updatePixels(db);
		db.fetchTileWithId(1,extractTile,true);
		tile.addSample(sample,6,3);
		tile.updatePixels(db);
		db.fetchTileWithId(1,extractTile,true);
		tile.addSample(sample,6,4);
		tile.updatePixels(db);
		db.fetchTileWithId(1,extractTile,true);
		tile.addSample(sample,6,5);
		tile.updatePixels(db);
		db.fetchTileWithId(1,extractTile,true);
		tile.addSample(sample,6,6);
		tile.updatePixels(db);
		db.fetchTileWithId(1,extractTile,true);
		//tile.insert(db);
		//tile.addSample(sample,6,2);
		//tile.insert(db);
		//tile.addSample(sample,6,3);
		//tile.insert(db);
		//tile.addSample(sample,6,4);
		//tile.insert(db);
		//tile.addSample(sample,6,5);
		//tile.insert(db);

		//db.fetchTileWithId(3,extractTile);
		//db.fetchTileWithPosition(1,2,extractTile);
		var canvas = convertBase64toCanvas("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAcAAAAHCAYAAADEUlfTAAAAE0lEQVQIW2Osr6//z4ADMA4pSQC09hFsUmxH9AAAAABJRU5ErkJggg== ",7,7)
		$("body").append(canvas);
		//var pix_array = convertCanvasToPixelsArray(canvas);
		console.log("max:"+Math.ceil(7*7 / 6));
		//db.fetchAllTiles(extractTile);

	});
});
