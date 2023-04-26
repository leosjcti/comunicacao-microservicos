package br.com.curso.productapi.modules.supplier.service;

import br.com.curso.productapi.config.exception.SuccessResponse;
import br.com.curso.productapi.config.exception.ValidationException;
import br.com.curso.productapi.modules.product.service.ProductService;
import br.com.curso.productapi.modules.supplier.dto.SupplierRequest;
import br.com.curso.productapi.modules.supplier.dto.SupplierResponse;
import br.com.curso.productapi.modules.supplier.model.Supplier;
import br.com.curso.productapi.modules.supplier.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = { @Lazy })
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Lazy
    private final ProductService productService;

    public List<SupplierResponse> findAll() {
        return supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findByName(String name) {
        if (ObjectUtils.isEmpty(name)) {
            throw new ValidationException("The supplier name must be informed.");
        }
        return supplierRepository
                .findByNameIgnoreCaseContaining(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id) {
        return SupplierResponse.of(findById(id));
    }


    public Supplier findById(Integer id) {
        validateInformedId(id);
        return supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no supllier for the given ID"));
    }

    public SupplierResponse save(SupplierRequest request) {
        validateSupplierNameInformed(request);
        var supplier = supplierRepository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request,
                                   Integer id) {
        validateSupplierNameInformed(request);
        validateInformedId(id);
        var supplier = Supplier.of(request);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public void validateSupplierNameInformed(SupplierRequest request) {
        if(ObjectUtils.isEmpty(request.getName())) {
            throw new ValidationException("The supplier description was not");
        }
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsBySupplierId(id)) {
            throw new ValidationException("You cannot delete this supplier because it's already defined by a product.");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("The supplier was deleted.");
    }

    private void validateInformedId(Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ValidationException("The supplier ID must be informed.");
        }
    }
}
