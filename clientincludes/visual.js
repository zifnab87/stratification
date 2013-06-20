$(function() {

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

			function newEl(tag){
				return document.createElement(tag);
			}

			function createImageFromRGBdata(pixelsArray, width, height){
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
				return mCanvas;
			}
			// 1. - append data as a canvas element
			var mCanvas = createImageFromRGBdata(this.pixelsArray, this.width, this.height);
			mCanvas.setAttribute('style', "width:"+10*this.width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*this.height+"px;"); // make it large enough to be visible
			document.body.appendChild( mCanvas );
			/*// 2 - append data as a (saveable) image
			var mImg = newEl("img");
			var imgDataUrl = mCanvas.toDataURL();	// make a base64 string of the image data (the array above)
			mImg.src = imgDataUrl;
			mImg.setAttribute('style', "width:4px; height:4px;"); // make it large enough to be visible
			document.body.appendChild(mIpixelsarraymg);*/
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
			return JSON.stringify(this.pixelsArray);
		}
		tile.insert = function(db){
			db.insertTileData(this.posX, this.posY, this.zoomLevel,this.serializePixels());
		}

		tile.updatePixels = function(db){
			console.log(tile["id"]);
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

		console.log("max:"+Math.ceil(7*7 / 6));
		//db.fetchAllTiles(extractTile);
	});
});
