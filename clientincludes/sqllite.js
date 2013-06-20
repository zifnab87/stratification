function createDatabaseIfNotExists(dbname,dbsize){
	var db = openDatabase(dbname, '1.0', 'Test DB2', dbsize);
	db.createTilesTableIfNotExists = function() {
		this.transaction(function (tx) {
			tx.executeSql('CREATE TABLE IF NOT EXISTS Tiles (id INTEGER PRIMARY KEY,x,y,pixelsArray,zoomLevel,stratPercent)');
			msg = '<p>Log message: created table.</p>';
			document.querySelector('#status').innerHTML +=  msg;
		});
	}

	db.dropTilesTableIfExists = function() {
		this.transaction(function (tx) {
			tx.executeSql('DROP TABLE IF EXISTS Tiles',[]); 
		  	msg = '<p>Log message: dropped table.</p>';
		  	document.querySelector('#status').innerHTML +=  msg; 
		});
	}

  	db.insertTileData = function (tilePosX, tilePosY, tileZoomLevel,tileSerializedPixelData){
  		this.transaction(function (tx) {
			tx.executeSql('INSERT INTO Tiles (zoomLevel, x,y,pixelsArray) VALUES ('+tileZoomLevel+','+tilePosX+','+tilePosY+',"'+tileSerializedPixelData+'")');
			msg = '<p>Log message: inserted row.</p>';
			document.querySelector('#status').innerHTML +=  msg;
		});
	}


	db.updateTilePixelDataWithId = function(id,tileSerializedPixelData){
		this.transaction(function (tx) {
			tx.executeSql('UPDATE Tiles SET pixelsArray = "'+tileSerializedPixelData+'" WHERE id='+id);
			msg = '<p>Log message: updated row with id='+id+'.</p>';
			document.querySelector('#status').innerHTML +=  msg;
			console.log("4:"+tileSerializedPixelData);
		});
	}

	db.fetchTileWithId = function(id,extractTile,renderFlag){
		this.transaction(function (tx) {
			tx.executeSql('SELECT * FROM Tiles WHERE id='+id, [], function (tx, results) {
				var len = results.rows.length, i;
				msg = "<p>Found tile with id: " + id + "</p>";
				document.querySelector('#status').innerHTML +=  msg;
				//if (len>=1){
					row = results.rows.item(0);
					tile = extractTile(row);
					if (renderFlag){
						tile.render();
					}
				//}
			}, null);
		});
	}

	db.fetchTileWithPosition = function(x,y,extractTile,renderFlag){
		this.transaction(function (tx) {
			tx.executeSql('SELECT * FROM Tiles WHERE x ='+x+' AND y='+y, [], function (tx, results) {
				var len = results.rows.length, i;
				msg = "<p>Found tile with position: " + x + ","+ y + "</p>";
				document.querySelector('#status').innerHTML +=  msg;
				if (len==1){
					row = results.rows.item(0);
					var tile = extractTile(row);
					if (renderFlag){
						tile.render();
					}
				}
			}, null);
		});
	}

	db.fetchAllTiles = function(extractTile,renderFlag){
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
  return db;
}
