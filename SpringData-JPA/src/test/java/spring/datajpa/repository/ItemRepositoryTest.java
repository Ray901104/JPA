package spring.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.datajpa.entity.Item;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    void save() {
        Item item = new Item("A"); //persist 호출 X -> merge -> 위험!
        itemRepository.save(item);
    }
}
