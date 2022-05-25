package com.example.microservicosmarianabrito.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.microservicosmarianabrito.models.ProdutoModel;
import com.example.microservicosmarianabrito.repositories.ProdutoRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping("/produtos")
@RestController
public class ProdutoController {
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	
	@GetMapping
	public ResponseEntity<List<ProdutoModel>> getAllProdutos(){
		List<ProdutoModel> produtoList = produtoRepository.findAll();
		if(produtoList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);	
		}else {
			for(ProdutoModel produto : produtoList) {
				long id = produto.getIdProduto();
				produto.add(linkTo(methodOn(ProdutoController.class).getOneProduto(id)).withSelfRel());
			}
			return new ResponseEntity<List<ProdutoModel>>(produtoList, HttpStatus.OK);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProdutoModel> getOneProduto(@PathVariable(value="id") long id){
		Optional<ProdutoModel> produtoO = produtoRepository.findById(id);
		if(!produtoO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);			
		}else {
			produtoO.get().add(linkTo(methodOn(ProdutoController.class).getAllProdutos()).withRel("Lista de Produtos"));
			return new ResponseEntity<ProdutoModel>(produtoO.get(), HttpStatus.OK);
		}
	}
	
	@PostMapping
	public ResponseEntity<ProdutoModel> saveProduto(@RequestBody @Valid ProdutoModel produto){
		return new ResponseEntity<ProdutoModel>(produtoRepository.save(produto), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduto(@PathVariable(value="id") long id){
		Optional<ProdutoModel> produtoO = produtoRepository.findById(id);
		if(!produtoO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);			
		}else {
			produtoRepository.delete(produtoO.get());
			return new ResponseEntity<ProdutoModel>(produtoO.get(), HttpStatus.OK);
		}
	} 
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateProduto(@PathVariable(value="id") long id,
							 @Valid @RequestBody ProdutoModel produto){
		Optional<ProdutoModel> produtoO = produtoRepository.findById(id);
		if(!produtoO.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);			
		}else {
			produto.setIdProduto(produtoO.get().getIdProduto());
			return new ResponseEntity<ProdutoModel>(produtoRepository.save(produto), HttpStatus.OK);
		}
	} 
	
	
	

}
