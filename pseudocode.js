Request Format:

var request_unprocessed = { tiles: [{x:?, y: ?}, ..] }
var request_processed =  { tiles: [{ x: ?, y: ?, samples:[?,?,?,?] }, … ]  }

example:

request {
	tiles: [
		{x:32,y:231, samples[1,4]},
		{x:32,y:231, samples[1,2,3,4]} 
	]
} 

var cache; //global

function process (request_unprocessed){
	var request_processed = {};
	for (tile : request_unprocessed) {
		if (!cache.contains(tile)){
			//var server_tile = server.get(tile.x,tile.y,tile.samples);
			request_processed.tiles.push({x: tile.x,y: tile.y, samples: tile.samples})
		}
		else {
			var tile_id = hash(tile.x+""+tile.y);
			var cached_tile = cache.get(tile_id);
			var samples_of_cached_tile = cached_tile.samples;
			var samples_to_be_requested_by_server = tile.samples - cached_tile.samples;
			if (sample_to_be_requested_by_server!=empty){
				request_processed.tiles.push({x: tile.x,y: tile.y, samples_to_be_requested_by_server})
			}
		}
	}

	return request_processed;
}

function process_request(request){
	foreach tile in request:
		if cache doesnt contain tile: 
			put tile in new_request
		else:
			get tile from the cache
			remove the sample ids from request that exist in cached tile
			if samples list not:
				put the changed tile in the new_request
			else
				render tile from cache
	return new_request
}

function server_request(new_request){
	send server the request and get response
	count space needed in cache based on the number of tiles and samples of each tile
	sort tiles in cache in ascending likelihood order and ascending samples num order 
	and remove one by one till the needed space is free
	push in the cache the servers response

function get_server_response(new_request){
	send the filtered_request and get response
	return server_response
}

function free_space_in_cache(server_response){
	sort tiles in cache in ascending LOD order and ascending fragm_count order 
	calculate space needed in cache based on the number of tiles and fragments of each tile in server_repsonse
	while more space is needed:
		remove one tile and recalculate space needed by subtracting fragm_count of that tile
	push in the cache the tiles and samples of this server response

}


//possibly the server should send the response tiles compressed in base64




