package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Sort;
import ru.mentor.gateway.model.SortObject;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SortMapper {

    default List<SortObject> map(Sort sort) {
        if (sort == null) {
            return null;
        }

        List<SortObject> result = new ArrayList<>();
        for (Sort.Order order : sort) {
            SortObject sortObject = new SortObject();
            sortObject.setProperty(order.getProperty());
            sortObject.setDirection(order.getDirection().name());
            result.add(sortObject);
        }
        return result;
    }

    default Sort map(List<SortObject> sortObjects) {
        if (sortObjects == null || sortObjects.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (SortObject sortObject : sortObjects) {
            Sort.Direction direction = Sort.Direction.fromString(sortObject.getDirection());
            orders.add(new Sort.Order(direction, sortObject.getProperty()));
        }
        return Sort.by(orders);
    }
}
