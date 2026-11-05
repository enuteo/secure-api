package bes.tcc.secureapi.controller;

import bes.tcc.secureapi.dto.MyObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ObjectController {

	@GetMapping("/object/{id}")
	public ResponseEntity<MyObject> getObjectById(@PathVariable Long id) {
		return ResponseEntity.ok(new MyObject(id, null));
	}
}
