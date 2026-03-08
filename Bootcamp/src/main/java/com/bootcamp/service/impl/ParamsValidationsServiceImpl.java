package com.bootcamp.service.impl;

import com.bootcamp.exception.paging.InvalidOrderByException;
import com.bootcamp.exception.paging.InvalidPageOffsetException;
import com.bootcamp.exception.paging.InvalidPageSizeException;
import com.bootcamp.exception.paging.InvalidSortByException;
import com.bootcamp.service.ParamsValidationsService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

@Service
public class ParamsValidationsServiceImpl implements ParamsValidationsService {
    private boolean validateSortBy(String sortBy,List<String> validValues){
        return validValues.stream().noneMatch(validValue -> validValue.equals(sortBy));
    }
    @Override
    public int getPageSize(String pageSize){
        int size;
        try{
            size=Integer.parseInt(pageSize);
            if(size<=0) throw new InvalidPageSizeException();
        }
        catch (NumberFormatException e){
            throw new InvalidPageSizeException();
        }
        return size;
    }

    @Override
    public String getSortByForProduct(String sortBy){
        List<String> validValues = Arrays.asList("id","created_at","updated_at","product_name","product_brand");
        if(validateSortBy(sortBy,validValues)) throw new InvalidSortByException();
        if(sortBy.equals("id")) sortBy="created_at";
        return ("p."+sortBy);
    }
    public int getPageOffset(String pageOffset){
        int offset;
        try{
            offset=Integer.parseInt(pageOffset);
            if(offset<0) throw new InvalidPageOffsetException();
        }
        catch (NumberFormatException e){
            throw new InvalidPageOffsetException();
        }
        return offset;
    }
    @Override
    public Sort.Direction getOrderBy(String orderBy){
        orderBy=orderBy.toLowerCase();
        Sort.Direction direction= Sort.Direction.ASC;
        if(orderBy.equals("asc")) direction=Sort.Direction.ASC;
        else if(orderBy.equals("desc")) direction=Sort.Direction.DESC;
        else throw new InvalidOrderByException();
        return direction;
    }
    @Override
    public String getOrderByString(String orderBy){
        orderBy=orderBy.toLowerCase();
        if(orderBy.equals("asc")) return "asc";
        else if(orderBy.equals("desc")) return "desc";
        else throw new InvalidOrderByException();
    }
    @Override
    public String getSortBy(String sortBy, List<String> validValues){
        if(validateSortBy(sortBy,validValues)) throw new InvalidSortByException();
        if(sortBy.equals("id")|| sortBy.equals("createdAt")) sortBy="auditing.createdAt";
        if (sortBy.equals("updatedAt")) sortBy = "auditing.updatedAt";
        return sortBy;
    }
}
