$(function() {

	function initialize_tile(width,height,default_color){ //if num_of_samples is 1 then there is no sampling
		var tile = new Array(width*height);
		tile.width = width;
		tile.height = height;
		for (var i = 0; i < tile.length; i++){
			if (!default_color){
				tile[i] = [];
			}
			else {
				tile[i] = default_color;
			}
		}

		return tile;
	}

	function tile_addsample(tile,sample_list,sample_spacing,sample_offset){
		var counter = 0;
		var position = 0;
		var num_of_pixels = tile.width*tile.height;
		var num_of_samples = sample_list.length;
		for (var i = 0; i<num_of_samples; i++){
			if (position+sample_offset>num_of_pixels){
				console.log(counter);
				return tile;
			}
			tile[position+sample_offset]=sample_list[i];
			position += sample_spacing;
			counter++;
		}
		return tile;
	
	}


	function newEl(tag){return document.createElement(tag);}

	function createImageFromRGBdata(rgbData, width, height)
	{
		var mCanvas = newEl('canvas');
		mCanvas.width = width;
		mCanvas.height = height;
		
		var mContext = mCanvas.getContext('2d');
		var mImgData = mContext.createImageData(width, height);
		
		var srcIndex=0, dstIndex=0, curPixelNum=0;
		
		for (curPixelNum=0; curPixelNum<width*height;  curPixelNum++)
		{
			mImgData.data[dstIndex] = rgbData[srcIndex][0];		// r
			mImgData.data[dstIndex+1] = rgbData[srcIndex][1];	// g
			mImgData.data[dstIndex+2] = rgbData[srcIndex][2];	// b
			mImgData.data[dstIndex+3] = 255; // 255 = 0xFF - constant alpha, 100% opaque
			srcIndex += 1;
			dstIndex += 4;
		}
		mContext.putImageData(mImgData, 0, 0);
		return mCanvas;
	}
	 

	var rgbData = [
		[0x7f,0x7f,0x7f] // grey
	]

	/*
	var rgbData = [
		[0xff,0x00,0x00],  [0xff,0x00,0x00],  [0xff,0xff,0x00],  [0xff,0xff,0x00],// red, red, yellow, yellow
		[0xff,0x00,0x00],  [0xff,0x00,0x00],  [0xff,0xff,0x00],  [0xff,0xff,0x00],// red, red, yellow, yellow
		[0x00,0xff,0xff],  [0x00,0xff,0xff],  [0x00,0x00,0xff],  [0x00,0x00,0xff],// aquamarine, aquamarine, blue,blue
		[0x00,0xff,0xff],  [0x00,0xff,0xff],  [0x00,0x00,0xff],  [0x00,0x00,0xff] // aquamarine, aquamarine, blue, blue
	]
	*/

	var rgbData2 = []


	function render(rgbData)
	{
		// 1. - append data as a canvas element
		var mCanvas = createImageFromRGBdata(rgbData, rgbData.width, rgbData.height);
		mCanvas.setAttribute('style', "width:"+10*rgbData.width+"px; margin-left: 2px; margin-bottom:2px; height:"+10*rgbData.height+"px;"); // make it large enough to be visible
			document.body.appendChild( mCanvas );
		
		/*// 2 - append data as a (saveable) image
		var mImg = newEl("img");
		var imgDataUrl = mCanvas.toDataURL();	// make a base64 string of the image data (the array above)
		mImg.src = imgDataUrl;
		mImg.setAttribute('style', "width:4px; height:4px;"); // make it large enough to be visible
		document.body.appendChild(mImg);*/
	}


	$(document).ready(function() {
			//var flat_pixel_array = initialize(10,400,300);
			//console.log(flat_pixel_array);
			//console.log($.xcolor.average('#ff7f00','#007fff'));
			//mInit();
			var tile = initialize_tile(7,7,[0xff,0x00,0x00]);
			var sample = [[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f],[0x7f,0x7f,0x7f]]
			tile = tile_addsample(tile,sample,6,0);
			render(tile);
			tile = tile_addsample(tile,sample,6,1);
			render(tile);
			tile = tile_addsample(tile,sample,6,2);
			render(tile);
			tile = tile_addsample(tile,sample,6,3);
			render(tile);
			tile = tile_addsample(tile,sample,6,4);
			render(tile);
			tile = tile_addsample(tile,sample,6,5);
			render(tile);
			console.log("max:"+Math.ceil(7*7 / 6));



			var bplusTree = new com.anvesaka.bplus.BPlusTree({
				order:6,
				mergeThreshold:2
			});

			bplusTree.insert(0.12,tile);
			var tile = bplusTree.range(0,1);
			console.log(tile);


			//MAXIMUM sample needed is Math.up(width_pix*height_pix / sample_spacing)
			//
			//console.log(tile);
	});
	
});