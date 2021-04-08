package ru.jeanponomarev.phonebookspringboot.converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConverter<S, D> implements Converter<S, D> {

    @Override
    public List<D> convert(List<S> sourceList) {
        return sourceList.stream().map(this::convert).collect(Collectors.toList());
    }
}
