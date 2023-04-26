package br.com.curso.productapi.modules.category.service;

import br.com.curso.productapi.config.exception.SuccessResponse;
import br.com.curso.productapi.config.exception.ValidationException;
import br.com.curso.productapi.modules.category.dto.CategoryRequest;
import br.com.curso.productapi.modules.category.dto.CategoryResponse;
import br.com.curso.productapi.modules.category.repository.CategoryRepository;
import br.com.curso.productapi.modules.category.model.Category;
import br.com.curso.productapi.modules.product.service.ProductService;
import br.com.curso.productapi.modules.supplier.model.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = { @Lazy})
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Lazy
    private final ProductService productService;

    public List<CategoryResponse> findAll() {
        return categoryRepository
                .findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public CategoryResponse findByIdResponse(Integer id) {
        return CategoryResponse.of(findById(id));
    }

    public List<CategoryResponse> findByDescription(String description) {
        if(ObjectUtils.isEmpty(description)) {
            throw  new ValidationException("The category must be informed");
        }
        return categoryRepository
                .findByDescriptionIgnoreCaseContaining(description)
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public Category findById(Integer id) {
        if(ObjectUtils.isEmpty(id)) {
            throw  new ValidationException("The category ID must be informed");
        }
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no supllier for the given ID"));
    }

    public CategoryResponse save(CategoryRequest request) {
        validateCategoryNameInformed(request);
        var category = categoryRepository.save(Category.of(request));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest request,
                                   Integer id) {
        validateCategoryNameInformed(request);
        validateInformedId(id);
        var category = Category.of(request);
        category.setId(id);
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }

    public void validateCategoryNameInformed(CategoryRequest request) {
        if(ObjectUtils.isEmpty(request.getDescription())) {
            throw new ValidationException("The category description was not");
        }
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsByCategoryId(id)) {
            throw new ValidationException("You cannot delete this category because it's already defined by a product.");
        }
        categoryRepository.deleteById(id);
        return SuccessResponse.create("The supplier was deleted.");
    }

    private void validateInformedId(Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ValidationException("The category ID must be informed.");
        }
    }


}
