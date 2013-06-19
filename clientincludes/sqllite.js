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
        tx.executeSql('INSERT INTO Tiles (zoomLevel, x,y,pixelsarray) VALUES ('+tileZoomLevel+','+tilePosX+','+tilePosY+',"'+tileSerializedPixelData+'")');
        msg = '<p>Log message: inserted row.</p>';
          document.querySelector('#status').innerHTML +=  msg;
      });
  }
  return db;
}
