package com.nixmash.springdata.solr.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.nixmash.springdata.solr.enums.SolrDocType;
import com.nixmash.springdata.solr.model.Product;
import com.nixmash.springdata.solr.repository.custom.CustomProductRepository;
import com.nixmash.springdata.solr.repository.simple.SimpleProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Resource
	SimpleProductRepository simpleProductRepo;

	@Resource
	CustomProductRepository productRepo;

	@Override
	public List<Product> getAvailableProducts() {
		logger.debug("Retrieving all available products");
		List<Product> products = Lists.newArrayList(productRepo.findByAnnotatedQuery("electronics", sortByIdDesc()));
		return products;
	}

	@Override
	public Iterable<Product> getAllRecords() {
		logger.debug("Retrieving all records in index");
		return simpleProductRepo.findAll();
	}

	@Override
	public Page<Product> getTestRecords() {
		return productRepo.findTestCategoryRecords();
	}

	public List<Product> getProducts() {
		logger.debug("Retrieving all records and filtering out by 'doctype:product'");
		List<Product> products = Lists.newArrayList(productRepo.findAll());
		return products.stream().filter(p -> p.getDoctype().equals(SolrDocType.PRODUCT)).collect(Collectors.toList());
	}

	public List<Product> getProductsByQuery() {
		logger.debug("Retrieving all products by solr @Query");
		return productRepo.findAllProducts();
	}

	@Override
	public Iterable<Product> getProductsByNameOrCategory(String searchTerm) {
		logger.debug("Using 'Product.findByNameOrCategory' named query - ('name:*?0* OR cat:*?0*')");
		return productRepo.findByNameOrCategory(searchTerm, sortByIdDesc());
	}

	@Override
	public List<Product> getProductsByNameOrCategoryAnnotatedQuery(String searchTerm) {
		logger.debug("Using annotated @query  - ('(name:*?0* OR cat:*?0*) AND doctype:product'");
		return productRepo.findByAnnotatedQuery(searchTerm, sortByIdDesc());
	}

	@Override
	public Page<Product> getProductsByPopularity(int popularity) {
		return productRepo.findByPopularityGreaterThanEqual(popularity, new PageRequest(0, 10));
	}

	@Override
	public Product getProduct(String Id) {
		return productRepo.findOne(Id);
	}

	@Override
	public void updateProductName(Product product) {
		productRepo.updateProductName(product);
	}

	@Override
	public List<Product> searchWithCriteria(String searchTerm) {
		return productRepo.searchWithCriteria(searchTerm);
	}

	public Sort sortByIdDesc() {
		return new Sort(Sort.Direction.DESC, Product.ID_FIELD);
	}
}