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

	function convertBase64toCanvas(myImage,width,height){
		var start = new Date().getMilliseconds();
		var mCanvas = newEl("canvas");
		mCanvas.width = width;
		mCanvas.height = height;
		mCanvas.setAttribute('style', "width:"+10*width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*height+"px;"); // make it large enough to be visible
		var mContext = mCanvas.getContext('2d');
      	mContext.drawImage(myImage, 0, 0);
        var end = new Date().getMilliseconds();
		console.log((end-start)+"ms");
		//console.log(mCanvas);


		//var canvas = newEl('canvas');
		//var ctx = canvas.getContext('2d');
		//ctx.drawImage(myImage, 0, 0);
		return mCanvas;
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
		tile.width = width;
		tile.height = height;
		tile.posX = x;
		tile.posY = y;
		tile.zoomLevel = zoomLevel;
		if (config["base64DataUrl"]){
			tile.base64DataUrl = config["base64DataUrl"];
		}
		else {
			var defaultColor = config["defaultColor"];
			pixelsArray = initialize_pixelsArray({width: width,height: height,defaultColor: defaultColor});
			tile.base64DataUrl = convertPixelsArrayToCanvas(pixelsArray,tile.width,tile.height).convertToBase64();
		}
		if (config["id"]){
			id = config["id"];
			tile.id = id;
		}
		

		tile.serializePixels = function(){
			//console.log(JSON.stringify(this.pixelsArray).length*4+"bytes");
			return JSON.stringify(this.pixelsArray);
		}

		tile.insert = function(db){
			db.insertTileData(this.posX, this.posY, this.zoomLevel,this.base64DataUrl);
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
			console.log(dbObj["base64DataUrl"]);
			return initialize_tile({id:dbObj["id"],x:dbObj["x"], y:dbObj["y"], zoomLevel:dbObj["zoomLevel"],width:7,height:7, base64DataUrl: dbObj["base64DataUrl"] });
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
				pixelsArray[i] = defaultColor;//[Math.floor(Math.random()*256),Math.floor(Math.random()*256),Math.floor(Math.random()*256) ]//defaultColor;


			}
		}
		return pixelsArray;
	}

	function render(config,dispatcher){
			var tile = config["tile"];	
			var myImage = new Image();
			myImage.setAttribute("src", tile.base64DataUrl);
			myImage.onload = function() {
				var mCanvas = convertBase64toCanvas(myImage,tile.width,tile.height);
				var start = new Date().getMilliseconds();
				mCanvas.setAttribute('style', "width:"+10*this.width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*this.height+"px;"); // make it large enough to be visible
				document.body.appendChild( mCanvas );
				//var end = new Date().getMilliseconds();
				dispatcher.check();
			}
		}

	function addSample(config,dispatcher){
			var tile = config["tile"]
			var sample_list = config["sample"]
			var samples_available = config["num_samples"];
			var which_sample = config["which_sample"];
			convertBase64ToPixelsArray(tile,function(pixelsArray){
				var counter = 0;
				var position = 0;
				var tile_pixels = tile.width*tile.height;
				var sample_pixels = sample_list.length;
				var sample_offset = which_sample - 1;
				for (var i = 0; i<sample_pixels; i++){
					if (position+sample_offset>tile_pixels){
						//TO INVERSE
					}
					else {
						pixelsArray[position+sample_offset] = sample_list[i];
						position += samples_available;
						counter++;
					}
				}
				tile.base64DataUrl = convertPixelsArrayToCanvas(pixelsArray,tile.width,tile.height).convertToBase64();
				
				dispatcher.check();

			});
		}

	function convertBase64ToPixelsArray(tile,whatToDoNext){
		var base64DataUrl = tile.base64DataUrl;
		var mCanvas = newEl("canvas");
		var ctx = mCanvas.getContext("2d");
		var pixelsArray = [];

		var img = newEl("img");
		img.onload = getRGB;
		img.src = base64DataUrl;

		function getRGB(){
			mCanvas.width = img.width;
			mCanvas.height = img.height;
			ctx.drawImage(img,0,0);
			var data = ctx.getImageData(0,0,mCanvas.width, mCanvas.height).data;
			for (var i=0; i<data.length; i+=4){
				pixelsArray.push([data[i],data[i+1],data[i+2]]);
			}
			//console.log(pixels);
			whatToDoNext(pixelsArray);
			//$(base64DataUrl).trigger(whatToDoNext);
		}
	}

	



	$(document).ready(function() {
		db = createDatabaseIfNotExists(':memory:',2*1024*1024);
		db.dropTilesTableIfExists();
		db.createTilesTableIfNotExists();
		
		var msg;

		var sample = [[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f]]
		var tile = initialize_tile({width: 7,height: 7,defaultColor: [0x00,0xAA,0x00], x: 1, y: 2, zoomLevel: 1});
		
		var dispatcher = { };
		dispatcher.current = 0;
		dispatcher.total = 0 ;
		dispatcher.next = function(){
			this.current++;
		}
		dispatcher.hasMore = function(){
			return dispatcher.current<dispatcher.total;
		}
		dispatcher.addNew = function(todo){
			this.total++;
			this[this.total] = todo;
			return this;
		}
		dispatcher.exec = function(){
			this[this.current].func(this[this.current].config,this);	
		}

		dispatcher.check = function(){
			if (this.hasMore()){
				dispatcher.next();
				dispatcher.exec();
			}
		}

		dispatcher.addNew({func: addSample, config: {tile: tile,sample: sample, num_samples: 6, which_sample: 1}});
		dispatcher.addNew({func: render,config: {tile: tile}});
		dispatcher.addNew({func: addSample, config: {tile: tile,sample: sample, num_samples: 6, which_sample: 2}});
		dispatcher.addNew({func: render,config: {tile: tile}});
		dispatcher.addNew({func: addSample, config: {tile: tile,sample: sample, num_samples: 6, which_sample: 3}});
		dispatcher.addNew({func: render,config: {tile: tile}});
		dispatcher.addNew({func: addSample, config: {tile: tile,sample: sample, num_samples: 6, which_sample: 4}});
		dispatcher.addNew({func: render,config: {tile: tile}});
		dispatcher.addNew({func: addSample, config: {tile: tile,sample: sample, num_samples: 6, which_sample: 5}});
		setInterval(function(){dispatcher.check();},1000);
		
		//dispatcher.start();	



		//disp.func(disp.config,dispatcher);
		/*addSample(tile,sample,6,1);
		addSample(tile,sample,6,2);
		addSample(tile,sample,6,3);
		addSample(tile,sample,6,4);
		addSample(tile,sample,6,5);
		addSample(tile,sample,6,6);*/
		//tile.addSample(sample,6,1);
		/*var callbacks = $.Callbacks();
		callbacks.add(function(){tile.addSample(sample,6,1)});
		callbacks.fire();
		callbacks.add(function(){tile.insert(db)});
		callbacks.fire();
		callbacks.add(function(){tile.addSample(sample,6,2)});
		callbacks.fire();
		callbacks.add(function(){tile.insert(db)});
		callbacks.fire();
		callbacks.add(function(){ db.fetchTileWithId(1,extractTile,true); });
		callbacks.fire();*/
		var exp = new Array(8);
		for (var i = 0; i < 8; i++) {
            exp[i] = new Array(8);
        }
		String.prototype.padRight = function(c, n) {
		    var txt = '';
		    for (var i = 0; i < n - this.length; i++) txt += c;
		    return txt + this;
		};

		// function myPNG(data){
		//     var reader = new Base64Reader(data);
		//     reader.skip(8);
		//     var length = reader.readInt();
		//     var type = reader.readChars(4);
		//     var data = [];
		//     console.log("bika");
		//     if (reader.read(data, 0, length) != length){
		//         throw 'Out of bounds';
		//     }
		//     reader.skip(4);
		//     return { type: type, data: data };
		// }


		function show(base64Data,width,height){
		    var png = myPNG(base64Data); // data is the base64 encoded data
			//var reader = new Base64Reader(base64Data);
  			console.log(png);

			var line;
			var byte;
			var y = 0;
			var pixelCount = 0;
			var pixelsArray = new Array();
			//byte = reader.readByte();
			//console.log(byte);
			/*while(byte = reader.readByte()){
				console.log(byte);
			}*/
			/*while(line = png.readLine())
			{
			    console.log(line);
			    //pixelsArray[y] = new Array(line.length);
			    for (var x = 0; x < line.length; x++){
			    	

			    	//var hexColor = '#' + line[x].toString(16).padRight('0',6);
			    	//1var rgbColor = hexToRgb(hexColor);
			    	
			    	var pixel = doSomethingWithPixelData(x, y, '#' + line[x].toString(16).padRight('0', 6))
			        //pixelsArray[pixelCount][0] = rgbColor.r.toString(16);
			        //pixelsArray[pixelCount][1] = rgbColor.g.toString(16);
			        //pixelsArray[pixelCount][2] = rgbColor.b.toString(16);
			        //console.log("~~~"+pixelsArray[pixelCount]);
			    	//pixelCount++;	
			    }
			    
			    y++;
			}*/
			//console.log("~~~"+pixelsArray[pixelCount]);
			/*function doSomethingWithPixelData(color) {
			    // guess what? do something with pixel data here ;)
			}*/
			return pixelsArray;
		}

		function doSomethingWithPixelData(x, y, color) {
		    exp[x][y] = color;
		}

		function hexToRgb(hex) {
			console.log(hex);
		    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
		    return result ? {
		        r: parseInt(result[1],16),
		        g: parseInt(result[2],16),
		        b: parseInt(result[3],16)
		    } : null;
		}
		//var pixelsArray = PNG("iVBORw0KGgoAAAANSUhEUgAAAAcAAAAHCAYAAADEUlfTAAAAFElEQVQIW2NkYGD4D8RYAeOQkgQAERQHAbuZaGoAAAAASUVORK5CYII=",7,7);
		
		//tile.base64DataUrl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAcAAAAHCAYAAADEUlfTAAAAFElEQVQIW2NkYGD4D8RYAeOQkgQAERQHAbuZaGoAAAAASUVORK5CYII=";
		
	
			/*var base64DataUrl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAcAAAAHCAYAAADEUlfTAAAAE0lEQVQIW2Osr6//z4ADMA4pSQC09hFsUmxH9AAAAABJRU5ErkJggg==";
			//var base64DataUrl = this.base64DataUrl;
			var myImage = new Image();
			myImage.setAttribute("src", base64DataUrl);
			myImage.onload = function() {
				var pixelsArray = convertCanvasToPixelsArray(convertBase64toCanvas(myImage,7,7));
				//console.log(pixelsArray);
				//$("body").append(canvas);
			}*/
		
		

		//var base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAcAAAAHCAYAAADEUlfTAAAAFElEQVQIW2NkYGD4D8RYAeOQkgQAERQHAbuZaGoAAAAASUVORK5CYII=";
		console.log("max:"+Math.ceil(7*7 / 6));
		//db.fetchAllTiles(extractTile);

	});
});

function doSomethingWithPixels(tile,params){
   convertBase64ToPixelsArray(tile,function(pixelsArray){
	//manipulate the pixels 
	//convert them back to base64 and store it in the tile
	tile.base64UrlData = convertPixelsArrayToCanvas(pixelsArray,tile.width,tile.height).convertToBase64();
	});
}



