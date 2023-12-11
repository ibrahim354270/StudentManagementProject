package com.project.service.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PageableHelper {

    public Pageable getPageableWithProperties(int page, int size, String sort, String type){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")){//null safe yapmış oluyoruz.
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        return pageable;
    }
}

