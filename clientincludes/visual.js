$(function() {

	function initialize_tile(config){
		var x = config["x"];
		var y = config["y"];
		var width = config["width"];
		var height = config["height"];
		var default_color = config["default_color"];
		var zoom_level = config["zoom_level"];
		var rgbData = initialize_rgbData({width: width,height: height,default_color: default_color});
		var tile = new Object();
		tile.width = rgbData.width;
		tile.height = rgbData.height;
		tile.rgbData = rgbData;
		tile.render = function(){
			this.rgbData.render();
		}
		tile.addSample = function(sample_list,sample_spacing,sample_offset){
			this.rgbData.addSample(sample_list,sample_spacing,sample_offset);
		}
		return tile;
		//var likelihood = config["likelihood"];
	}


	function initialize_rgbData(config){ //if num_of_samples is 1 then there is no sampling
		var width = config["width"];
		var height = config["height"];
		var default_color = config["default_color"];
		var rgbData = new Object();
		rgbData.width = width;
		rgbData.height = height;
		rgbData.pixelsarray = new Array(width*height);
		for (var i = 0; i < rgbData.pixelsarray.length; i++){
			if (!default_color){
				rgbData.pixelsarray[i] = [];
			}
			else {
				rgbData.pixelsarray[i] = default_color;
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

		rgbData.render = function(){

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
			var mCanvas = createImageFromRGBdata(this, this.width, this.height);
			mCanvas.setAttribute('style', "width:"+10*this.width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*this.height+"px;"); // make it large enough to be visible
			document.body.appendChild( mCanvas );
			/*// 2 - append data as a (saveable) image
			var mImg = newEl("img");
			var imgDataUrl = mCanvas.toDataURL();	// make a base64 string of the image data (the array above)
			mImg.src = imgDataUrl;
			mImg.setAttribute('style', "width:4px; height:4px;"); // make it large enough to be visible
			document.body.appendChild(mImg);*/
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
			
			var tile = initialize_tile({width: 7,height: 7,default_color: [0xff,0x00,0x00], x: 1, y: 2});
			tile.render();
			tile.addSample(sample,6,0);
			tile.render();
			tile.addSample(sample,6,1);
			tile.render();
			tile.addSample(sample,6,2);
			tile.render();
			tile.addSample(sample,6,3);
			tile.render();
			tile.addSample(sample,6,4);
			tile.render();
			tile.addSample(sample,6,5);
			tile.render();

			console.log("max:"+Math.ceil(7*7 / 6));



			/*var bplusTree = new com.anvesaka.bplus.BPlusTree({
				order:6,
				mergeThreshold:2
			});

			bplusTree.insert(0.12,tile);
			bplusTree.insert(0.12,tile);
			var tile = bplusTree.range(0,1);
			console.log(tile);*/

			 function pass(results, query) {
			    var target = document.getElementById('results'), html = target.innerHTML;
			    target.innerHTML = html + 'pass - ' + query + '<br />';
			  }

			  function fail(error, query) {
			    var target = document.getElementById('results'), html = target.innerHTML;
			    target.innerHTML = html + 'fail - ' + error.message + ': ' + query + '<br />';
			  }

			  var db = SQLite({ shortName: 'mydb' + parseInt(Math.random() * 100000), defaultErrorHandler: fail, defaultDataHandler: pass });

			  db.createTable('people', 'name TEXT, age INTEGER');

			  db.insert('people', { name: "Jeremy", age: 29 });
			  db.insert('people', { name: "Tara", age: 28 });

			  db.update('people', { age: 30 }, { name: 'Jeremy' });

			  db.select('people', '*', { age: 30 }, null, function (r, q) { pass(r, q); var x; for(x=0; x<r.rows.length; x++) { console.log(r.rows.item(x)); } });
			  db.select('people', 'name', null, { order: 'age DESC' }, function (r, q) { pass(r, q); var x; for(x=0; x<r.rows.length; x++) { console.log(r.rows.item(x)); } });
			  db.select('people', 'name', null, { limit: 1 }, function (r, q) { pass(r, q); var x; for(x=0; x<r.rows.length; x++) { console.log(r.rows.item(x)); } });

			  db.destroy('people', { age: 30 });
			//MAXIMUM sample needed is Math.up(width_pix*height_pix / sample_spacing)
			//
			//console.log(tile);
	});
	
});