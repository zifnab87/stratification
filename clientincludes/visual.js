$(function() {

	function initialize_tile(config){
		var x = config["x"];
		var y = config["y"];
		var width = config["width"];
		var height = config["height"];
		var defaultColor = config["defaultColor"];
		var zoomLevel = config["zoomLevel"];
		var rgbData = initialize_rgbData({width: width,height: height,defaultColor: defaultColor});
		var tile = new Object();
		tile.width = width;
		tile.height = height;
		tile.posX = x;
		tile.posY = y;
		tile.zoomLevel = zoomLevel;
		tile.rgbData = rgbData;
		tile.render = function(){

			function newEl(tag){
				return document.createElement(tag);
			}

			function createImageFromRGBdata(rgbData, width, height){
				var mCanvas = newEl('canvas');
				mCanvas.width = width;
				mCanvas.height = height;
				
				var mContext = mCanvas.getContext('2d');
				var mImgData = mContext.createImageData(width, height);
				
				var srcIndex=0, dstIndex=0, curPixelNum=0;
				
				for (curPixelNum=0; curPixelNum<width*height;  curPixelNum++)
				{
					mImgData.data[dstIndex] = rgbData.pixelsarray[srcIndex][0];		// r
					mImgData.data[dstIndex+1] = rgbData.pixelsarray[srcIndex][1];	// g
					mImgData.data[dstIndex+2] = rgbData.pixelsarray[srcIndex][2];	// b
					mImgData.data[dstIndex+3] = 255; // 255 = 0xFF - constant alpha, 100% opaque
					srcIndex += 1;
					dstIndex += 4;
				}
				mContext.putImageData(mImgData, 0, 0);
				return mCanvas;
			}
			// 1. - append data as a canvas element
			var mCanvas = createImageFromRGBdata(this.rgbData, this.width, this.height);
			mCanvas.setAttribute('style', "width:"+10*this.width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*this.height+"px;"); // make it large enough to be visible
			document.body.appendChild( mCanvas );
			/*// 2 - append data as a (saveable) image
			var mImg = newEl("img");
			var imgDataUrl = mCanvas.toDataURL();	// make a base64 string of the image data (the array above)
			mImg.src = imgDataUrl;
			mImg.setAttribute('style', "width:4px; height:4px;"); // make it large enough to be visible
			document.body.appendChild(mImg);*/
		}
		tile.addSample = function(sample_list,sample_spacing,sample_offset){
			this.rgbData.addSample(sample_list,sample_spacing,sample_offset);
		}
		tile.serializePixels = function(){
			console.log(JSON.stringify(this.rgbData.pixelsarray));
			return JSON.stringify(this.rgbData.pixelsarray);
		}
		tile.insert = function(db){
			db.insertTileData(tile.posX, tile.posY, tile.zoomLevel,tile.serializePixels());
		}

		
		return tile;
		//var likelihood = config["likelihood"];
	}

	function extractFromDB(dbobj){
			console.log(dbobj["x"]);
	}


	function initialize_rgbData(config){ //if num_of_samples is 1 then there is no sampling
		var width = config["width"];
		var height = config["height"];
		var defaultColor = config["defaultColor"];
		var rgbData = new Object();
		//rgbData.width = width;
		//rgbData.height = height;
		rgbData.pixelsarray = new Array(width*height);
		for (var i = 0; i < rgbData.pixelsarray.length; i++){
			if (!defaultColor){
				rgbData.pixelsarray[i] = []
			}
			else {
				rgbData.pixelsarray[i] = defaultColor;
			}
		}


		rgbData.addSample = function(sample_list,sample_spacing,sample_offset){
			var counter = 0;
			var position = 0;
			var num_of_pixels = this.width*this.height;
			var num_of_samples = sample_list.length;
			for (var i = 0; i<num_of_samples; i++){
				if (position+sample_offset>num_of_pixels){
					return this;
				}
				this.pixelsarray[position+sample_offset]=sample_list[i];
				position += sample_spacing;
				counter++;
			}
		}

		

		return rgbData;
	}


		$(document).ready(function() {
		//var flat_pixel_array = initialize(10,400,300);
		//console.log(flat_pixel_array);
		//console.log($.xcolor.average('#ff7f00','#007fff'));
		//mInit();
		//var rgbData = initialize_rgbData({width: 7,height: 7,default_color: [0xff,0x00,0x00]});
		var sample = [[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f]]
		
		var tile = initialize_tile({width: 7,height: 7,defaultColor: [0xff,0x00,0x00], x: 1, y: 2, zoomLevel: 1});
		//console.log(tile);
		//  console.log(JSON.stringify(tile));
		//tile.render();
		//tile.addSample(sample,6,0);
		//tile.render();
		//tile.addSample(sample,6,1);
		//tile.render();
		/*tile.addSample(sample,6,2);
		tile.render();
		tile.addSample(sample,6,3);
		tile.render();
		tile.addSample(sample,6,4);
		tile.render();
		tile.addSample(sample,6,5);
		tile.render();*/

		console.log("max:"+Math.ceil(7*7 / 6));


		  //var db = SQLite({ shortName: ':memory:', defaultErrorHandler: fail, defaultDataHandler: pass });
		  //db.createTable('tiles', 'id INTEGER, tileObj TEXT');
		 // db.insert('tiles',{id: 2, tileObj: JSON.stringify(tile)  });
		  //db.select('tiles','*', null, function (r, q) { cosole.log("bika"); pass(r, q); console.log(r); var x; for(x=0; x<r.rows.length; x++) { console.log(r.rows.item(x)); }});
		  //db.destroy('tiles');

		  /*db.insert('people', { name: "Jeremy", age: 29 });
		  db.insert('people', { name: "Tara", age: 28 });

		  db.update('people', { age: 30 }, { name: 'Jeremy' });

		  db.select('people', '*', { age: 30 }, null, function (r, q) { pass(r, q); var x; for(x=0; x<r.rows.length; x++) { console.log(r.rows.item(x)); } });
		  db.select('people', 'name', null, { order: 'age DESC' }, function (r, q) { pass(r, q); var x; for(x=0; x<r.rows.length; x++) { console.log(r.rows.item(x)); } });
		  db.select('people', 'name', null, { limit: 1 }, function (r, q) { pass(r, q); var x; for(x=0; x<r.rows.length; x++) { console.log(r.rows.item(x)); } });

		  db.destroy('people', { age: 30 });*/
		//MAXIMUM sample needed is Math.up(width_pix*height_pix / sample_spacing)
		//
		//console.log(tile);

		//var db = openDatabase(':memory:', '1.0', 'Test DB', 2 * 1024 * 1024);
		var db = createDatabaseIfNotExists(':memory:',2*1024*1024);
		db.createTilesTableIfNotExists();
		//db.dropTilesTableIfExists();
		var msg;
		//var pixels = tile.serializePixels();
		//console.log(str);
		
		tile.insert(db);


		/*db.transaction(function (tx) {
		  tx.executeSql('DROP TABLE Tiles',[]);
		});*/
		
		db.transaction(function (tx) {
		  tx.executeSql('SELECT * FROM Tiles', [], function (tx, results) {
		   var len = results.rows.length, i;
		   msg = "<p>Found rows: " + len + "</p>";
		   document.querySelector('#status').innerHTML +=  msg;
		   for (i = 0; i < len; i++){
		   	 row = results.rows.item(i);
		   	 console.log(row);
		   	 //var array = JSON.parse(msg)
		     msg = "<p><b>" + row.pixelsarray + "</b></p>";
		     document.querySelector('#status').innerHTML +=  msg;
		   }
		 }, null);
		});
		//var json = JSON.parse('{"width":7,"height":7}');
		//console.log(json);
		/*db.transaction(function (tx) {
		  tx.executeSql('CREATE TABLE IF NOT EXISTS LOGS (id, log)');
		  tx.executeSql('INSERT INTO LOGS (id, log) VALUES (333, "'+str+'")');
		  //tx.executeSql('INSERT INTO LOGS (id, log) VALUES (2, "logmsg")');
		  msg = '<p>Log message created and row inserted.</p>';
		  document.querySelector('#status').innerHTML =  msg;
		});
		*/
	});
});
