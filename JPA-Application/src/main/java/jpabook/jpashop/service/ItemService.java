package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    //merge 대신에 아래와 같은 변경감지기능을 이용하자
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockquantity) { //변경되는 파라미터가 너무 많으면 서비스 단에서 DTO를 따로 만들자.
        Item findItem = itemRepository.findOne(itemId); //영속상태
        //setter보다 엔티티 레벨에서 의미있는 변경 메서드를 만드는 편이 낫다.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockquantity);
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }
}
