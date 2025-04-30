package com.alfonsovidrio.springboot.app.springboot_crud.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
// import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.alfonsovidrio.springboot.app.springboot_crud.entities.Product;

@Component
public class ProductValidation implements Validator{
    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;
        // ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", null, "Product name must not be empty");
        // ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "NotBlank.product.price");
        // ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotBlank.product.description");

        if (product.getName() == null || product.getName().isBlank()) {
            errors.rejectValue("name", null, "Product name must not be empty");
        } else if (product.getName().length() < 3 || product.getName().length() > 50) {
            errors.rejectValue("name", null, "Product name must be between 3 and 50 characters long");
        }

        if (product.getPrice() == null) {
            errors.rejectValue("price", null, "Product price must not be null");
        } else if (product.getPrice() < 10) {
            errors.rejectValue("price", null, "Product price must be at least 10");
        }

        if (product.getDescription() == null || product.getDescription().isBlank()) {
            errors.rejectValue("description", null, "Product description must not be empty");
        } else if (product.getDescription().length() < 10 || product.getDescription().length() > 255) {
            errors.rejectValue("description", null, "Product description must be between 10 and 255 characters long");
        }
    }
    
}
