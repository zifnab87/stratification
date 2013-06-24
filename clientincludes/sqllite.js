function tileInsert(config,dispatcher){
	var db = config["db"];
	var tile = config["tile"];
	var tileZoomLevel = tile.zoomLevel;
	var tilePosX = tile.posX;
	var tilePosY = tile.posY;
	var base64DataUrl = tile.base64DataUrl;
	db.transaction(function (tx) {
	tx.executeSql('INSERT INTO Tiles (zoomLevel, x,y,base64DataUrl) VALUES ('+tileZoomLevel+','+tilePosX+','+tilePosY+',"'+base64DataUrl+'")',[],
		function(tx,results){
			console.log("WWW"+results.insertId);
			tile.id = results.insertId;
			msg = '<p>Log message: inserted row.</p>';
			document.querySelector('#status').innerHTML +=  msg;
			dispatcher.check();
		});
	});
	
}

function tileFetchById(config,dispatcher){
	var id = config["id"];
	var db = config["db"];
	db.transaction(function (tx) {
		tx.executeSql('SELECT * FROM Tiles WHERE id='+id, [], function (tx, results) {
			var len = results.rows.length, i;
			msg = "<p>Found tile with id: " + id + "</p>";
			document.querySelector('#status').innerHTML +=  msg;
			//if (len>=1){
				row = results.rows.item(0);
				tile = extractTile(row);
				//render(tile);
				console.log("here");
				dispatcher.check();
				dispatcher.addNew({func: render,config: {tile: tile}});
			//}
		}, null);
	});
}

function tileFetchByPosition(config,dispatcher){
	var posX = config("posX");
	var posY = config("posY");
	var db = config("db");
	db.transaction(function (tx) {
		tx.executeSql('SELECT * FROM Tiles WHERE x ='+posX+' AND y='+posY, [], function (tx, results) {
			var len = results.rows.length, i;
			msg = "<p>Found tile with position: " + posX + ","+ posY + "</p>";
			document.querySelector('#status').innerHTML +=  msg;
			if (len==1){
				row = results.rows.item(0);
				var tile = extractTile(row);
			}
		}, null);
	});
}


function createDatabaseIfNotExists(dbname,dbsize){
	return openDatabase(dbname, '1.0', 'Test DB2', dbsize);
}

function createTilesTableIfNotExists(db) {
	db.transaction(function (tx) {
		tx.executeSql('CREATE TABLE IF NOT EXISTS Tiles (id INTEGER PRIMARY KEY,x,y,base64DataUrl,zoomLevel,stratPercent)');
		msg = '<p>Log message: created table.</p>';
		document.querySelector('#status').innerHTML +=  msg;
	});
}


function dropTilesTableIfExists(db) {
	db.transaction(function (tx) {
		tx.executeSql('DROP TABLE IF EXISTS Tiles',[]); 
	  	msg = '<p>Log message: dropped table.</p>';
	  	document.querySelector('#status').innerHTML +=  msg; 
	});
}

  	

function tilePixelDataUpdate(config,dispatcher){
	var db = config["db"];
	var tile = config["tile"];
	var id;
	var base64DataUrl;
	if (tile){
		id = tile.id;
		console.log(id);
		base64DataUrl = tile.base64DataUrl;
	}
	else {
		var id = config["id"];
		var base64DataUrl = config["base64DataUrl"];
	}
	db.transaction(function (tx) {
		tx.executeSql('UPDATE Tiles SET base64DataUrl = "'+base64DataUrl+'" WHERE id='+id);
		msg = '<p>Log message: updated row with id='+id+'.</p>';
		document.querySelector('#status').innerHTML +=  msg;
	});
}


$(function() {

});

	

	

	/*db.fetchAllTiles = function(extractTile,renderFlag){
		this.transaction(function (tx) {
			tx.executeSql('SELECT * FROM Tiles', [], function (tx, results) {
				var len = results.rows.length, i;
				msg = "<p>Found rows: " + len + "</p>";
				document.querySelector('#status').innerHTML +=  msg;
				for (i = 0; i < len; i++){
					row = results.rows.item(i);
					var tile = extractTile(row);
					if (renderFlag){
						tile.render();
					}
				}	
			}, null);
		});
	}
  return db;*/
